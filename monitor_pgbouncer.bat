@echo off
setlocal enabledelayedexpansion

set OUTFILE=pgbouncer_monitor.log
echo. > %OUTFILE%

:loop
echo ===== %date% %time% ===== >> %OUTFILE%
psql -h localhost -p 6432 -U postgres pgbouncer -c "SHOW POOLS;" >> %OUTFILE% 2>&1
psql -h localhost -p 6432 -U postgres pgbouncer -c "SHOW STATS;" >> %OUTFILE% 2>&1
echo. >> %OUTFILE%
timeout /t 1 /nobreak > nul
goto loop