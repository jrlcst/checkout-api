---
name: claude-code-review
description: Revisa mudanças da checkout-api com foco em risco real de bug, quebra de contrato entre APIs, impacto em integrações, ausência de teste relevante, inconsistência de regra de negócio e risco de configuração. Use quando quiser fazer code review desta API sem duplicar achados de Sonar ou Trivy.
argument-hint: [contexto opcional: PR, diff, foco em contrato, foco em integração, etc.]
---

# Claude Code Review

Use este skill para revisar mudanças da `checkout-api` com foco em risco real e impacto funcional.

## Contexto obrigatório

Antes de revisar qualquer diff, leia nesta ordem:

1. `docs/ai-context.yaml`
2. `README.md`
3. `docs/contexts/**` e `docs/flows/**` quando forem necessários para entender contrato, fluxo ou integração

## Objetivo

O objetivo deste review é ajudar o reviewer humano.

Este review não é gate bloqueante.

## O que deve avaliar

- possível bug funcional
- quebra de contrato HTTP
- impacto em consumidores ou dependências conhecidas
- alteração de regra de negócio
- ausência de teste relevante em comportamento alterado
- risco em configuração

## O que não deve avaliar

- estilo irrelevante
- comentário genérico
- refatoração grande fora do escopo
- achados já cobertos por Sonar ou Trivy

## Prioridades específicas da checkout-api

- mudanças no endpoint `GET /v1/checkouts/{customerId}/summary`
- alterações nos campos `customerDocument`, `customerStatus`, `billingStatus`, `availableLimit` e `canCheckout`
- mudanças nos contratos esperados de `customer-api` e `billing-api`
- alterações na regra que calcula `canCheckout`
- tratamento de `404` e `502` nas integrações externas

## Procedimento

1. Leia `docs/ai-context.yaml` para entender endpoint, dependências e campos sensíveis.
2. Leia apenas os arquivos alterados necessários para confirmar o comportamento real.
3. Priorize mudanças em contrato, DTO, service, resource, clients, configuração e testes.
4. Considere impacto cross-service quando houver alteração de campos esperados de `customer-api` ou `billing-api`.
5. Se não houver risco concreto, declare que não encontrou findings relevantes.

## Formato esperado da resposta

- Liste findings primeiro, ordenados por severidade.
- Use linguagem curta, técnica e verificável.
- Se não houver findings, diga explicitamente que não encontrou problemas relevantes.
- Pode mencionar riscos residuais ou gaps de teste ao final.