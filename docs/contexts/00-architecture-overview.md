# Architecture Overview

## Sequencia logica do servico

1. O endpoint HTTP recebe `customerId`.
2. O `CheckoutResource` chama `checkoutService.getCheckoutSummary(customerId)`.
3. O `CheckoutService` busca o cliente em `customer-api`.
4. O `CheckoutService` busca o billing em `billing-api`.
5. O service calcula `canCheckout`.
6. O service devolve o DTO consolidado.

## Componentes principais

### CheckoutResource

- Arquivo: `src/main/java/com/jeffersonjr/checkout/resource/CheckoutResource.java`
- Papel: expor o endpoint `GET /v1/checkouts/{customerId}/summary`.
- Responsabilidade: delegar a orquestracao para o service.

### CheckoutService

- Arquivo: `src/main/java/com/jeffersonjr/checkout/service/CheckoutService.java`
- Papel: orquestrar chamadas externas e aplicar a regra de elegibilidade.
- Regra: `canCheckout = true` apenas quando cliente `ACTIVE`, billing `APPROVED` e `availableLimit > 0`.

### CustomerApiClient

- Arquivo: `src/main/java/com/jeffersonjr/checkout/client/CustomerApiClient.java`
- Papel: consumir `GET /v1/customers/{id}`.

### BillingApiClient

- Arquivo: `src/main/java/com/jeffersonjr/checkout/client/BillingApiClient.java`
- Papel: consumir `GET /v1/billing/customers/{customerId}/summary`.

### CheckoutSummaryResponse

- Arquivo: `src/main/java/com/jeffersonjr/checkout/dto/CheckoutSummaryResponse.java`
- Papel: definir o contrato consolidado retornado ao consumidor.

## Configuracao principal

- `quarkus.application.name=checkout-api`
- `quarkus.http.port=8080`
- `quarkus.rest-client.customer-api.url=http://localhost:8081`
- `quarkus.rest-client.billing-api.url=http://localhost:8082`

Essas propriedades estao em `src/main/resources/application.properties`.

## Build, testes e coverage

- Build tool: Maven Wrapper
- Runtime: Quarkus 3.34.6
- Java release: 21
- Coverage: JaCoCo configurado no `pom.xml`
- Exclusoes de coverage: `**/dto/**`, `**/entity/**`, `**/config/**`, `**/client/**`

## Configuracao de CI e quality gates

- O repositorio usa o caller `.github/workflows/pr-quality.yml` para acionar o workflow reutilizavel do projeto `jrlcst/pipeline-templates`.
- O gate de SonarCloud depende das variaveis `SONAR_HOST_URL`, `SONAR_ORGANIZATION` e `SONAR_PROJECT_KEY` configuradas no GitHub.
- Para o `checkout-api`, os valores esperados sao `https://sonarcloud.io`, `jeffersonpersonalsonar` e `jrlcst_checkout-api`.
- O gate de Sonar tambem depende do secret `SONAR_TOKEN`.
- As etapas `doc-review` e `code-review-ai` dependem do secret `ANTHROPIC_API_KEY`.
- A notificacao final de Slack depende do secret `SLACK_WEBHOOK_URL`.
- No SonarCloud, `Automatic Analysis` deve permanecer desabilitado para o projeto ser analisado pelo pipeline de CI.

## Endpoints principais

- `GET /v1/checkouts/{customerId}/summary`

## Integracoes e dependencias externas

- `customer-api`
- `billing-api`
- Nao ha banco, cache ou mensageria.

## Onde isso aparece no codigo

- Entrada HTTP: `src/main/java/com/jeffersonjr/checkout/resource/CheckoutResource.java`
- Regra de negocio e tratamento de falhas: `src/main/java/com/jeffersonjr/checkout/service/CheckoutService.java`
- Contratos externos: `src/main/java/com/jeffersonjr/checkout/client/*.java`
- Contrato de resposta: `src/main/java/com/jeffersonjr/checkout/dto/CheckoutSummaryResponse.java`
- Validacao do comportamento: `src/test/java/com/jeffersonjr/checkout/resource/CheckoutResourceTest.java` e `src/test/java/com/jeffersonjr/checkout/service/CheckoutServiceTest.java`