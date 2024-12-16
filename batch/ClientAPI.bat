@echo off

echo Running ClientAPI

cd ..
call  ./gradlew :ClientAPI:run --console=plain

pause