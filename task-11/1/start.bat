@echo off
chcp 65001 >nul

REM Настройки базы
set DB_NAME=hotel_db
set DB_USER=postgres
set DB_HOST=localhost
set DB_PORT=5432

set /p DB_PASSWORD=Введите пароль пользователя %DB_USER%: 

set PGPASSWORD=%DB_PASSWORD%

psql -U %DB_USER% -h %DB_HOST% -p %DB_PORT% -c "CREATE DATABASE %DB_NAME%;" 2>nul
if %ERRORLEVEL% EQU 0 (
    echo База данных создана.
) else (
    echo База данных уже существует или ошибка создания.
)

psql -U %DB_USER% -h %DB_HOST% -p %DB_PORT% -d %DB_NAME% -f ddl.sql

psql -U %DB_USER% -h %DB_HOST% -p %DB_PORT% -d %DB_NAME% -f dml.sql

pause
