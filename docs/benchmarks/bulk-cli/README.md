# Bulk CLI and ZIP baseline

This checks the CLI argument dispatcher (`CommandRun`) in isolated JVMs. It does
not exercise `Main`'s banner, argument bracket rewriting, or directory bootstrap.
Each child uses the test's disposable working directory and a private SNMP cache.
No customer configuration or credentials are used.

## Repeat the checks

Routine checks use 12 files and a 2-by-3 merge:

```bash
mvn -B -Dtest=BulkCliTest test
```

The scale test uses 10,000 source files and a 100-by-100 merge, producing
10,000 outputs per conversion/merge phase. For a stable future comparison,
repeat in three independent test JVMs (Maven and children run sequentially):

```bash
for sample in 1 2 3; do
  mvn -B -DargLine=-Xmx512m -Doscar.bulk.scale=true \
    '-Dtest=BulkCliTest#tenThousandFilesTimingBaseline' test || break
done
```

Each successful scale run writes a CSV under `target/bulk-baseline/`. That CSV
contains only phase names, counts and nanosecond timings. Use seconds when
presenting results. There are no machine-speed pass/fail assertions.

## Verification

- `--bulk bin`: every output filename and byte matches an independently compiled
  reference using the public lab key.
- `-b txt`: every binary becomes text and recompiles to the reference bytes.
- `-b bin`: both exported-text recompilation and binary-to-binary copying match
  the originals, including finalized MIC bytes.
- `--mergebulkbuild` and `-mbb`: every model/tier combination appears with the
  requested `.cfg` or `.txt` extension and preserves both fragments' values.
- ZIP creation calls OSCAR's existing `Zip.ZipFile` map overload using actual
  generated binary files. The CLI itself does not expose ZIP creation. Java's
  independent ZIP reader verifies the exact entry count, every name and every
  uncompressed byte. ZIP creation and verification are timed separately.
- Small tests cover empty input, incomplete configuration rejection, and the
  known missing-directory and space-in-directory defects below.

`@TempDir(cleanup = ALWAYS)` removes test inputs, output files, ZIP archives,
child logs, caches and warm-up files on success or failure. The workload also
has explicit `finally` cleanup with an assertion that its workspace no longer
exists. Timed-out CLI children are terminated before cleanup. An externally
killed JVM or machine failure cannot guarantee cleanup.

## Timing method and limits

`System.nanoTime()` measures the CLI dispatcher inside each child, excluding
process startup. Each child first compiles 20 small synthetic files. This warms
common compilation/registry paths; it does not fully warm every export or merge
path. ZIP creation is warmed separately with up to 20 entries.

Fixture generation, independent reference compilation, validation of generated
files, and cleanup are outside the conversion timings. ZIP timings exclude
loading the binary files into the map; verification includes opening and reading
the ZIP. ZIP creation includes closing/writing each entry through the existing
OSCAR wrapper. Both the scale-test JVM and child JVMs use `-Xmx512m` in the
recorded command. No filesystem-cache flushing or CPU isolation is performed.

This is an end-to-end workload baseline, not a microbenchmark or proof of an
optimization. Compare only the same inputs, JVM flags, logging, host/storage and
measurement phases. Synthetic configurations are small: the source varies the
downstream frequency per file; merge tiers add distinct MaxCPEAllowed values.
A customer configuration with certificates or many SNMP entries may cost more.
The initial recorded baseline is one complete scale run; no variance or speedup
is claimed. Repeat samples before using it to judge an optimization.

## Confirmed existing CLI gaps

The first small test used a directory containing a space. It failed: expected
12 binary files, found zero. The CLI printed `Directory .../bulk does not exist`
and returned exit status zero. The bulk option's space value separator splits
an already-quoted argument. The large baseline therefore uses paths without
spaces. The checked-in characterization test records this defect; it is not a
claim that paths with spaces work.

A missing input directory likewise prints an error but returns zero. Tests
verify actual file counts/bytes instead of trusting exit status. These CLI
behaviors are recorded without changing production code during this baseline.

## Initial measured baseline — 2026-10-22

Production revision: `6a8cd21`, OSCAR 3.5.3 on `update-security-patches`, after
Log4j 2.26.1, Jackson BOM 2.21.8 and Zip4j 2.11.6 updates. Production sources
were unchanged during the measurement; the new test harness was uncommitted.
The small workload passed before the scale run. Additional small error-path
characterization tests were added while the already-compiled scale run continued;
these do not change its measured workload.

Environment: Ubuntu 22.04.5 LTS KVM VM, Linux 6.8.0-138-generic, eight virtual
CPUs exposed as Intel Core i7-13700HX, ext4 temporary storage, Ubuntu OpenJDK
21.0.12.1, Maven 3.6.3. Scale-test and child JVM heaps: `-Xmx512m`.
Default logging was used; DEBUG was not enabled.

Command used for this single initial sample:

```bash
mvn -B -DargLine=-Xmx512m -Doscar.bulk.scale=true \
  '-Dtest=BulkCliTest#tenThousandFilesTimingBaseline' test
```

Raw nanosecond samples: [baseline.csv](baseline.csv).

| Phase | Files | Seconds |
| --- | ---: | ---: |
| Text → binary | 10,000 | 38.846 |
| Binary → text | 10,000 | 5.304 |
| Exported text → binary | 10,000 | 39.117 |
| Binary → binary | 10,000 | 5.603 |
| ZIP creation | 10,000 entries | 23.775 |
| ZIP entry verification | 10,000 entries | 0.056 |
| Merge → binary | 100 × 100 | 4.057 |
| Merge → text | 100 × 100 | 8.278 |

The scale test passed all assertions. Maven reported 11 minutes 11 seconds for
this invocation, including fixture/reference construction, warm-up, child JVM
startup, exhaustive validation and cleanup. The generated workspace was confirmed
absent afterward. Only timing/summary records are retained. This single sample
has no measured variance and must not be presented as a speedup result.

### Additional performance follow-up

The long-lived test JVM slowed during exhaustive reference/reimport validation.
A thread sample taken during validation showed `ArrayList.addAll` called by
`TlvBuilder.updateMapTypeToByteLength` while compiling a merged text output.
Source inspection found:

- `TlvBuild.bTlvBuilder` is an interface field, hence a shared static list.
- `TlvBuilder.add(HexString)` appends byte arrays to that list.
- `updateMapTypeToByteLength(TlvBuilder)` copies the accumulated list into a
  local list whose contents are not subsequently used.

This retains data across builder instances and adds increasing copy work.
The baseline does not reset this production state or modify production code.
Investigate with a focused regression and before/after measurement separately;
do not attribute the entire observed runtime to this one stack sample.
