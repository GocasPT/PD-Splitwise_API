@echo off

echo Running ClientRMIT

cd ..
call  ./gradlew :ClientRMI:run --console=plain

pause