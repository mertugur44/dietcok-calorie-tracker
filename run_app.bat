@echo off
setlocal

echo ==========================================
echo DietCok uygulamasi baslatiliyor...
echo ==========================================

echo.
if "%DB_PASSWORD%"=="" (
    echo UYARI: DB_PASSWORD ortam degiskeni tanimli degil.
    echo MySQL kullaniciniz parola gerektiriyorsa once su komutu calistirin:
    echo set DB_PASSWORD=parolaniz
    echo.
)

call mvnw.cmd spring-boot:run
pause
