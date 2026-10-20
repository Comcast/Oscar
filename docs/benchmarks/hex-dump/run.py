# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
"""Run from the repository root after compiling production classes."""
import os
from pathlib import Path
import subprocess
import sys
import tempfile

source = Path(__file__).resolve().parent
root = source.parents[2]
classpath = str(root / 'target/classes')
with tempfile.TemporaryDirectory(prefix='oscar-hexdump-benchmark-') as directory:
    subprocess.run(['javac', '-cp', classpath, '-d', directory,
                    str(source / 'HexDumpBenchmark.java'), str(source / 'LegacyHexDump.java')], check=True)
    with Path(sys.argv[1]).open('w') as output:
        output.write('mode,bytes,round,ns_per_op,output_chars\n')
        output.flush()
        for fork in range(3):
            for size in (16, 1024, 8192):
                for mode in sys.argv[2:] or ['baseline', 'current']:
                    subprocess.run([
                        'java', '-Xms128m', '-Xmx128m', '-cp',
                        directory + os.pathsep + classpath,
                        'HexDumpBenchmark', mode, str(size),
                    ], stdout=output, check=True)
