@echo off
setlocal enabledelayedexpansion

set URL=http://localhost:8080/pedidos
set FILE=pedido.json
set N=2000

echo ===================================
echo Teste c=1 (baseline sequencial)
echo ===================================
hey -n %N% -c 1 -m POST -D %FILE% -H "Content-Type: application/json" %URL% > resultado_c1.txt
type resultado_c1.txt | findstr /C:"Average" /C:"Requests/sec" /C:"99%%"

echo.
echo ===================================
echo Teste c=2
echo ===================================
hey -n %N% -c 2 -m POST -D %FILE% -H "Content-Type: application/json" %URL% > resultado_c2.txt
type resultado_c2.txt | findstr /C:"Average" /C:"Requests/sec" /C:"99%%"

echo.
echo ===================================
echo Teste c=4 (= threads de hardware)
echo ===================================
hey -n %N% -c 4 -m POST -D %FILE% -H "Content-Type: application/json" %URL% > resultado_c4.txt
type resultado_c4.txt | findstr /C:"Average" /C:"Requests/sec" /C:"99%%"

echo.
echo ===================================
echo Teste c=8
echo ===================================
hey -n %N% -c 8 -m POST -D %FILE% -H "Content-Type: application/json" %URL% > resultado_c8.txt
type resultado_c8.txt | findstr /C:"Average" /C:"Requests/sec" /C:"99%%"

echo.
echo ===================================
echo Teste c=16
echo ===================================
hey -n %N% -c 16 -m POST -D %FILE% -H "Content-Type: application/json" %URL% > resultado_c16.txt
type resultado_c16.txt | findstr /C:"Average" /C:"Requests/sec" /C:"99%%"

echo.
echo ===================================
echo Teste c=50 (seu teste original)
echo ===================================
hey -n 20000 -c 50 -m POST -D %FILE% -H "Content-Type: application/json" %URL% > resultado_c50.txt
type resultado_c50.txt | findstr /C:"Average" /C:"Requests/sec" /C:"99%%"

echo.
echo ===================================
echo TODOS OS TESTES CONCLUIDOS
echo Arquivos salvos: resultado_c1.txt ate resultado_c50.txt
echo ===================================