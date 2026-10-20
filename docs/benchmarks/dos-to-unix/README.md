# DOS-to-Unix conversion benchmark and regression

Measured 2026-10-20 before changing `FileConversions.dosToUnix`, then again
with the same harness and settings after fixing it. Baseline revision:
`477751e8d21da54fa3eeb2375e54e58c4cd568c5`; pending BinaryConversion and QA
changes did not alter this method. The harness retains the original method.

## Correctness finding

The old method called `HexString.toAsciiBinary`, which creates binary-digit
text, not decoded text. It returned eight ASCII digit bytes per input byte and
never saw CRLF pairs. The first regression run failed three of four tests.
The fixed method removes only CR immediately followed by LF, preserves every
other byte, never changes input and always returns a fresh array. This is a
byte-level operation for ASCII-compatible encodings, not UTF-16 normalization.

JUnit covers mixed line endings, consecutive CRLF, CRCRLF, every byte value,
UTF-8, embedded zero, empty/null input, and input immutability. All 549 Java
tests passed after the fix. No performance assertion is part of JUnit.

## Measurements

OpenJDK 21.0.12.1 Ubuntu build; Linux on Intel Core i7-13700HX;
`-Xms128m -Xmx128m`, default GC. Three fresh JVMs per size, four warm-up rounds,
five measured rounds (15 samples per size). Operations per round:
`max(8, 65536 / input size)`. Sixteen prebuilt ASCII arrays contain CRLF every
16 bytes. Each result is assigned to a volatile sink. Input generation is
outside timing; loop/mode selection and volatile publication remain timed.
Consecutive groups of five CSV rows identify each fork for that size.

| Input bytes | Before median µs/op | After median µs/op | Sample min–max µs/op |
| --- | --- | --- | --- |
| 32 | 118.05 | 0.26 | before: 100.88–139.91; after: 0.18–0.72 |
| 1024 | 3760.08 | 8.02 | before: 3292.63–6433.43; after: 4.65–15.08 |
| 8192 | 29839.16 | 60.92 | before: 25473.49–37074.51; after: 37.34–95.26 |

These are costs of the old incorrect and new correct behaviors, not an
apples-to-apples speedup. The old output is eight times the input size, whereas
the new output removes CR bytes. This custom harness is not JMH; short runs and
JIT/host noise limit timing precision, especially for the faster implementation.
No memory/allocation profiler was used. Retained CSVs contain raw measurements.

## Reproduce

From the repository root, with a shaded JAR under `target/` for HexString's
existing dependencies (production classes take precedence on the classpath):

```bash
mvn -B -DskipTests package
python3 docs/benchmarks/dos-to-unix/run.py /tmp/dos-to-unix.csv
mvn -B -Dtest=FileConversionsTest test
```

The runner compares retained baseline and current code; the original before and
after captures each passed `current` as a final argument. Compilation uses a
temporary directory and introduces no new application dependencies.

## Readability simplification

The final production method uses ISO-8859-1, literal String.replace and getBytes.
ISO-8859-1 maps each byte reversibly, preserving all bytes except CR in CRLF.
This replaces the intermediate two-loop implementation at the user's request.
The harness retains `loops` and `simple` modes for future comparisons.

The same harness was run before this edit (`simplification-before.csv`, current
was loops) and after compilation (`simplification-after.csv`, current is the
three-line implementation), with `current simple` as runner arguments. Same
inputs, JVM settings, forks, warm-up and measurement counts as above.

| Input bytes | Loops median µs/op | Three-line median µs/op | Loops / three-line min–max µs/op |
| --- | --- | --- | --- |
| 32 | 0.33 | 0.42 | 0.24–1.18; 0.28–3.28 |
| 1024 | 9.55 | 9.29 | 4.86–22.87; 7.00–59.08 |
| 8192 | 57.69 | 90.79 | 39.49–815.18; 43.21–496.88 |

Retained for simpler code, not a claim of uniformly faster execution. Small-run
JIT and host variation still apply. All four FileConversions regression tests
passed after replacement, including every byte value and UTF-8 preservation.

```bash
python3 docs/benchmarks/dos-to-unix/run.py /tmp/simple-comparison.csv loops current
```
