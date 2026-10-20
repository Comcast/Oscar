# byteArrayToInteger benchmark

Measured 2026-10-20, before editing this method and again after recompiling.
Baseline revision: `477751e8d21da54fa3eeb2375e54e58c4cd568c5`.
Local changes at baseline: binary-mask optimization/tests and optimization QA
instructions; `byteArrayToInteger` itself was still the original implementation.
The harness retains that original method as `baseline` for future comparisons.

Environment: Linux, Intel Core i7-13700HX, OpenJDK 21.0.12.1 Ubuntu build,
`-Xms128m -Xmx128m`, default GC. No logging inside the timed method. JMH was not
available locally; this is a custom comparative harness, not a JMH result.

Each scenario uses 256 seeded, prebuilt arrays, 8 warm-up rounds, 7 measured
rounds of 200,000 operations and 3 fresh JVMs (21 samples per scenario/mode).
Results feed a checksum stored in a volatile field. Input generation is untimed.
Mode selection, array selection and checksum arithmetic remain in the timed loop.
Each consecutive group of seven rows identifies one fork for that mode/size.

`before.csv` measures production before editing; `after.csv` measures production
after editing. Both also measure the same pure ByteBuffer comparison: allocate
and left-pad short input, wrap longer input. Matching checksums verify output
consistency across runs; JUnit separately checks signs, widths, null and mutation.

| Input bytes | Before median ns/op | After median ns/op | Ratio | Sample min–max ns/op |
| --- | --- | --- | --- | --- |
| 1 | 25.77 | 9.90 | 2.60x | before: 14.58–100.94; after: 3.94–34.05 |
| 4 | 18.36 | 9.58 | 1.92x | before: 12.41–41.29; after: 3.13–33.16 |
| 1024 | 307.80 | 9.41 | 32.72x | before: 262.99–429.09; after: 2.58–36.07 |

The large-input case preserves the legacy first-four-bytes result while avoiding
copying the ignored tail. Short arrays use a shift loop; four or more bytes use
[ByteBuffer.getInt](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/ByteBuffer.html#getInt()).
Big-endian is ByteBuffer's default. Empty input remains zero.

These measurements include JVM/host noise and shared harness overhead. Ratios are
local observations, not application-wide speedups or latency guarantees. No
allocation profiler was run, so no measured memory-savings claim is made.

## Reproduce

From the repository root:

```bash
mvn -B -DskipTests compile
python3 docs/benchmarks/byte-array-to-integer/run.py /tmp/integer-benchmark.csv
mvn -B -Dtest=BinaryConversionTest test
```

The runner compares the retained baseline, current production method and pure
ByteBuffer implementation. It builds the harness in a temporary directory and
adds no runtime dependencies. The original before/after runs used identical
commands for current/builtin only; the replay runner also includes baseline.

Validation: 4 utility tests passed before the method change; the complete suite
passed afterward (545 tests). Timing is not a JUnit pass/fail threshold.
