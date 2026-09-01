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








-

# Setup do ambiente: Postgres em Docker (VirtualBox) + Testcontainers no Windows

Workspace do projeto: `C:\Users\mauro\Documents\shared\workspace\lab`

Este guia documenta o passo a passo para configurar o ambiente de desenvolvimento e testes, considerando a topologia:

**Windows 11** (aplicação Java + JUnit) → **VirtualBox** (VM Linux) → **Docker** (container Postgres)

---

## Pré-requisitos

- VirtualBox instalado no Windows 11, com uma VM Linux (Ubuntu/Debian recomendado)
- Docker instalado dentro da VM
- Projeto Java em `C:\Users\mauro\Documents\shared\workspace\lab`
- JDK e Maven/Gradle configurados no Windows

---

## Passo 1 — Configurar a rede da VirtualBox

1. Abra as configurações da VM no VirtualBox.
2. Vá em **Rede** e configure o Adaptador 1 como **Host-only Adapter** (em vez de NAT).
   - Host-only garante um IP fixo e previsível para a VM, acessível diretamente pelo Windows.
3. Inicie a VM e confirme o IP atribuído:
   ```bash
   ip addr show
   ```
4. Anote esse IP (ex.: `192.168.56.10`) — ele será usado em todas as etapas seguintes.

---

## Passo 2 — Expor o Docker daemon via TCP na VM

1. Edite (ou crie) o arquivo de configuração do Docker:
   ```bash
   sudo nano /etc/docker/daemon.json
   ```
2. Adicione o conteúdo:
   ```json
   {
     "hosts": ["unix:///var/run/docker.sock", "tcp://0.0.0.0:2375"]
   }
   ```
3. Como a distro usa systemd, crie um override para o serviço não ignorar essa configuração:
   ```bash
   sudo mkdir -p /etc/systemd/system/docker.service.d
   cat <<EOF | sudo tee /etc/systemd/system/docker.service.d/override.conf
   [Service]
   ExecStart=
   ExecStart=/usr/bin/dockerd
   EOF
   ```
4. Recarregue e reinicie o Docker:
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl restart docker
   ```

> ⚠️ **Atenção:** isso expõe o Docker sem TLS. Aceitável em rede host-only isolada de desenvolvimento, nunca em rede compartilhada ou corporativa sem TLS.

---

## Passo 3 — Subir o Postgres via docker-compose na VM

1. Dentro da VM, crie um diretório para o compose (ex.: `~/postgres-lab`):
   ```bash
   mkdir -p ~/postgres-lab && cd ~/postgres-lab
   ```
2. Crie o arquivo `docker-compose.yml`:
   ```yaml
   services:
     postgres:
       image: postgres:16
       restart: unless-stopped
       environment:
         POSTGRES_DB: lab
         POSTGRES_USER: lab_user
         POSTGRES_PASSWORD: lab_pass
       ports:
         - "5432:5432"
       volumes:
         - pgdata:/var/lib/postgresql/data
   volumes:
     pgdata:
   ```
3. Suba o container:
   ```bash
   docker compose up -d
   ```
4. Confirme que está no ar:
   ```bash
   docker compose ps
   ```

---

## Passo 4 — Testar a conexão a partir do Windows

1. No Windows, teste a conectividade com a VM:
   ```powershell
   Test-NetConnection -ComputerName 192.168.56.10 -Port 5432
   ```
2. Configure a connection string da aplicação (ex.: `application.properties`):
   ```properties
   spring.datasource.url=jdbc:postgresql://192.168.56.10:5432/lab
   spring.datasource.username=lab_user
   spring.datasource.password=lab_pass
   ```

---

## Passo 5 — Configurar o Testcontainers para o Docker remoto (opcional)

Se optar por deixar o Testcontainers gerenciar containers de teste (em vez do Postgres fixo do Passo 3):

1. No projeto (`C:\Users\mauro\Documents\shared\workspace\lab`), crie/edite `src/test/resources/testcontainers.properties`:
   ```properties
   docker.host=tcp://192.168.56.10:2375
   ```
2. Desabilite o Ryuk, já que a limpeza automática cross-host tende a falhar silenciosamente:
   - Defina a variável de ambiente:
     ```powershell
     setx TESTCONTAINERS_RYUK_DISABLED true
     ```
3. Use containers estáticos (compartilhados por classe de teste) para reduzir o número de containers criados:
   ```java
   @Testcontainers
   class PedidoRepositoryTest {

       @Container
       static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
               .withReuse(true);
   }
   ```
4. Habilite reuse globalmente, no arquivo `~/.testcontainers.properties` (na home do usuário do Windows):
   ```properties
   testcontainers.reuse.enable=true
   ```

---

## Passo 6 — Alternativa recomendada: Postgres fixo + rollback por teste

Caso o Testcontainers remoto se mostrar instável, use o Postgres fixo (Passo 3) e isole cada teste com transação e rollback:

```java
@BeforeEach
void setUp() {
    jdbcTemplate.execute("BEGIN");
}

@AfterEach
void tearDown() {
    jdbcTemplate.execute("ROLLBACK");
}
```

Vantagens:
- Elimina a complexidade de rede do Testcontainers cross-host.
- Reduz ruído de warm-up de cache/plano de execução entre testes, já que a função `fn_listar_pedidos_keyset` é `STABLE` e se beneficia de plano cacheado.

---

## Passo 7 — Validar performance da função SQL

1. Conecte via `psql` na VM (ou pelo IP a partir do Windows) e habilite timing:
   ```sql
   \timing on
   ```
2. Rode a função diretamente:
   ```sql
   SELECT fn_listar_pedidos_keyset(NULL, 20);
   ```
3. Analise o plano de execução para confirmar uso do índice do PK:
   ```sql
   EXPLAIN (ANALYZE, BUFFERS)
   SELECT * FROM fn_listar_pedidos_keyset(NULL, 20);
   ```

---

## Checklist final

- [ ] VM com IP fixo (host-only)
- [ ] Docker daemon exposto via TCP (se for usar Testcontainers remoto)
- [ ] Postgres rodando via docker-compose na VM
- [ ] Conexão testada do Windows para a VM
- [ ] Estratégia de teste definida (Testcontainers remoto **ou** Postgres fixo + rollback)
- [ ] Plano de execução da função validado com `EXPLAIN ANALYZE`







