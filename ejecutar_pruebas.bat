@echo off
cd /d "%~dp0"
chcp 65001 > nul
echo ====================================================================
echo             EJECUTANDO PRUEBAS FUNCIONALES ECORUTA
echo ====================================================================

if not exist bin (
    mkdir bin
    javac -encoding UTF-8 -d bin src\ecoruta\modelo\*.java src\ecoruta\servicio\*.java src\ecoruta\pruebas\*.java src\ecoruta\app\*.java
)

java -Dfile.encoding=UTF-8 -cp bin ecoruta.pruebas.PruebasEcoRuta
pause