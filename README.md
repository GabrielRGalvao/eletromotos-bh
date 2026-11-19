# EletroMotos BH

Sistema web para gerenciamento de uma oficina especializada em veículos de mobilidade elétrica, com controle de clientes, veículos, agendamentos e ordens de serviço.

Projeto desenvolvido para aprendizado, evolução contínua e futura utilização na oficina.

## Funcionalidades

- Cadastro e gerenciamento de clientes.
- Cadastro e gerenciamento de veículos vinculados aos clientes.
- Cadastro e gerenciamento de mecânicos.
- Agendamento de serviços.
- Distribuição automática de agendamentos entre mecânicos por rodízio.
- Gerenciamento de ordens de serviço.
- Registro de vistorias em vídeo.
- Diagnóstico e elaboração de orçamentos.
- Aprovação de orçamentos.
- Acompanhamento da execução e conclusão dos serviços.
- Proteção do histórico de agendamentos e ordens de serviço.

## Tecnologias

**Backend**

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- Maven

**Frontend**

- React
- JavaScript
- Vite
- CSS

**Banco de dados e infraestrutura**

- PostgreSQL 17
- Flyway
- Docker
- Docker Compose
- Nginx

## Estrutura do projeto

```text
eletromotos-bh/
├── backend/
│   ├── src/
│   ├── .mvn/
│   ├── mvnw
│   └── pom.xml
├── frontend/
│   ├── src/
│   ├── package.json
│   └── package-lock.json
├── .env.example
├── compose.yaml
├── compose.prod.yaml
├── compose.server.yaml
└── README.md
```

## Requisitos

Para executar o ambiente local:

- Java 21.
- Node.js 22.12 ou superior na linha 22.
- npm.
- Docker e Docker Compose.
- Git.

## Configuração do ambiente

Na raiz do projeto, copie o arquivo de exemplo:

```bash
cp .env.example .env
```

Configure as variáveis no arquivo `.env`:

| Variável            | Descrição                 |
| ------------------- | ------------------------- |
| `POSTGRES_DB`       | Nome do banco de dados    |
| `POSTGRES_USER`     | Usuário do PostgreSQL     |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL       |
| `APP_ADMIN_NOME`    | Nome do administrador     |
| `APP_ADMIN_EMAIL`   | E-mail utilizado no login |
| `APP_ADMIN_SENHA`   | Senha utilizada no login  |
| `APP_HTTP_PORT`     | Porta HTTP da aplicação   |

Preencha as credenciais antes de iniciar a aplicação.

O arquivo `.env` contém configurações locais e credenciais e não deve ser enviado ao Git. O `.env.example` deve ser mantido versionado como modelo de configuração, sem senhas reais.

## Banco de dados local

O arquivo `compose.yaml` inicia somente o PostgreSQL.

Na raiz do projeto, execute:

```bash
docker compose up -d
```

Verifique os containers:

```bash
docker compose ps
```

Aguarde o PostgreSQL apresentar o estado `healthy`.

O banco fica disponível localmente em:

```text
localhost:5432
```

Os dados são persistidos em um volume Docker.

Para parar o banco sem remover os dados:

```bash
docker compose stop
```

Alterar a senha no `.env` após a criação do banco não altera automaticamente a senha armazenada no PostgreSQL.

## Executar o backend

Requisitos: Java 21 e acesso à internet para baixar as dependências na primeira execução.

Antes de iniciar, configure o `.env` e inicie o PostgreSQL conforme a seção anterior.

Na raiz do projeto, entre na pasta `backend`:

```bash
cd backend
```

Execute o Spring Boot pelo Maven Wrapper:

```bash
./mvnw spring-boot:run
```

O Maven Wrapper deve ser executado dentro da pasta `backend`, pois a aplicação carrega as configurações do arquivo `.env` pelo caminho relativo `../.env`.

O Flyway gerencia as migrations do banco de dados, enquanto o Hibernate valida a estrutura das tabelas.

O servidor inicia em:

http://localhost:8080

Para verificar se a API está respondendo:

http://localhost:8080/api/status

Para encerrar o backend, pressione `Ctrl + C` no terminal.

## Executar o frontend

Requisitos: Node.js 22.12 ou superior na linha 22 e npm.

Em outro terminal, partindo da raiz do projeto:

```bash
cd frontend
```

Instale as dependências:

```bash
npm install
```

Inicie o ambiente de desenvolvimento:

```bash
npm run dev
```

Abra o endereço exibido no terminal, normalmente:

http://localhost:5173

Nas próximas execuções, desde que as dependências não tenham mudado, basta executar `npm run dev`.

Para encerrar o frontend, pressione `Ctrl + C` no terminal.

## Executar a aplicação com Docker Compose

O arquivo `compose.prod.yaml` define os serviços PostgreSQL, backend e frontend.

Com o arquivo `.env` configurado, execute na raiz do projeto:

```bash
docker compose -f compose.prod.yaml up -d --build
```

Verifique os serviços:

```bash
docker compose -f compose.prod.yaml ps
```

A aplicação ficará disponível na porta definida por `APP_HTTP_PORT`, cujo valor padrão é `8081`.

Por padrão:

http://localhost:8081

Para consultar os logs:

```bash
docker compose -f compose.prod.yaml logs -f
```

Para parar os serviços sem remover os dados persistidos:

```bash
docker compose -f compose.prod.yaml down
```

Os volumes do PostgreSQL e dos vídeos são utilizados para persistência dos dados.

## Verificações

### Backend

Na pasta `backend`, execute:

```bash
./mvnw test
```

Para gerar o pacote:

```bash
./mvnw package
```

### Frontend

Na pasta `frontend`, execute:

```bash
npm run lint
npm run build
```

- `npm run lint`: verifica possíveis problemas no código.
- `npm run build`: gera os arquivos de produção na pasta `dist`.

As pastas `node_modules` e `dist` não devem ser versionadas. O arquivo `package-lock.json` deve ser mantido no Git.

## Banco de dados e migrations

As alterações na estrutura do banco são gerenciadas pelo Flyway.

As migrations ficam no backend e são executadas automaticamente durante a inicialização da aplicação.

O Hibernate valida o mapeamento das entidades com a estrutura do banco.

Não altere migrations já aplicadas em ambientes existentes. Novas alterações devem ser registradas em novas migrations.

## Segurança e configurações

- Credenciais e configurações locais devem permanecer no `.env`.
- O arquivo `.env.example` deve conter somente valores de exemplo.
- O `.env` não deve ser versionado.
- As rotas da API exigem autenticação, exceto as explicitamente públicas.
- A aplicação utiliza sessões HTTP para autenticação.

## Observações

O projeto está em desenvolvimento e sujeito a alterações de funcionalidades, estrutura e infraestrutura.

A configuração de execução local é destinada ao desenvolvimento. A configuração de produção possui serviços e parâmetros próprios.
