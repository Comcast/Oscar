# Hexadecimal display

OSCAR's verbose hexadecimal display presents 16 bytes per row: an absolute
byte offset, uppercase hexadecimal bytes, and an aligned ASCII column.
Space through tilde are displayed as characters; other bytes appear as dots.
The final row includes ASCII even when it contains exactly 16 bytes.
Partial rows are padded to keep the ASCII column aligned.

The compact string display uses colon-separated uppercase bytes without offsets
or an ASCII column. Both displays expose configuration bytes in clear text.

## Java API

- `HexDump.dumpHexString(bytes)`: verbose display of the entire array.
- `HexDump.dumpHexString(bytes, offset, length)`: verbose display of a slice,
  retaining absolute offsets. Empty slices retain an empty offset row.
- `HexDump.dumpHexString(bytes, offset)`: compact colon-separated bytes from
  the offset to the end; offset equal to array length returns empty text.
- `HexDump.toHexString(...)`: uppercase hexadecimal without delimiters.
- `HexDump.hexStringToByteArray(text)`: accepts even-length hexadecimal text,
  either case, with no separators. Invalid input throws IllegalArgumentException.

Slices are validated before rendering. Invalid ranges throw
IndexOutOfBoundsException, null inputs throw NullPointerException, and source
arrays are not modified. Verbose output retains its leading newline and has no
trailing newline. Byte and integer conversions retain fixed widths and
big-endian order.

See [regression and benchmark results](benchmarks/hex-dump/README.md).
