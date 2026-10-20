# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
"""Run from the repository root after compiling the candidate with Maven."""
from pathlib import Path
import os
import subprocess
import sys
import tempfile

source = Path(__file__).resolve().parent / 'IntegerBenchmark.java'
root = source.parents[3]
classes = root / 'target/classes'
with tempfile.TemporaryDirectory(prefix='oscar-integer-benchmark-') as directory:
    subprocess.run(['javac', '-cp', str(classes), '-d', directory, str(source)], check=True)
    with Path(sys.argv[1]).open('w') as output:
        output.write('mode,bytes,round,ns_per_op,checksum\n')
        output.flush()
        for fork in range(3):
            for size in (1, 4, 1024):
                for mode in ('baseline', 'current', 'builtin'):
                    subprocess.run([
                        'java', '-Xms128m', '-Xmx128m', '-cp',
                        directory + os.pathsep + str(classes),
                        'IntegerBenchmark', mode, str(size),
                    ], stdout=output, check=True)
