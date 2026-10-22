# Optimization QA

For requested method or class optimizations, collect a speed baseline before
changing the implementation. Repeat the same measurement after the change.
This is required optimization QA, alongside deterministic correctness tests.

## Checklist

- [ ] Identify the method/class, original revision and local changes.
- [ ] Add or reuse correctness tests covering boundaries and representative data.
- [ ] Retain a runnable benchmark with synthetic small, typical and large inputs.
- [ ] Record the original timings before editing the implementation.
- [ ] Use JVM warm-up, repeated measurements and independent JVM runs.
- [ ] Consume output; exclude unrelated setup from timed work.
- [ ] Keep JDK, JVM flags, hardware, inputs and logging settings identical.
- [ ] Rerun the same benchmark after optimization.
- [ ] Report units, variability, improvements and regressions per scenario.
- [ ] Pass correctness and applicable round-trip tests after the change.
- [ ] Retain benchmark source, commands and results under
      `docs/benchmarks/<optimization-name>/`.

Prefer JMH for Java microbenchmarks when available. Benchmark dependencies must
not become application runtime dependencies. A custom harness must document its
measurement limitations. Do not add machine-speed assertions to ordinary JUnit
tests or treat a single stopwatch run as conclusive evidence.

## Results record

Record the baseline and candidate revisions, local changes, JDK, OS/CPU, JVM
flags, exact commands, input construction, warm-up, measured iterations and JVM
fork count. Include raw samples or benchmark output and a comparison table:

| Scenario / input size | Baseline time/op | Updated time/op | Change | Variability |
| --- | --- | --- | --- | --- |
| Small | | | | |
| Typical | | | | |
| Large | | | | |

State whether an improvement exceeds measurement noise. If the baseline was
reconstructed after editing, say so. Do not claim allocation or memory savings
from elapsed-time measurements alone. Keep benchmark inputs free of customer
secrets and exclude generated binaries from source control.

## Bulk CLI workload

See the [10,000-file bulk CLI and ZIP baseline](benchmarks/bulk-cli/README.md)
for opt-in scale checks, independent ZIP verification, timing boundaries,
and cleanup behavior.
