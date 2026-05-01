# 02 - Tratamento de elegibilidade e falhas externas

## Objetivo do fluxo

Aplicar a regra de elegibilidade do checkout e tratar adequadamente falhas e ausencias nas APIs dependentes.

## Sequencia ponta a ponta

1. O `CheckoutService` recebe o cliente e o billing retornados pelos clients.
2. O metodo `canCheckout(...)` verifica:
   - `customer.status == ACTIVE`
   - `billing.status == APPROVED`
   - `billing.availableLimit > 0`
3. Quando qualquer uma dessas condicoes falha, `canCheckout` retorna `false`.
4. Quando `customer-api` ou `billing-api` retornam ausencia, o servico propaga `404` contextualizado.
5. Quando uma dependencia falha inesperadamente, o servico responde `502`.

## Entradas importantes

- `customer.status`
- `billing.status`
- `billing.availableLimit`

## Saidas importantes

- `canCheckout = true`
- `canCheckout = false`
- `404`
- `502`

## Erros relevantes

- ausencia de cliente para checkout
- ausencia de billing para checkout
- indisponibilidade ou resposta inesperada das APIs dependentes

## Caminho no codigo

- `src/main/java/com/jeffersonjr/checkout/service/CheckoutService.java`
- `src/test/java/com/jeffersonjr/checkout/resource/CheckoutResourceTest.java`
- `src/test/java/com/jeffersonjr/checkout/service/CheckoutServiceTest.java`