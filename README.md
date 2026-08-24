ssh-keygen -t ed25519 -C "msergiost@hotmail.com"

eval "$(ssh-agent -s)"

ssh-add ~/.ssh/id_ed25519

ssh -T git@github.com

git init

git add .

git remote add origin git@github.com:maurotelles2000/lab.git

git commit -m "first commit"

git push -u origin main


git push --set-upstream origin Alteracao
git push -u origin Alteracao

git config --global user.name "Seu Nome Completo"
git config --global user.email "seu-email@exemplo.com"

----------------------
Configurar certificado no Ubuntu
mkdir -p ~/.ssh

chmod 700 ~/.ssh


cp id_ed25519 ~/.ssh/id_ed25519


chmod 600 ~/.ssh/id_ed25519

eval "$(ssh-agent -s)"

ssh-add ~/.ssh/id_ed25519


Configurar para ficar permanente

nano ~/.ssh/config

Copia para dentro do arquivo

Host github.com
    AddKeysToAgent yes
    IdentityFile ~/.ssh/id_ed25519
    
chmod 600 ~/.ssh/config

ssh -T git@github.com    

sudo chown -R $USER:$USER /home/mauro/shared/workspace/lab













--------


hey -n 10000 -c 100 "http://localhost:8080/pedidos/pesquisa?page=0&size=10"

hey -n 10000 -c 100 -m POST -D pedido.json -H "Content-Type: application/json" http://localhost:8080/pedidos



https://github.com/rakyll/hey

1. Teste de Pico (Spike Test)
O objetivo: Ver como o sistema reage a uma explosão repentina de tráfego pesado.

Como funciona com o ab: Como o ab não faz subida gradual (rampa), você simula o pico disparando uma concorrência agressiva de uma só vez para ver se o pool e as virtual threads engolem o tranco.

Comando para rodar:

PowerShell


./ab -n 10000 -c 500 -p pedido.json -T application/json http://localhost:8080/pedidos
(Aqui você joga 10.000 requisições de uma vez com 500 usuários simultâneos).

2. Teste de Longa Duração (Soak Test / Endurance)
O objetivo: Descobrir se há vazamento de memória (memory leak) ou lentidão acumulada ao longo do tempo.

Como funciona com o ab: O ab tem a flag -t que permite rodar o teste por tempo (em segundos) em vez de um número fixo de requisições.

Comando para rodar (ex: rodar por 5 minutos / 300 segundos):

PowerShell


./ab -t 300 -c 100 -p pedido.json -T application/json http://localhost:8080/pedidos
(Ele vai ficar disparando requisições sem parar por 5 minutos seguidos mantendo 100 conexões ativas).

3. Teste Misto: Escrita + Leitura (Mixed Workload)
O objetivo: Simular o mundo real, onde clientes criam pedidos e consultam o sistema ao mesmo tempo.

Como funciona com o ab: Como o ab foca em uma URL por comando, o truque para misturar é abrir duas janelas do terminal e rodar ambos ao mesmo tempo!

Comandos (execute os dois simultaneamente):

Terminal 1 (Gerando carga de escrita - POST):

PowerShell


./ab -t 120 -c 50 -p pedido.json -T application/json http://localhost:8080/pedidos
Terminal 2 (Gerando carga de leitura - GET):

PowerShell


./ab -t 120 -c 50 http://localhost:8080/pedidos/cliente/10
(Isso testa o banco recebendo centenas de inserts enquanto responde buscas simultâneas).

4. Teste de Ruptura (Breakpoint Test)
O objetivo: Encontrar o limite absoluto onde a sua aplicação ou o PostgreSQL começam a recusar conexões.

Como funciona com o ab: Você vai subindo o valor da concorrência (-c) degrau por degrau até aparecer algum erro em Failed requests.

Comandos em escada (suba o -c aos poucos):

Passo 1: ./ab -n 3000 -c 200 -p pedido.json -T application/json http://localhost:8080/pedidos

Passo 2: ./ab -n 3000 -c 500 -p pedido.json -T application/json http://localhost:8080/pedidos

Passo 3: ./ab -n 3000 -c 1000 -p pedido.json -T application/json http://localhost:8080/pedidos
(Monstre o terminal e o console do banco para ver onde o limite de conexões do sistema operacional ou do Postgres estoura).