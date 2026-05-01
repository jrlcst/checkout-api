# checkout-api context

## O que e a API

A `checkout-api` e a API orquestradora da POC. Ela recebe um `customerId`, chama `customer-api` e `billing-api`, consolida as respostas e devolve um resumo de checkout com uma decisao simples de elegibilidade.

## Para que serve

O objetivo principal do servico e demonstrar integracao entre APIs e analise de impacto contratual, retornando para o consumidor final:

- dados cadastrais do cliente
- status financeiro
- limite disponivel
- decisao `canCheckout`

## Papel no ecossistema

- Atua como servico consumidor de `customer-api` e `billing-api`.
- E o melhor ponto da POC para demonstrar quebra de contrato entre servicos.
- Serve como orquestrador central para o cenario de quality gates, Claude code review e Claude doc review.

## Visao de alto nivel do fluxo

1. O cliente chama `GET /v1/checkouts/{customerId}/summary`.
2. O `CheckoutResource` delega para o `CheckoutService`.
3. O service chama `customer-api` via `CustomerApiClient`.
4. O service chama `billing-api` via `BillingApiClient`.
5. O service calcula `canCheckout` com base em status do cliente, status do billing e limite disponivel.
	O fluxo atual considera elegivel apenas billing aprovado com `availableLimit >= 100.00`.
6. O endpoint devolve um `CheckoutSummaryResponse` consolidado.

## Entradas e saidas

### Entrada

- Metodo: `GET`
- Path: `/v1/checkouts/{customerId}/summary`
- Parametro de caminho: `customerId`

### Saida

Resposta JSON com:

- `customerId`
- `customerName`
- `customerDocument`
- `customerStatus`
- `billingStatus`
- `availableLimit`
- `canCheckout`

## Integracoes

- Integra `customer-api` via `CustomerApiClient`.
- Integra `billing-api` via `BillingApiClient`.
- Nao tem consumidores explicitamente mapeados dentro do workspace atual.
- Campos sensiveis de dependencias: `document`, `status`, `availableLimit`.

## Modelo de execucao

- Execucao sincrona via Quarkus REST.
- Sem filas, schedulers ou processamento assincrono.
- Sem persistencia local.

## Arquivos de referencia

- `src/main/java/com/jeffersonjr/checkout/resource/CheckoutResource.java`
- `src/main/java/com/jeffersonjr/checkout/service/CheckoutService.java`
- `src/main/java/com/jeffersonjr/checkout/client/CustomerApiClient.java`
- `src/main/java/com/jeffersonjr/checkout/client/BillingApiClient.java`
- `src/main/java/com/jeffersonjr/checkout/dto/CheckoutSummaryResponse.java`
- `src/test/java/com/jeffersonjr/checkout/resource/CheckoutResourceTest.java`
- `src/test/java/com/jeffersonjr/checkout/service/CheckoutServiceTest.java`
- `src/main/resources/application.properties`