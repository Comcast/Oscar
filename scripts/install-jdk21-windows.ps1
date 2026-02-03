Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if (Get-Command java -ErrorAction SilentlyContinue) {
  Write-Host "Java already installed:"
  java -version
  exit 0
}

if (Get-Command winget -ErrorAction SilentlyContinue) {
  winget install --id EclipseAdoptium.Temurin.21.JDK --source winget --accept-package-agreements --accept-source-agreements
} elseif (Get-Command choco -ErrorAction SilentlyContinue) {
  choco install temurin21 -y
} elseif (Get-Command scoop -ErrorAction SilentlyContinue) {
  scoop install temurin21-jdk
} else {
  Write-Host "No supported package manager found (winget/choco/scoop)."
  Write-Host "Install JDK 21 manually, then re-run this script."
  exit 1
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
  Write-Host "JDK install finished but java is still not on PATH."
  Write-Host "Ensure JAVA_HOME and PATH are set, then re-run."
  exit 1
}

java -version
