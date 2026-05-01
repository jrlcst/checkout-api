# checkout-api

Microserviço responsável por orquestrar dados de cliente e billing para retornar um resumo simples de checkout na POC de quality gates, AI review e revisão de documentação no pipeline.

## Contextos e fluxos

- Contexto funcional e arquitetural: [docs/contexts/checkout-api-context.md](docs/contexts/checkout-api-context.md)
- Visão de arquitetura e configuração: [docs/contexts/00-architecture-overview.md](docs/contexts/00-architecture-overview.md)
- Fluxo principal: [docs/flows/01-resumo-de-checkout-orquestrado.md](docs/flows/01-resumo-de-checkout-orquestrado.md)
- Fluxo secundário: [docs/flows/02-tratamento-de-elegibilidade-e-falhas-externas.md](docs/flows/02-tratamento-de-elegibilidade-e-falhas-externas.md)

## Contexto para IA e revisão automatizada

- Contexto estruturado para Claude code review e doc review: [docs/ai-context.yaml](docs/ai-context.yaml)
- Skill local de Claude para code review: [.claude/skills/claude-code-review/SKILL.md](.claude/skills/claude-code-review/SKILL.md)
- Skill local de Claude para doc review: [.claude/skills/claude-doc-review/SKILL.md](.claude/skills/claude-doc-review/SKILL.md)
- Mudanças em endpoint, DTO, client, regra de elegibilidade, configuração ou documentação devem refletir neste README, no `docs/ai-context.yaml` e nos skills locais de `.claude` quando a orientação de review mudar.

## O que o serviço faz

1. Expõe o endpoint `GET /v1/checkouts/{customerId}/summary`.
2. Consome `customer-api` para buscar dados cadastrais.
3. Consome `billing-api` para buscar status financeiro e limite disponível.
4. Calcula `canCheckout` como `true` apenas quando cliente está `ACTIVE`, billing está `APPROVED` e `availableLimit > 0`.
5. Propaga `404` quando cliente ou billing não existem e converte falhas inesperadas de integração em `502`.

## Stack técnica

- Linguagem: Java 21
- Build: Maven Wrapper
- Framework HTTP e runtime: Quarkus 3.34.6
- Persistência: não utiliza banco; apenas orquestra integrações HTTP
- Mensageria: não utiliza
- Integrações HTTP: MicroProfile Rest Client com `customer-api` e `billing-api`
- Testes: JUnit 5, Mockito, Rest Assured e Quarkus Test
- Coverage: JaCoCo com exclusões de `dto`, `entity`, `config` e `client` para análise de cobertura

## Modelo de execução

- Serviço síncrono via HTTP REST.
- Orquestração concentrada em `CheckoutService`.
- Dependência direta de duas APIs internas da POC.

## Entrada e saída da API

### Endpoint principal

- `GET /v1/checkouts/{customerId}/summary`

Exemplo de resposta:

```json
{
  "customerId": "cus-001",
  "customerName": "Maria Silva",
  "customerDocument": "12345678900",
  "customerStatus": "ACTIVE",
  "billingStatus": "APPROVED",
  "availableLimit": 1500.00,
  "canCheckout": true
}
```

### Regras de retorno

- Retorna `200` com `canCheckout = true` quando `customerStatus = ACTIVE`, `billingStatus = APPROVED` e `availableLimit > 0`.
- Retorna `200` com `canCheckout = false` quando o cliente está bloqueado ou o billing está rejeitado.
- Retorna `404` quando `customer-api` ou `billing-api` respondem ausência do recurso esperado.
- Retorna `502` quando uma dependência externa falha de forma inesperada.

## Erros e validação

- A regra de elegibilidade está em `CheckoutService.canCheckout(...)`.
- O serviço trata `NotFoundException` das APIs externas como ausência de cliente ou billing para checkout.
- O serviço traduz falhas inesperadas de integração em `WebApplicationException` com status `502`.
- O endpoint não recebe payload; o fluxo é orientado por `customerId` no path.

## Integrações

- Consome `customer-api` em `GET /v1/customers/{id}`.
- Consome `billing-api` em `GET /v1/billing/customers/{customerId}/summary`.
- Não usa filas, banco ou cache.

## Impacto documental e revisão automatizada

- Mudanças em `customerDocument`, `customerStatus`, `billingStatus`, `availableLimit`, `canCheckout`, clients HTTP ou regra de elegibilidade podem quebrar o comportamento esperado da POC.
- Mudanças em `resource`, `service`, `client`, `dto`, `application.properties`, `pom.xml`, `docs` ou `.claude` devem revisar a documentação local e a orientação de review automatizado quando necessário.

## Como subir localmente

### Pré-requisitos

- Java 21
- Maven Wrapper disponível no repositório
- `customer-api` disponível em `http://localhost:8081`
- `billing-api` disponível em `http://localhost:8082`

### Dev mode

```bash
./mvnw quarkus:dev
```

O serviço sobe por padrão na porta `8080`.

### Build

```bash
./mvnw package
```

## Testes

```bash
./mvnw test
```

## Arquivos úteis para exploração

- `src/main/java/com/jeffersonjr/checkout/resource/CheckoutResource.java`
- `src/main/java/com/jeffersonjr/checkout/service/CheckoutService.java`
- `src/main/java/com/jeffersonjr/checkout/client/CustomerApiClient.java`
- `src/main/java/com/jeffersonjr/checkout/client/BillingApiClient.java`
- `src/main/java/com/jeffersonjr/checkout/dto/CheckoutSummaryResponse.java`
- `src/test/java/com/jeffersonjr/checkout/resource/CheckoutResourceTest.java`
- `src/test/java/com/jeffersonjr/checkout/service/CheckoutServiceTest.java`
- `src/main/resources/application.properties`
- `pom.xml`
# pipeline-templates
# pipeline-templates
# pipeline-templates
