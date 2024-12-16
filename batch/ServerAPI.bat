@echo off

echo Running ServerAPI

cd ..
call  ./gradlew :ServerAPI:bootRun --console=plain

pause