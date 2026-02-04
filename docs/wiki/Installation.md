# Installation

## Recommended

From project root:

```bash
./install.sh
```

This verifies or installs Java 21 + Maven (Linux-supported flow).
It also verifies or installs Net-SNMP (`snmptranslate`) on Linux.

## Build `oscar.jar`

```bash
./install.sh --build-oscar-jar
```

## First Run Directories
When OSCAR runs, it may initialize these folders if missing:
- `certificates/`
- `db/`
- `mibs/txt/`
