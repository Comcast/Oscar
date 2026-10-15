# Installation

## Windows PowerShell

Use PowerShell on Windows 10 or 11. Running the downloaded release does not
require Maven or a source checkout.

### Check and Install Java

Check whether PowerShell can find Java on `PATH`:

```powershell
if (Get-Command java -ErrorAction SilentlyContinue) {
    java -version
} else {
    Write-Host "Java was not found on PATH. Install Java 21 below."
}
```

Use Java 21 for Oscar. If Java is missing or an older version is selected,
install the Temurin 21 JDK:

```powershell
winget install --id EclipseAdoptium.Temurin.21.JDK --exact --source winget --accept-package-agreements --accept-source-agreements
```

Accept any Windows installer approval prompt. If `winget` is unavailable,
install it through Microsoft's
[App Installer](https://apps.microsoft.com/detail/9nblggh4nns1), or download a
[Temurin 21 Windows installer](https://adoptium.net/temurin/releases/?version=21&os=windows).
With the manual installer, enable its options to add Java to `PATH` and set
`JAVA_HOME`.

Close PowerShell and open a new window so it receives the updated `PATH`, then
check again:

```powershell
Get-Command java
java -version
```

The version output should identify Java 21. If Java is still not found or an
older version appears, check your Windows environment variables: set
`JAVA_HOME` to the installed JDK 21 folder and put `%JAVA_HOME%\bin` ahead of
older Java entries in your user `Path`. Open another PowerShell window afterward.

### Download and Run Oscar

Use a writable folder under your user profile, not `C:\Windows\system32`:

```powershell
New-Item -ItemType Directory -Force "$HOME\Oscar" | Out-Null
Set-Location "$HOME\Oscar"
Invoke-WebRequest -UseBasicParsing -Uri "https://github.com/Comcast/Oscar/releases/download/v3.0.0/oscar.jar" -OutFile ".\oscar.jar"
java -jar .\oscar.jar -v
java -jar .\oscar.jar --help
```

The version command should report `3.0.0`. You can also download `oscar.jar`
manually from the [v3.0.0 release](https://github.com/Comcast/Oscar/releases/tag/v3.0.0)
and place it in this folder. The `target/oscar-3.0.0-all.jar` path used in
source-build examples does not apply to the downloaded release.

### Compile and Decompile Lab Examples

Continue in the same folder. Download the version-matched examples and their
public lab key without editing or adding a newline to the key file:

```powershell
$base = "https://raw.githubusercontent.com/Comcast/Oscar/v3.0.0/src/com/comcast/oscar/examples/testfiles"
foreach ($name in @("DOCSIS-3.1-LAB.txt", "DOCSIS-4.0-LAB.txt", "DOCSIS-LAB.key")) {
    Invoke-WebRequest -UseBasicParsing -Uri "$base/$name" -OutFile ".\$name"
}
New-Item -ItemType Directory -Force .\output | Out-Null

java -jar .\oscar.jar -c -s d 3.1 -k .\DOCSIS-LAB.key -i .\DOCSIS-3.1-LAB.txt -o .\output\DOCSIS-3.1-LAB.bin
java -jar .\oscar.jar -c -s d4 4.0 -k .\DOCSIS-LAB.key -i .\DOCSIS-4.0-LAB.txt -o .\output\DOCSIS-4.0-LAB.bin

java -jar .\oscar.jar -d -s d 3.1 -i .\output\DOCSIS-3.1-LAB.bin
java -jar .\oscar.jar -d -s d4 4.0 -i .\output\DOCSIS-4.0-LAB.bin
```

Decompiled text is printed in the terminal. These commands use single lines,
so no PowerShell continuation backticks are needed. Quote paths containing
spaces. `-k` takes a key-file path, not a literal secret; its bytes are used
verbatim. The bundled key is public and is only for lab testing, not production.
See [CLI Examples](CLI-Examples.md) for profile details and coverage limits.

## Linux Installation

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
