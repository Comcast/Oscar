# Copyright 2026 Comcast Cable Communications Management, LLC
# SPDX-License-Identifier: Apache-2.0
"""Run from the repository root after compiling production classes."""
import os
from pathlib import Path
import subprocess
import sys
import tempfile

source = Path(__file__).resolve().parent / 'FileConversionBenchmark.java'
root = source.parents[3]
jars = list((root / 'target').glob('*-all.jar'))
if len(jars) != 1:
    raise SystemExit('Build one shaded JAR first; expected target/*-all.jar')
classpath = os.pathsep.join((str(root / 'target/classes'), str(jars[0])))
with tempfile.TemporaryDirectory(prefix='oscar-file-benchmark-') as directory:
    subprocess.run(['javac', '-cp', classpath, '-d', directory, str(source)], check=True)
    with Path(sys.argv[1]).open('w') as output:
        output.write('mode,bytes,round,ns_per_op,output_bytes\n')
        output.flush()
        for fork in range(3):
            for size in (32, 1024, 8192):
                for mode in sys.argv[2:] or ['baseline', 'current']:
                    subprocess.run([
                        'java', '-Xms128m', '-Xmx128m', '-cp',
                        directory + os.pathsep + classpath,
                        'FileConversionBenchmark', mode, str(size),
                    ], stdout=output, check=True)
