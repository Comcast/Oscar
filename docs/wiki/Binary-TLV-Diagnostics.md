# Inspect a damaged binary configuration

OSCAR's `TlvDisassemble.diagnose` API walks a raw DOCSIS or DPoE TLV stream
without first importing it as an editable configuration. It returns partial
structural results even when a header, length, or nested branch is damaged.
Existing disassembly and editable import behavior is unchanged.

## Java API

```java
import java.nio.file.Files;
import java.nio.file.Path;
import com.comcast.oscar.tlv.DisassemblyOptions;
import com.comcast.oscar.tlv.TlvDisassemble;

byte[] input = Files.readAllBytes(Path.of("configuration.cfg"));
var report = TlvDisassemble.diagnose(input, DisassemblyOptions.docsis());
for (var entry : report.entries()) {
    System.out.printf("%d %s %s length=%d status=%s%n",
        entry.headerOffset(), entry.tlvPath(), entry.name(),
        entry.declaredLength(), entry.status());
    // Recursively inspect entry.children() for nested TLVs.
}
```

For DPoE, supply `new DisassemblyOptions(DisassemblyOptions.Family.DPOE,
1024 * 1024, 32, 10000)`. The defaults limit input to one MiB, nesting to
32 levels, and traversal to 10000 entries. Rejection of oversized input occurs
before copying it; callers reading untrusted files should also bound file reads.
Limit markers describe uninspected remainders and may exceed the entry budget
by one per active branch. Input arrays and returned byte ranges are copied.

The family is explicit. This API does not detect modem versions or strip
PacketCable/DOCSIS 4.0 envelopes. Those formats are not supported by this initial
raw-stream API. Do not infer a family from whichever scan appears to succeed.

## Interpret the report

Each entry contains a numeric path, one-based sibling occurrence, catalog name,
absolute header/value offsets, declared length, exclusive available end offset,
status, and ordered children. A declared length of `-1` means the length field
was incomplete. Repeated entries retain their original order.

- `OK`: known entry with bounded framing and catalog length checks passed.
- `UNKNOWN_TYPE`: retained as opaque bytes; one-byte length framing is assumed
  for an unknown type in these selected families. Subsequent boundaries are
  conditional on that assumption. Its payload is not guessed to contain children.
- `TRUNCATED_HEADER` / `TRUNCATED_VALUE`: stop this branch at its parent boundary.
  Resume after the parent when that outer boundary remains available.
- `INVALID_LENGTH`: a catalog length constraint failed; the encoded boundary
  still determines where the next sibling begins. Catalog zero maximum means
  unspecified, not a requirement for an empty value.
- `DEPTH_LIMIT` / `ENTRY_LIMIT`: the marked bytes were not traversed.
- `END` / `PADDING`: top-level marker/padding bytes. Nonzero bytes after the
  end marker produce `TRAILING_DATA`; empty input produces `EMPTY_INPUT`.

`structurallyComplete()` is false if any entry is unknown, damaged, or limited.
Even true does not establish a complete deployable file: this API does not
require an end marker, validate required TLVs, decode value syntax/BER, validate
MICs, or establish that a device accepts the configuration. It deliberately
avoids the legacy value decoder so a codec failure cannot hide the raw layout.
An editable text round-trip can still fail after a successful structural scan.

## Customer data and recovery

The API never logs original bytes or decoded values. Entry descriptions contain
metadata only. `report.copyBytes(startOffset, endOffset)` explicitly retrieves
original bytes, which can include passwords, communities, certificates, and
other sensitive data. Treat those bytes and any hex output as customer data.
UI integrations must apply their masking policy before displaying or exporting
raw values; unknown payloads cannot automatically be assumed safe.

This is read-only diagnosis, not repair. It neither saves a partial configuration
nor searches arbitrary value bytes for plausible replacement headers. Keep the
original file unchanged. Use synthetic fixtures for committed regression tests.
The OSCAR-UI diagnostic dialog is a separate integration task; this release adds
the OSCAR Java API and CLI, not a new UI menu.

## Command line

```bash
java -jar oscar.jar --diagnose-binary configuration.cfg
java -jar oscar.jar --diagnose-binary configuration.cfg --diagnostic-family dpoe
```

Use these options alone; do not combine them with normal input, output, or
editing options. The command prints a monospaced tree in wire order with decimal
byte offsets, dotted paths, declared lengths, names, and statuses. It never
prints payload values or hex and does not create or modify configuration files.

The CLI stops the entire traversal at the first structural error or resource
limit, including inside a parent. It prints the failure and the remaining byte
range, rather than searching for MICs or attempting resynchronization. Unknown
TLVs with complete framing are retained and traversal continues under the stated
one-byte-length assumption. The Java API retains its recoverable default; pass
`true` as the fifth `DisassemblyOptions` argument for CLI-style stopping.

Exit codes: `0` for structurally known input, `1` for unknown entries or other
diagnostic findings, `2` for invalid arguments, unsupported family, input over
one MiB, or file read errors. A zero exit code is not MIC or semantic validation.
