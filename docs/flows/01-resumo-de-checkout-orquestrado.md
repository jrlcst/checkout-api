# 01 - Resumo de checkout orquestrado

## Objetivo do fluxo

Atender uma consulta sincronizada de resumo de checkout, consolidando dados de cliente e billing em uma unica resposta.

## Sequencia ponta a ponta

1. O cliente chama `GET /v1/checkouts/{customerId}/summary`.
2. O `CheckoutResource` recebe o `customerId`.
3. O resource delega para `CheckoutService.getCheckoutSummary(customerId)`.
4. O service consulta `customer-api`.
5. O service consulta `billing-api`.
6. O service calcula `canCheckout`.
7. O endpoint devolve `200` com o `CheckoutSummaryResponse`.

## Entradas importantes

- `customerId` no path.

## Saidas importantes

- `customerId`
- `customerName`
- `customerDocument`
- `customerStatus`
- `billingStatus`
- `availableLimit`
- `canCheckout`

## Erros relevantes

- `404` quando cliente ou billing nao existem para o checkout.
- `502` quando uma dependencia externa falha de forma inesperada.

## Caminho no codigo

- `src/main/java/com/jeffersonjr/checkout/resource/CheckoutResource.java`
- `src/main/java/com/jeffersonjr/checkout/service/CheckoutService.java`
- `src/main/java/com/jeffersonjr/checkout/client/CustomerApiClient.java`
- `src/main/java/com/jeffersonjr/checkout/client/BillingApiClient.java`