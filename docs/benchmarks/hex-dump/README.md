# HexDump cleanup: regression and benchmark evidence

Baseline captured before changing `HexDump` on 2026-10-20, at revision
`b141745eecaa430241b4b98eb2d2cfb9dce5e87e`. Only the new test and benchmark files were pending then.
`LegacyHexDump.java` retains the original implementation for reproduction,
including its Android Open Source Project attribution and Apache notice.

## Correctness

The initial 13 tests produced six assertion failures and one offset exception:
nonzero offset in colon output, missing ASCII on the final complete row,
space/tilde handling, invalid-range acceptance and unclear invalid-hex errors.
After the fix, all 13 cases pass. Coverage also includes 0/1/15/16/17/32/33-byte
slices, absolute offsets, all byte values, mixed-case hex, fixed-width signed
integers, big-endian output and unchanged input.

Production uses `HexFormat` for conversions, explicit bounds checking, and one
row renderer with named column dimensions. All public overloads remain.

## Before/after measurements

OpenJDK 21.0.12.1 Ubuntu build; Linux on Intel Core i7-13700HX;
`-Xms128m -Xmx128m`, default GC. Three fresh JVMs per input size, eight warm-up
rounds and seven measured rounds (21 samples/size). Each round executes
`max(128, 65536 / size)` operations over sixteen prebuilt printable ASCII arrays.
Output is published to a volatile sink. Setup is outside timing; loop/mode
selection and publication remain timed. Consecutive groups of seven CSV rows
identify each JVM fork for that size.

| Input bytes | Before median µs/op | After median µs/op | Sample min–max µs/op |
| --- | --- | --- | --- |
| 16 | 0.40 | 0.55 | before: 0.14–2.59; after: 0.37–3.61 |
| 1024 | 33.69 | 27.31 | before: 17.75–128.46; after: 16.49–67.04 |
| 8192 | 209.94 | 111.94 | before: 186.27–259.60; after: 91.04–203.34 |

The updated output includes the previously missing final ASCII column, so this
is not an identical-output speed comparison. The small case regresses in median
time while producing the complete row. Custom harness results include JIT and
host variability; they are local observations, not application-wide speedups.
No allocation profiler was used. Raw measurements are retained in the CSVs.

## Reproduce

From the repository root:

```bash
mvn -B -DskipTests compile
python3 docs/benchmarks/hex-dump/run.py /tmp/hex-dump.csv
mvn -B -Dtest=HexDumpTest test
```

The runner compares retained baseline and current production code. Original
before/after captures passed `current` as the final runner argument, before and
after recompilation respectively. Harness classes compile in a temporary
directory; no benchmark dependency is added to the runtime artifact.

Full validation: `tools/run-all.sh` passed with 562 Java tests and 22 Python
tests. The Java-header checker also gained a reproduced-then-fixed regression
for the retained Android notice; it still rejects unknown attribution in place
of the project's required header. Both packaged JAR version checks passed.
