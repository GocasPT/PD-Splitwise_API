@echo off

set PORT=6000
set DB_FILE=./db/Server/database.db

echo Running Server with PORT: %PORT% and DB_FILE: %DB_FILE%

cd ..
call ./gradlew :Server:run --console=plain --args="%PORT% %DB_FILE%" -Dlog.level=DEBUG

pause