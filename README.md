# EletroMotos BH

Sistema web para gerenciamento de ordens de serviço de uma oficina
de veículos de mobilidade elétrica.

Projeto desenvolvido para aprendizado e uso futuro na oficina.

## Tecnologias

- Java 21 e Spring Boot
- React e Vite
- PostgreSQL 17 e Docker Compose

## Banco de dados local

Requisitos: Docker e Docker Compose instalados, com o Docker em execução.

1. Copie `.env.example` para `.env`.
2. Preencha `POSTGRES_PASSWORD` no `.env` antes da primeira execução.
3. Na raiz do projeto, execute:

```bash
docker compose up -d
docker compose ps
```

Aguarde o PostgreSQL aparecer como `healthy`.

O banco fica disponível em `localhost:5432`.
Os dados são persistidos em um volume Docker.

O arquivo `.env` contém configurações locais e não deve ser enviado ao Git.
O `.env.example` serve como modelo, sem credenciais reais.

Para parar o banco, execute na raiz do projeto:

```bash
docker compose stop
```

Alterar a senha no `.env` após a criação do banco não altera
automaticamente a senha armazenada no PostgreSQL.

## Executar o backend

Requisitos: Java 21 e internet para baixar as dependências na primeira execução.

Antes de iniciar o backend, configure o `.env` e inicie o PostgreSQL
conforme a seção “Banco de dados local”.

Partindo da raiz do projeto:

```bash
cd backend
./mvnw spring-boot:run
```

Execute o Maven Wrapper dentro da pasta `backend`, pois a aplicação
carrega o arquivo `.env` pelo caminho relativo `../.env`.

O Flyway gerencia as migrations, e o Hibernate valida a estrutura
das tabelas.

O servidor inicia em http://localhost:8080.

Para verificar se a API está respondendo, acesse:
http://localhost:8080/api/status.

Para encerrar o backend, pressione `Ctrl + C` no terminal.

## Executar o frontend

Requisitos: Node.js 22.12 ou superior na linha 22 e npm.

Em outro terminal, partindo da raiz do projeto:

```bash
cd frontend
npm install
npm run dev
```

Abra o endereço exibido no terminal, normalmente http://localhost:5173.

O comando `npm install` instala as dependências. Nas próximas execuções,
basta usar `npm run dev`, desde que as dependências não tenham mudado.

Para encerrar o frontend, pressione `Ctrl + C` no terminal.

### Verificações

Dentro da pasta `frontend`:

```bash
npm run lint
npm run build
```

- `npm run lint`: verifica possíveis problemas no código.
- `npm run build`: gera os arquivos de produção na pasta `dist`.

As pastas `node_modules` e `dist` não são versionadas.
O arquivo `package-lock.json` deve ser versionado.