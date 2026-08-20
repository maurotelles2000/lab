#!/bin/bash

# Nome do arquivo de log onde a evolução será salva
LOG_FILE="evolucao_memoria.txt"

echo "Data/Hora | Memória Usada (MB)" > "$LOG_FILE"
echo "Iniciando monitoramento de memória. Pressione CTRL+C para parar."

# Descobre o PID do processo Java/Spring Boot automaticamente
PID=$(pgrep -f "java" | head -n 1)

if [ -z "$PID" ]; then
    # Tenta procurar caso seja um binário nativo do GraalVM (substitua 'seu-app' pelo nome do binário se necessário)
    PID=$(pgrep -f "target/" | head -n 1)
fi

if [ -z "$PID" ]; then
    echo "Erro: Não foi possível encontrar o processo da aplicação."
    exit 1
fi

echo "Monitorando o PID: $PID. Gravando em $LOG_FILE..."

# Loop infinito gravando enquanto o processo estiver rodando
while kill -0 "$PID" 2>/dev/null; do
    # Pega a memória RSS em Kilobytes e converte para Megabytes
    MEM_KB=$(ps -o rss= -p "$PID" | tr -d ' ')

    if [ -n "$MEM_KB" ]; then
        MEM_MB=$(echo "scale=2; $MEM_KB / 1024" | bc)
        TIMESTAMP=$(date "+%Y-%m-%d %H:%M:%S")

        # Escreve no terminal e no arquivo ao mesmo tempo
        echo "$TIMESTAMP | $MEM_MB MB" | tee -a "$LOG_FILE"
    fi

    sleep 2
done

echo "A aplicação foi encerrada. Log salvo em $LOG_FILE."
