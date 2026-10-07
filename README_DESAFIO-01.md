# DESAFIO 1 — DRIVERS E EVOLUÇÃO DO CORE

---

## 1. REQUISITOS FUNCIONAIS (RF)

- **RF01:** Processar ranking mensal de participantes (fórum)
- **RF02:** Gerenciar e conceder conquistas/badges
- **RF03:** Evolução contínua das funcionalidades existentes

---

## 2. REQUISITOS NÃO-FUNCIONAIS (RNF)

Qualidades críticas para o crescimento de 1k para 100k usuarios:

| RNF | Descrição |
|-----|-----------|
| **Escalabilidade** | Suportar crescimento de carga (ranking cresce 10x mais) |
| **Manutenibilidade** | Modificar uma funcionalidade sem quebrar outras |
| **Desempenho** | Latência aceitável em processamento de rankings |
| **Testabilidade** | Isolar testes por funcionalidade |
| **Complexidade Operacional** | Viabilidade de deploy/operação com 6 devs |

---

## 3. REQUISITOS ARQUITETURALMENTE SIGNIFICATIVOS (ASR)

**Segue as 3 RNFs que realmente dirigem a decisão:**

### ASR-01: Escalabilidade
**Como altera arquitetura?** Computação de ranking intensiva exige isolamento de recursos e possível deploy independente.

### ASR-02: Manutenibilidade
**Como altera arquitetura?** Ranking e Conquistas têm ciclos de mudança distintos; separação em módulos evita quebra acidental.

### ASR-03: Complexidade Operacional
**Como altera arquitetura?** Equipe pequena (6 devs) não suporta overhead de microserviços; deve ser viável com 1 deploy.

---

## 4. REQUISITOS/PREMISSAS/CONSTRAINTS (RPC)

Limitações que restringem o espaço de decisão:

| Constraint | Descrição |
|-----------|-----------|
| **RPC-01** | Core permanece em **Java/Spring Boot** |
| **RPC-02** | Equipe: **6 desenvolvedores** |
| **RPC-03** | Banco de dados **relacional** (PostgreSQL) |
| **RPC-04** | Legado da AC1 deve ser **reaproveitado** |

---

## 5. ALTERNATIVAS COMPARADAS

### Alternativa A: Monólito em Camadas (ATUAL - AC1)

```
┌─────────────────────┐
│   Controllers       │
├─────────────────────┤
│   Services          │ ← Ranking, Conquistas, Cursos (tudo junto)
├─────────────────────┤
│   Repositories      │
├─────────────────────┤
│   Database          │
└─────────────────────┘
```

**Trade-offs:**
- ✅ Deploy simples
- ✅ Sem overhead operacional
- ❌ Acoplamento alto (mudança impacta tudo)
- ❌ Difícil escalar ranking independentemente
- ❌ Testes precisam mockar múltiplas dependências

O Mnólito em Camadas foi uma boa e suficiente alternativa para o projeto da AC1, porém, dada 
as circunstâncias da AC2, uma nova abordagem deve ser adotada.

---

### Alternativa B: Monólito Modular

```
┌────────────────────────────┐
│   Controllers (Único)      │
├────────────────────────────┤
│ ┌──────────┐ ┌──────────┐  │
│ │ RANKING  │ │CONQUISTA │  │
│ │ MODULE   │ │ MODULE   │  │
│ ├──────────┤ ├──────────┤  │
│ │ Domain   │ │ Domain   │  │
│ │ Service  │ │ Service  │  │
│ │ Repo     │ │ Repo     │  │
│ └──────────┘ └──────────┘  │
├────────────────────────────┤
│   Repositories (Shared)    │
├────────────────────────────┤
│   Database                 │
└────────────────────────────┘
```

**Trade-offs:**
- ✅ Isolamento lógico (mudança em um módulo não quebra outro)
- ✅ Deploy ainda simples (1 JAR)
- ✅ Sem overhead operacional
- ✅ Pronto para evoluir para serviços
- ⚠️ Requer refatoração (esforço inicial)

---

### Alternativa C: Microserviços

```
┌──────────────┐       ┌──────────────┐
│Ranking Svc   │◄─────►│Conquest Svc  │
└──────────────┘       └──────────────┘
       ▲                      ▲
       └─────────┬────────────┘
            API Gateway
```

**Trade-offs:**
- ✅✅ Escalabilidade máxima
- ✅✅ Deploy totalmente independente
- ❌ Complexidade operacional ALTA (N pipelines, N deploys)
- ❌ Sincronização de dados complexa (transações distribuídas)
- ❌ **Inviável com 6 devs** (overhead operacional)

---

## ANÁLISE DE DECISÃO

| Critério | A (Camadas) | B (Modular) ⭐ | C (Microserviços) |
|----------|-------------|-----------------|-------------------|
| Resolve ASR-01? | ❌ Não | ✅ Sim | ✅✅ Sim |
| Resolve ASR-02? | ❌ Não | ✅ Sim | ✅ Sim |
| Resolve ASR-03? | ✅ Sim | ✅ Sim | ❌ Não |
| Respita RPC-01? | ✅ Sim | ✅ Sim | ✅ Sim |
| Respita RPC-02? | ✅ Sim | ✅ Sim | ❌ Não |
| Overhead | Nenhum | Nenhum | Alto |
| Esforço | 0 | Médio | Alto |

**Conclusão:** **Alternativa B (Monólito Modular)** é o melhor trade-off.

---

## 6. ARCHITECTURE DECISION RECORD (ADR-001)

### Título
**Evoluir de Monólito em Camadas para Monólito Modular**

### Contexto
- MVP cresceu de 1k para 100k usuários esperados
- Ranking é funcionalidade que cresce mais (10x)
- Ranking, Conquistas e Cursos têm ciclos de mudança distintos
- Equipe tem 6 desenvolvedores
- Legado da AC1 deve ser aproveitado
- Prepara evolução para futuros serviços (Desafios 3+)

### Decisão
Reorganizar o **Core em módulos temáticos** (Ranking, Conquistas, Shared), mantendo como **um único deployment** mas com **fronteiras explícitas** entre domínios.

```
src/main/java/org/example/ac1_atdd/
├── ranking/
│   ├── domain/
│   ├── service/
│   └── repository/
├── conquistas/
│   ├── domain/
│   ├── service/
│   └── repository/
└── shared/
    └── domain/
```

### Justificativa
- ✅ **Resolve ASR-01:** Ranking pode escalar independentemente dentro do monólito
- ✅ **Resolve ASR-02:** Ciclos de mudança isolados por módulo
- ✅ **Resolve ASR-03:** Sem overhead operacional com 1 deploy
- ✅ **Respeita RPCs:** Java, 6 devs, relacional, legado
- ✅ **Evolutivo:** Prepara para serviços no futuro

### Consequências

**Positivas:**
- Isolamento lógico sem complexidade operacional
- Mudança em um módulo não afeta outro
- Testes mais rápidos (mocks de módulos)
- Preparação para Desafios 2-6
- Escalabilidade gradual

**Negativas:**
- Refatoração necessária
- Boundary entre módulos exige disciplina
- Ainda não resolve escala de recursos (um JVM)

**Mitigação:**
- Interfaces Java para separação clara
- Package-level encapsulation
- Testes de integração entre módulos
- Futuro: separar em serviços se necessário

---

## 7. C4 CONTAINER DIAGRAM

```
┌──────────────────────────────────────────────────┐
│               Web Client                         │
│           (Vue 3 + Vite)                         │
└─────────────────────┬──────────────────────────┘
                      │ HTTP/REST
                      ▼
┌──────────────────────────────────────────────────┐
│        Spring Boot Application                   │
│      (Monólito Modular - 1 Deploy)               │
│                                                  │
│  ┌─────────────────────────────────────────┐   │
│  │      API Controller Layer               │   │
│  └─────────────────────────────────────────┘   │
│           │           │           │             │
│  ┌────────▼──┐ ┌─────▼──────┐ ┌──▼──────┐    │
│  │  RANKING  │ │ CONQUISTAS │ │ SHARED  │    │
│  │  MODULE   │ │   MODULE   │ │ MODULE  │    │
│  ├───────────┤ ├────────────┤ ├─────────┤    │
│  │ • Domain  │ │ • Domain   │ │• Curso  │    │
│  │ • Service │ │ • Service  │ │• Config │    │
│  │ • Repo    │ │ • Repo     │ │         │    │
│  └────────────┘ └────────────┘ └─────────┘    │
│           │           │           │             │
│  ┌────────▼───────────▼───────────▼──────┐    │
│  │   Repository Layer (JPA)               │    │
│  │  - RankingRepository                   │    │
│  │  - ConquistaRepository                 │    │
│  │  - CursoRepository                     │    │
│  └──────────────────┬──────────────────────┘   │
└─────────────────────┼───────────────────────────┘
                      │ SQL
                      ▼
         ┌──────────────────────────┐
         │  PostgreSQL Database     │
         │  (Tabelas compartilhadas)│
         └──────────────────────────┘
```

---

## 8. EVIDÊNCIA / MINI-LAB

### Objetivo
Demonstrar que a estrutura modular é viável e não quebra o sistema.

### Estrutura Modular Planejada

**ANTES (Atual - Monólito em Camadas):**
```
src/main/java/org/example/ac1_atdd/
├── domain/       (Aluno, Comentario, RankingItem, Resultado, Curso)
├── service/      (ForumRewardService, AlunoService, etc)
├── repository/   (AlunoRepository, CursoRepository, etc)
├── controller/   (ForumRewardController)
└── dto/          (AlunoDTO, CursoDTO, etc)
```

**DEPOIS (Objetivo - Monólito Modular):**
```
src/main/java/org/example/ac1_atdd/
├── ranking/
│   ├── domain/       (Aluno, Comentario, RankingItem, Resultado)
│   ├── service/      (ForumRewardService)
│   ├── repository/   (RankingItemRepository, ResultadoRepository)
│   └── api/          (RankingController)
│
├── conquistas/
│   ├── domain/       (Conquista, Badge)
│   ├── service/      (ConquistaService)
│   ├── repository/   (ConquistaRepository)
│   └── api/          (ConquistaController)
│
└── shared/
    ├── domain/       (Curso, Usuario)
    └── config/       (Configurações comuns)
```

### Passos Executados

1. **Criar estrutura de pacotes** ✅
   ```bash
   mkdir -p src/main/java/org/example/ac1_atdd/ranking/{domain,service,repository}
   mkdir -p src/main/java/org/example/ac1_atdd/shared/domain
   ```

2. **Mover classes** (planejado para próxima iteração)
    - Ranking classes → `ranking/`
    - Shared classes → `shared/`

3. **Compilar sem erros** ✅
   ```bash
   mvn clean compile
   ```

4. **Testes passando** ✅
   ```bash
   mvn test
   ```

5. **Docker rodando** ✅
   ```bash
   docker-compose up -d
   ```

### Comparação de Dependências

| Métrica | ANTES | DEPOIS (Objetivo) |
|---------|-------|-------------------|
| Pacotes | 5 (domain, service, repo, controller, dto) | 6+ (ranking/*, conquistas/*, shared/*) |
| Imports cruzados | Alto (tudo importa de domain/) | Baixo (cada módulo importa seu domain/) |
| Acoplamento | Alto | Baixo |
| Testabilidade | Média | Alta |

---

## MATRIZ CUMULATIVA DE ASRs

| Desafio | ASR-01 (Escalabilidade) | ASR-02 (Manutenibilidade) | ASR-03 (Complexidade Operacional) |
|---------|-------------------------|---------------------------|----------------------------------|
| **Desafio 1** | ✅ Isolamento em módulo | ✅ Ciclos independentes | ✅ 1 deploy, sem overhead |
| Desafio 2 | - | ✅ Ports & Adapters | - |
| Desafio 3 | ✅ IA em serviço Python | - | - |
| Desafio 4 | - | - | ✅ Event-driven |
| Desafio 5 | - | - | ✅ MQTT integrado |
| Desafio 6 | - | - | ✅ CI/CD independente |

---

## CONCLUSÃO

### O que mudou arquiteturalmente?

**De A1 para A2 - Desafio 1:**
- ❌ **Antes:** Monólito em Camadas (tudo junto)
- ✅ **Depois:** Monólito Modular (módulos isolados)

---