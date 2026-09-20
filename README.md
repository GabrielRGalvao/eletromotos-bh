# EletroMotos BH

Sistema web para gerenciamento de ordens de serviço de uma oficina
de veículos de mobilidade elétrica.

Projeto desenvolvido para aprendizado e uso futuro na oficina.

## Tecnologias

- Java 21 e Spring Boot
- React e Vite — planejado
- PostgreSQL 17 e Docker Compose

## Executar o backend

Requisitos: Java 21 e internet na primeira execução.

```bash
cd backend
./mvnw spring-boot:run
```

Antes de iniciar o backend, configure o `.env` e inicie o PostgreSQL
conforme a seção “Banco de dados local”.

Execute o Maven Wrapper dentro da pasta `backend`, pois a aplicação
carrega o arquivo `.env` pelo caminho relativo `../.env`.

O Flyway gerencia as migrations, e o Hibernate valida a estrutura
das tabelas.

O servidor inicia em http://localhost:8080.

## Banco de dados local

1. Copie `.env.example` para `.env`.
2. Preencha `POSTGRES_PASSWORD` no `.env` antes da primeira execução.
3. Na raiz do projeto, execute:

```bash
docker compose up -d
docker compose ps
```

O banco fica disponível em `localhost:5432`.
Os dados são persistidos em um volume Docker.

Para parar o banco:

```bash
docker compose stop
```

Alterar a senha no `.env` após a criação do banco não altera
automaticamente a senha armazenada no PostgreSQL.