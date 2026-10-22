# OpenJDK ZIP migration baseline

Measured 2026-10-22, before and after removing Zip4j 2.11.6 from OSCAR.
The production baseline was revision `20fa404` on `update-security-patches`;
the candidate used the same revision plus the uncommitted OpenJDK ZIP migration.

## Reproduce

```bash
mvn -B -DargLine=-Xmx512m -Doscar.zip.scale=true -Dtest=ZipTest test
```

`tenThousandEntryBaseline` creates a map of 10,000 entries named
`config-<index>.bin`. Each entry is 256 bytes; byte `offset` is `(byte)
(index + offset)`. Each of three timed iterations creates a fresh archive using
OSCAR's map overload. Input preparation and verification are outside timing.
The writer is warmed with one 256-byte entry first. The JDK reader verifies all
10,000 names and payloads after each iteration. Archives are immediately deleted;
JUnit temporary-directory cleanup also runs on failure. Only CSV measurements
remain under `target/zip-baseline/`.

The tests were first run against Zip4j, including the new append/replacement
and empty-map checks, before editing production code. The same benchmark and
flags were then rerun with the JDK writer. The comparison uses synthetic data,
not customer files, and differs from the earlier full bulk-CLI workload.

## Environment and results

Ubuntu 22.04.5 LTS KVM VM, Linux 6.8.0-138-generic, eight virtual CPUs presented
as Intel Core i7-13700HX, ext4 temporary storage, Ubuntu OpenJDK 21.0.12.1,
Maven 3.6.3, test JVM `-Xmx512m`, default logging without DEBUG enabled.

| Iteration | Zip4j seconds | OpenJDK seconds |
| --- | ---: | ---: |
| 1 | 25.107 | 0.771 |
| 2 | 23.849 | 0.724 |
| 3 | 22.707 | 0.757 |

Both implementations produced 3,827,802-byte archives for this input. Correctness
compares uncompressed payloads, not whole ZIP bytes (timestamps can differ).
Raw results: [before.csv](before.csv), [after.csv](after.csv).

There is one independent JVM invocation per implementation, with three iterations
inside each JVM. There is no CPU isolation or filesystem-cache reset. These are
observed workload results, not a universal throughput guarantee or allocation
measurement. Use additional independent JVM runs for a stronger comparison.

The old map wrapper reopened/updated the archive for every entry. The JDK writer
opens a single output stream, writes all entries and replaces the destination
only after completing the archive. Existing archives require one rewrite to
preserve unrelated entries; new archive creation needs no existing-entry copy.
