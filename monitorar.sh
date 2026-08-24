#!/bin/bash

LOG="consumo_carga.log"
> "$LOG" # Limpa o log anterior

echo "=== INÍCIO DO TESTE DE CARGA ===" | tee -a "$LOG"

# 1. Inicia o monitoramento em segundo plano e guarda o PID dele
while true; do
    echo "=== $(date) ===" >> "$LOG"
    ps -eo %cpu,%mem,comm | grep "postgres" | awk '{cpu+=$1; mem+=$2} END {printf "Postgres Total -> CPU: %.1f%% | MEM: %.1f%%\n", cpu, mem}' >> "$LOG"
    ps -eo %cpu,%mem,comm | grep "lab" | awk '{cpu+=$1; mem+=$2} END {printf "Lab Total      -> CPU: %.1f%% | MEM: %.1f%%\n", cpu, mem}' >> "$LOG"
    echo "" >> "$LOG"
    sleep 1
done &
MONITOR_PID=$!

# 2. Roda o hey (substitua pelos seus parâmetros de URL, requisições e concorrência)
# Exemplo: 1000 requisições total, 50 concorrentes
echo "Executando o hey..."
hey -n 10000 -c 100 -m POST -D pedido.json -H "Content-Type: application/json" http://localhost:8080/pedidos
hey -n 10000 -c 100 "http://localhost:8080/pedidos/pesquisa?page=0&size=10"


# 3. Para o monitoramento assim que o hey terminar
kill $MONITOR_PID 2>/dev/null

echo "=== FIM DO TESTE DE CARGA ===" | tee -a "$LOG"