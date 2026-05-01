---
name: claude-doc-review
description: Valida se mudanças da checkout-api exigem atualização de README, docs, docs/ai-context.yaml e skills locais de review. Use quando quiser revisar se a documentação acompanhou mudanças de contrato, comportamento, integração, configuração ou fluxo.
argument-hint: [contexto opcional: PR, diff, foco em contrato, foco em documentação, etc.]
---

# Claude Doc Review

Use este skill para decidir se um conjunto de mudanças deve bloquear o PR por documentação desatualizada na `checkout-api`.

## Contexto obrigatório

Antes de revisar qualquer diff, leia nesta ordem:

1. `docs/ai-context.yaml`
2. `README.md`
3. `docs/contexts/**`
4. `docs/flows/**`

## Objetivo

Bloquear PR que muda comportamento, contrato ou integração sem atualizar a documentação correspondente.

Este review é gate bloqueante quando encontrar desatualização documental relevante.

## Deve bloquear quando

Bloqueie apenas quando as duas condições forem verdadeiras ao mesmo tempo:

1. O diff altera endpoint, DTO, regra de negócio, integração, configuração, contrato ou fluxo relevante.
2. `README.md`, `docs/**`, `docs/ai-context.yaml` ou os skills locais de `.claude` não foram atualizados de forma coerente quando a orientação de review também mudou.

## Não deve bloquear quando

- a mudança é só teste
- a mudança é refatoração interna sem impacto externo
- a mudança é ajuste de log
- a mudança é somente formatação
- a mudança é melhoria interna sem alteração de comportamento
- a mudança relevante já está documentada de forma coerente, mesmo que ainda mereça atenção humana

## Regras adicionais obrigatórias

- Testes, nomes de constantes, nomes de métodos, nomes de classes e o próprio código não contam como documentação.
- Se `README.md`, `docs/**` ou `docs/ai-context.yaml` contiverem regra antiga, contraditória, incompleta ou que deveria ser atualizada em relação ao diff, o resultado deve ser `BLOCK`.
- Não use `PASS` quando a própria análise identificar documentação desatualizada, contraditória ou incompleta.
- Não use `PASS com ponto de atenção` se o ponto de atenção for atualização documental faltante.

## Prioridades específicas da checkout-api

- alterações no endpoint `GET /v1/checkouts/{customerId}/summary`
- mudanças na regra de `canCheckout`
- alterações em contratos esperados de `customer-api` e `billing-api`
- mudanças na forma como `404` e `502` são tratados
- qualquer alteração que mude o fluxo orquestrador documentado

## Procedimento

1. Leia `docs/ai-context.yaml` para identificar `doc_sensitive_paths`, dependências e campos sensíveis.
2. Verifique se o diff mudou arquivos sensíveis ou alterou comportamento relevante.
3. Compare as mudanças de código com `README.md`, `docs/**` e `docs/ai-context.yaml`.
4. Se a documentação não acompanhou, aponte exatamente quais arquivos deveriam ter sido atualizados.
5. Se a mudança não exigir atualização documental, ou se a documentação estiver coerente com a mudança relevante, explicite por que o review passa.

## Formato esperado da resposta

- Comece com `BLOCK` ou `PASS`.
- Em caso de bloqueio, liste objetivamente o que mudou e quais arquivos documentais faltaram.
- Em caso de `PASS`, explique de forma curta por que a documentação está coerente ou por que a mudança não exige atualização.
- Não use `BLOCK` apenas para registrar que a mudança é sensível ou merece revisão humana explícita.