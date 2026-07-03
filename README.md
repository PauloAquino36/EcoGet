# EcoGet - Sistema de Gestão Financeira Pessoal

**Autor:** Paulo Vitor Fernandes Romualdo de Aquino  
**Matrícula:** 202176014

---

## Sobre o Projeto

O **EcoGet** é uma aplicação web de gestão financeira pessoal desenvolvida com o objetivo de auxiliar os usuários a organizarem suas finanças de forma simples e eficiente. A aplicação permite o controle de receitas, despesas, metas financeiras e orçamentos, oferecendo uma visão clara da saúde financeira do usuário.

## Funcionalidades

- **Dashboard** — Visão geral das finanças com resumo de contas e transações
- **Transações** — Registro e acompanhamento de receitas e despesas
- **Contas Bancárias** — Gerenciamento de múltiplas contas
- **Metas Financeiras** — Criação e acompanhamento de metas de economia
- **Orçamentos** — Definição de limites de gastos por categoria e período
- **Relatórios** — Geração de relatórios financeiros
- **Autenticação** — Cadastro e login de usuários com segurança via JWT

## Tecnologias Utilizadas

### Back-End
- **Java 17**
- **Spring Boot 3.4.1**
- **Spring Security** (autenticação e autorização com JWT)
- **Spring Data JPA / Hibernate**
- **PostgreSQL** (banco de dados relacional)
- **Maven** (gerenciamento de dependências)

### Front-End
- **HTML5 / CSS3 / JavaScript**
- Páginas: Dashboard, Transações, Contas, Metas, Orçamentos, Cadastro

## Como Executar

### Pré-requisitos

- Java 17+
- Maven
- PostgreSQL rodando na porta `5432` com banco de dados `eco_get_db`

### Configuração do Banco de Dados

Crie o banco de dados no PostgreSQL:

```sql
CREATE DATABASE eco_get_db;
```

As credenciais padrão configuradas são:
- **Usuário:** `postgres`
- **Senha:** `postgres`

### Executando a Aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## Estrutura do Projeto

```
EcoGet/
├── src/
│   └── main/
│       ├── java/com/example/EcoGet/
│       │   ├── api/          # Controllers e DTOs
│       │   ├── Model/        # Entidades e Repositórios
│       │   ├── service/      # Regras de negócio
│       │   ├── security/     # Configuração de segurança e JWT
│       │   └── config/       # Configurações gerais
│       └── resources/
│           └── application.properties
└── frontEnd/
    ├── index.html
    ├── pages/                # Páginas da aplicação
    ├── css/                  # Estilos
    └── script/               # Scripts JavaScript
```
