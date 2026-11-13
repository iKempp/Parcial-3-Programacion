@echo off
cd /d "%~dp0"
chcp 65001 > nul
echo ====================================================================
echo             COMPILANDO PROYECTO ECORUTA EN JAVA 17+
echo ====================================================================

if not exist bin mkdir bin

javac -encoding UTF-8 -d bin src\ecoruta\modelo\*.java src\ecoruta\servicio\*.java src\ecoruta\pruebas\*.java src\ecoruta\app\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] La compilacion fallo. Revise que Java JDK 17+ este instalado y en el PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo [OK] Compilacion exitosa.
echo ====================================================================
echo             EJECUTANDO APLICACION INTERACTIVA ECORUTA
echo ====================================================================
java -Dfile.encoding=UTF-8 -cp bin ecoruta.app.Main
pause