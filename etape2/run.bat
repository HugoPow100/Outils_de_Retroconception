@echo off
REM Script pour compiler le projet avec architecture MVC

REM Se placer dans le dossier du script
cd /d "%~dp0"

REM Compiler tous les fichiers Java avec les packages
echo Compilation du projet...
javac -d bin src\metier\*.java

if %errorlevel% equ 0 (
    echo. 
    echo √ Compilation reussie
) else (
    echo.
    echo X Erreur de compilation
    exit /b 1
)
@echo off
REM Script pour executer Retroconception avec architecture MVC

REM Se placer dans le dossier du script
cd /d "%~dp0"

REM Verifier si un argument est fourni
if "%~1"=="" (
    echo Usage: run.bat ^<fichier.java ou dossier^>
    exit /b 1
)

REM Executer le programme
java metier.Retroconception %*
