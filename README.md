# Tech Challenge - Fase 3 | Sistema de Agendamento Hospitalar

Backend simplificado e modular para gestão de consultas médicas em ambiente
hospitalar, desenvolvido para a Fase 3 do Tech Challenge (Pós Tech - Arquitetura
de Sistemas .NET/Java). O sistema é composto por dois microsserviços Spring Boot
que se comunicam de forma assíncrona via **RabbitMQ**, com autenticação/autorização
via **Spring Security** e consultas flexíveis de histórico via **GraphQL**.

## Sumário

- [Arquitetura](#arquitetura)
- [Perfis de acesso](#perfis-de-acesso)
- [Tecnologias](#tecnologias)
- [Como executar](#como-executar)
- [Usuários de teste (seed)](#usuários-de-teste-seed)
- [Endpoints REST - agendamento-service](#endpoints-rest---agendamento-service)
- [GraphQL - agendamento-service](#graphql---agendamento-service)
- [Endpoints REST - notificacao-service](#endpoints-rest---notificacao-service)
- [Fluxo assíncrono (RabbitMQ)](#fluxo-assíncrono-rabbitmq)
- [Collection Postman](#collection-postman)
- [Testes automatizados](#testes-automatizados)

## Arquitetura

```
                       ┌────────────────────┐
   HTTP / GraphQL ───► │  agendamento-service │ ───┐
   (8081)              │  (Postgres :5434)    │    │  publica evento
                       └────────────────────┘    │  (consulta.criada / consulta.editada)
                                                   ▼
                                         ┌────────────────────┐
                                         │      RabbitMQ       │
                                         │ exchange: consultas │
                                         └─────────┬──────────┘
                                                   │ consome
                                                   ▼
   HTTP        ┌────────────────────┐
   (8082) ───► │ notificacao-service │
               │  (Postgres :5435)   │
               └────────────────────┘
```

Cada serviço segue uma arquitetura em camadas (hexagonal simplificada):

- `domain`: modelos, exceções e contratos de repositório (agnósticos de framework).
- `application`: casos de uso (regras de negócio) e DTOs/mappers.
- `infrastructure`: adapters de entrada/saída — controllers REST, resolver GraphQL,
  segurança, persistência JDBC e mensageria RabbitMQ.

### Serviços

| Serviço | Porta | Banco | Responsabilidade |
|---|---|---|---|
| `agendamento-service` | 8081 | `agendamento` (Postgres :5434) | Cadastro de usuários, criação/edição de consultas, histórico via GraphQL, publica eventos no RabbitMQ |
| `notificacao-service` | 8082 | `notificacao` (Postgres :5435) | Consome eventos de consulta criada/editada e gera lembretes (notificações) para os pacientes |

## Perfis de acesso

| Perfil | Permissões |
|---|---|
| `MEDICO` | Visualiza e edita o histórico de consultas (todas) |
| `ENFERMEIRO` | Registra novas consultas e acessa o histórico (todas) |
| `PACIENTE` | Visualiza apenas as próprias consultas/histórico |

Autenticação via **HTTP Basic**, com senhas armazenadas com hash **BCrypt**.

## Tecnologias

- Java 21 + Spring Boot 3
- Spring Security (HTTP Basic + `@PreAuthorize` + method security)
- Spring for GraphQL
- Spring Data JDBC (JdbcTemplate)
- RabbitMQ (Spring AMQP)
- PostgreSQL
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito (testes unitários e de integração)
- Docker / Docker Compose

## Como executar

Pré-requisitos: Docker e Docker Compose instalados.

```bash
# na raiz do repositório
docker compose up --build
```

Isso sobe: Postgres do agendamento (5434), Postgres da notificação (5435),
RabbitMQ (5672 / painel de gerência em 15672, guest/guest), `agendamento-service`
(8081) e `notificacao-service` (8082).

- Swagger UI agendamento: http://localhost:8081/swagger-ui.html
- Swagger UI notificação: http://localhost:8082/swagger-ui.html
- GraphiQL (playground GraphQL): http://localhost:8081/graphiql
- Painel RabbitMQ: http://localhost:15672 (guest/guest)

### Executando localmente sem Docker

Cada serviço pode rodar isoladamente com `./mvnw spring-boot:run`, desde que
Postgres e RabbitMQ estejam disponíveis (ver variáveis em
`src/main/resources/application.properties` de cada serviço, todas com
defaults para `localhost`).

## Usuários de teste (seed)

O `data.sql` do `agendamento-service` já popula os seguintes usuários:

| Login | Senha | Perfil |
|---|---|---|
| `medico` | `medico123` | MEDICO |
| `enfermeiro` | `enfermeiro123` | ENFERMEIRO |
| `paciente` | `paciente123` | PACIENTE |

O `notificacao-service` usa um usuário de serviço fixo (STAFF) para consultar
os lembretes enviados: `staff` / `staff123` (configurável via
`NOTIFICACAO_USERNAME` / `NOTIFICACAO_PASSWORD`).

## Endpoints REST - agendamento-service

Base URL: `http://localhost:8081`

### Usuários

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/v1/usuarios` | Público | Cadastra médico, enfermeiro ou paciente |
| GET | `/v1/usuarios` | Autenticado | Lista usuários (paginado, filtro opcional por `nome`) |
| GET | `/v1/usuarios/{id}` | Autenticado | Busca usuário por id |

### Consultas

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/v1/consultas` | Autenticado | Médico/enfermeiro veem todas; paciente vê apenas as próprias |
| GET | `/v1/consultas/{id}` | Autenticado | Paciente só acessa a própria consulta |
| POST | `/v1/consultas` | MEDICO, ENFERMEIRO | Cria consulta (dispara evento `consulta.criada`) |
| PUT | `/v1/consultas/{id}` | MEDICO, ENFERMEIRO | Edita consulta (dispara evento `consulta.editada`) |

## GraphQL - agendamento-service

Endpoint: `POST http://localhost:8081/graphql` (autenticado via Basic Auth).

```graphql
type Consulta {
  id: ID!
  pacienteId: ID!
  medicoId: ID!
  dataHora: String!
  status: StatusConsulta!
  observacoes: String
  dataCriacao: String
  dataAtualizacao: String
}

type Query {
  historicoPorPaciente(pacienteId: ID!): [Consulta!]!
  consultasFuturasPorPaciente(pacienteId: ID!): [Consulta!]!
  consulta(id: ID!): Consulta
}
```

Exemplo de query:

```graphql
query {
  historicoPorPaciente(pacienteId: 3) {
    id
    dataHora
    status
    observacoes
  }
}
```

Pacientes só conseguem consultar o próprio `pacienteId` (regra validada nos
casos de uso, retornando erro de acesso negado caso contrário).

## Endpoints REST - notificacao-service

Base URL: `http://localhost:8082`

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/v1/notificacoes` | Autenticado (STAFF) | Lista notificações/lembretes enviados (paginado, filtro opcional `pacienteId`) |
| GET | `/v1/notificacoes/{id}` | Autenticado (STAFF) | Busca notificação por id |

Não há endpoint de criação: notificações são geradas automaticamente ao
consumir os eventos do RabbitMQ.

## Fluxo assíncrono (RabbitMQ)

1. `agendamento-service` cria (`POST /v1/consultas`) ou edita
   (`PUT /v1/consultas/{id}`) uma consulta.
2. `ConsultaEventoPublisher` publica uma mensagem no exchange `consultas`
   com routing key `consulta.criada` ou `consulta.editada`.
3. `notificacao-service` consome a fila `notificacoes` via
   `ConsultaEventoListener` (`@RabbitListener`).
4. `ProcessarLembreteConsultaUseCase` registra o lembrete no banco do
   serviço de notificação, disponível para consulta via
   `GET /v1/notificacoes`.

## Collection Postman

Arquivo: [`postman/tech-challenge-fase3.postman_collection.json`](postman/tech-challenge-fase3.postman_collection.json)

Importe a collection e a environment
[`postman/tech-challenge-fase3.postman_environment.json`](postman/tech-challenge-fase3.postman_environment.json)
no Postman. A collection já contém requisições autenticadas (Basic Auth) para
os três perfis de usuário, cobrindo:

- Cadastro de usuários (médico, enfermeiro, paciente)
- CRUD de consultas com validação de perfil
- Queries GraphQL de histórico/consultas futuras
- Consulta de notificações geradas de forma assíncrona

## Testes automatizados

Cada serviço possui testes unitários (casos de uso) e de integração
(controllers, GraphQL e listener RabbitMQ):

```bash
cd agendamento-service && ./mvnw test
cd notificacao-service && ./mvnw test
```
