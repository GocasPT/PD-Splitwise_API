@echo off

set IP=localhost
set PORT=6000

echo Running Client with IP: %IP% and PORT: %PORT%

cd ..
call  ./gradlew :Client:run --console=plain --args="%IP% %PORT%"

pause