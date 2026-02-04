# Template Merge Bulk Build

Merge multiple template directories into one output set.

## Example
```bash
java -jar oscar.jar -s d 3 -mbb InputDir1 InputDir2 InputDir3 o=OutputDir e=.bin b
```

## Notes
- `o=` output dir is optional
- `e=` output extension is optional
- final mode flag: `b` (binary) or `t` (text)
