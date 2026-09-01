# Setup do ambiente: Postgres em Docker (VirtualBox) + Testcontainers no Windows
### Guia passo a passo para quem não tem experiência com Linux

Workspace do projeto: `C:\Users\mauro\Documents\shared\workspace\lab`

Topologia: **Windows 11** (aplicação Java + JUnit) → **VirtualBox** (VM Linux) → **Docker** (container Postgres)

> 💡 Cada comando abaixo deve ser digitado dentro do terminal da VM Linux (não no PowerShell do Windows), a menos que esteja explicitamente indicado.

---

## Passo 1 — Abrir o terminal dentro da VM

1. Ligue a VM no VirtualBox e faça login normalmente.
2. Abra o **Terminal**:
   - No Ubuntu/Debian com interface gráfica: pressione `Ctrl + Alt + T`, ou procure "Terminal" no menu de aplicativos.
3. Você verá uma linha parecida com esta, esperando comandos:
   ```
   usuario@nome-da-vm:~$
   ```
4. Esse `~` significa que você está na sua pasta pessoal (home).

---

## Passo 2 — Descobrir o IP da VM

1. Digite o comando abaixo e pressione **Enter**:
   ```bash
   ip addr show
   ```
2. Esse comando lista as interfaces de rede. Procure um bloco parecido com:
   ```
   3: enp0s8: <BROADCAST,MULTICAST,UP,LOWER_UP> ...
       inet 192.168.56.10/24 ...
   ```
3. O número depois de `inet` (ex.: `192.168.56.10`) é o IP da VM. **Anote esse valor** — vamos usá-lo depois no Windows.

> Se aparecer mais de uma interface com `inet`, procure a que começa com `192.168.56.x` — essa costuma ser a rede **Host-only** configurada no VirtualBox.

### Se a VM ainda não tem rede Host-only configurada

1. Desligue a VM (feche a janela ou `sudo shutdown now` dentro dela).
2. No VirtualBox, clique com o botão direito na VM → **Configurações**.
3. Vá em **Rede** → aba **Adaptador 1**.
4. Em "Conectado a", troque para **Adaptador Host-only**.
5. Clique OK e ligue a VM novamente.
6. Repita o Passo 2 para pegar o novo IP.

---

## Passo 3 — Verificar se o Docker já está instalado

1. Digite:
   ```bash
   docker --version
   ```
2. **Se aparecer uma versão** (ex.: `Docker version 24.0.5`), o Docker já está instalado — pule para o **Passo 4**.
3. **Se aparecer "comando não encontrado"**, instale o Docker seguindo os comandos abaixo, um de cada vez:

   ```bash
   sudo apt update
   ```
   > Isso atualiza a lista de pacotes disponíveis. Pode pedir sua senha — digite e pressione Enter (a senha não aparece na tela, é normal).

   ```bash
   sudo apt install -y docker.io
   ```
   > Instala o Docker. Aguarde terminar.

   ```bash
   sudo systemctl enable --now docker
   ```
   > Garante que o Docker inicie junto com o sistema e já liga o serviço agora.

   ```bash
   sudo usermod -aG docker $USER
   ```
   > Adiciona seu usuário ao grupo docker, para não precisar de `sudo` em todo comando docker.

4. **Saia e entre de novo na VM** (logout/login, ou reinicie a VM) para que a permissão do grupo tenha efeito:
   ```bash
   sudo reboot
   ```

5. Após reiniciar, teste novamente:
   ```bash
   docker --version
   docker run hello-world
   ```
   Se aparecer uma mensagem dizendo que o Docker está funcionando corretamente, está tudo certo.

---

## Passo 4 — Expor o Docker daemon via rede (para o Windows conseguir acessar)

Isso permite que o Testcontainers (rodando no Windows) converse com o Docker que está dentro da VM.

1. Abra o arquivo de configuração do Docker usando o editor de texto `nano` (simples, roda no terminal):
   ```bash
   sudo nano /etc/docker/daemon.json
   ```
2. Se o arquivo abrir vazio, digite exatamente isto:
   ```json
   {
     "hosts": ["unix:///var/run/docker.sock", "tcp://0.0.0.0:2375"]
   }
   ```
3. **Salvar e sair do nano:**
   - Pressione `Ctrl + O` (a letra O, de "Output") para salvar.
   - Pressione `Enter` para confirmar o nome do arquivo.
   - Pressione `Ctrl + X` para sair do editor.

4. Agora crie uma pasta de configuração extra para o serviço do Docker:
   ```bash
   sudo mkdir -p /etc/systemd/system/docker.service.d
   ```

5. Crie o arquivo de override, executando este bloco inteiro de uma vez (copie e cole tudo junto no terminal):
   ```bash
   cat <<EOF | sudo tee /etc/systemd/system/docker.service.d/override.conf
   [Service]
   ExecStart=
   ExecStart=/usr/bin/dockerd
   EOF
   ```
   > Esse comando cria o arquivo automaticamente, sem precisar abrir editor.

6. Recarregue as configurações do systemd e reinicie o Docker:
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl restart docker
   ```

7. Confirme que o Docker está escutando na porta 2375:
   ```bash
   sudo ss -tlnp | grep 2375
   ```
   Se aparecer uma linha mencionando `2375`, deu certo.

> ⚠️ **Atenção:** isso deixa o Docker acessível pela rede sem senha (sem TLS). Só faça isso em rede **Host-only** isolada, nunca em rede Wi-Fi compartilhada ou corporativa.

---

## Passo 5 — Criar o Postgres com docker-compose

1. Crie uma pasta para guardar os arquivos do Postgres:
   ```bash
   mkdir -p ~/postgres-lab
   cd ~/postgres-lab
   ```
   > `mkdir -p` cria a pasta, e `cd` entra nela.

2. Crie o arquivo de configuração do compose:
   ```bash
   nano docker-compose.yml
   ```
3. Cole exatamente este conteúdo:
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
4. Salve e saia (mesmo atalho de antes: `Ctrl + O`, `Enter`, `Ctrl + X`).

5. Verifique se o comando `docker compose` existe:
   ```bash
   docker compose version
   ```
   - Se não existir, instale o plugin:
     ```bash
     sudo apt install -y docker-compose-plugin
     ```

6. Suba o container do Postgres:
   ```bash
   docker compose up -d
   ```
   > O `-d` faz rodar em segundo plano (não trava o terminal).

7. Confirme que está rodando:
   ```bash
   docker compose ps
   ```
   Deve aparecer uma linha com o status `running` ou `Up`.

---

## Passo 6 — Testar a conexão a partir do Windows

Agora volte para o **Windows** (PowerShell).

1. Abra o PowerShell no Windows.
2. Teste se consegue "ver" a VM na porta do Postgres (troque pelo IP anotado no Passo 2):
   ```powershell
   Test-NetConnection -ComputerName 192.168.56.10 -Port 5432
   ```
3. Se aparecer `TcpTestSucceeded : True`, a conexão está funcionando.

4. Configure a aplicação Java em `C:\Users\mauro\Documents\shared\workspace\lab`, no arquivo `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://192.168.56.10:5432/lab
   spring.datasource.username=lab_user
   spring.datasource.password=lab_pass
   ```

---

## Passo 7 — Testcontainers apontando para o Docker da VM (opcional)

Se quiser que o JUnit suba containers de teste automaticamente via Testcontainers:

1. No projeto (`C:\Users\mauro\Documents\shared\workspace\lab`), crie o arquivo:
   ```
   src\test\resources\testcontainers.properties
   ```
2. Com o conteúdo (troque pelo IP da VM):
   ```properties
   docker.host=tcp://192.168.56.10:2375
   ```
3. No **PowerShell do Windows**, desative o Ryuk (evita falha silenciosa de limpeza cross-host):
   ```powershell
   setx TESTCONTAINERS_RYUK_DISABLED true
   ```
   > Feche e abra o PowerShell/IDE de novo para a variável ter efeito.

4. No código de teste Java, use containers estáticos com reuse:
   ```java
   @Testcontainers
   class PedidoRepositoryTest {

       @Container
       static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
               .withReuse(true);
   }
   ```

5. Habilite reuse globalmente. No Windows, crie/edite o arquivo:
   ```
   C:\Users\mauro\.testcontainers.properties
   ```
   Com o conteúdo:
   ```properties
   testcontainers.reuse.enable=true
   ```

---

## Passo 8 — Alternativa mais simples e estável: Postgres fixo + rollback por teste

Se a configuração do Testcontainers remoto (Passo 7) causar instabilidade, use o Postgres do Passo 5 direto, e isole cada teste com transação:

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

Vantagem: elimina toda a complexidade de rede do Testcontainers cross-host, e ainda reduz ruído de warm-up de cache/plano de execução entre testes.

---

## Passo 9 — Validar a performance da função SQL

Ainda na VM (terminal), conecte no Postgres usando o cliente `psql`:

1. Verifique se o `psql` está instalado:
   ```bash
   psql --version
   ```
   Se não estiver:
   ```bash
   sudo apt install -y postgresql-client
   ```

2. Conecte no banco dentro do container:
   ```bash
   docker exec -it postgres-lab-postgres-1 psql -U lab_user -d lab
   ```
   > O nome do container pode variar; confira com `docker compose ps`.

3. Dentro do `psql`, ative a medição de tempo:
   ```sql
   \timing on
   ```
4. Rode a função:
   ```sql
   SELECT fn_listar_pedidos_keyset(NULL, 20);
   ```
5. Veja o plano de execução:
   ```sql
   EXPLAIN (ANALYZE, BUFFERS)
   SELECT * FROM fn_listar_pedidos_keyset(NULL, 20);
   ```
6. Para sair do `psql`:
   ```sql
   \q
   ```

---

## Checklist final

- [ ] Terminal da VM aberto e IP da VM identificado (`ip addr show`)
- [ ] Rede da VM configurada como Host-only
- [ ] Docker instalado e funcionando (`docker run hello-world`)
- [ ] Docker exposto via TCP na porta 2375 (se for usar Testcontainers remoto)
- [ ] Postgres rodando via `docker compose up -d`
- [ ] Conexão testada do Windows (`Test-NetConnection`)
- [ ] Estratégia de teste definida (Testcontainers remoto **ou** Postgres fixo + rollback)
- [ ] Plano de execução validado com `EXPLAIN ANALYZE`
