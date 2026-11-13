[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "             COMPILANDO PROYECTO ECORUTA EN JAVA 17+                " -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = (Get-ChildItem -Path src -Recurse -Filter *.java).FullName
javac -encoding UTF-8 -d bin $sources

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] La compilación falló." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[OK] Compilación exitosa." -ForegroundColor Green
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "             EJECUTANDO APLICACIÓN INTERACTIVA ECORUTA              " -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan

java -Dfile.encoding=UTF-8 -cp bin ecoruta.app.Main
