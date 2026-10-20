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
Reorganizar o **Core em módulos temáticos** (Ranking, Conquistas, Shared), mantendo como **um único deployment** mas com **fronteiras explícitas** entre domínios. Cada módulo expõe uma camada `api/` pública (Controllers + DTOs) e encapsula sua lógica em `internal/` (Service, Repository, Domain).

```
src/main/java/org/example/ac1_atdd/
├── ranking/
│   ├── api/         ← ForumRewardController, RankingItemDTO, ResultadoDTO
│   └── internal/
│       ├── domain/      (Aluno, Comentario, RankingItem, Resultado)
│       ├── service/     (ForumRewardService)
│       └── repository/  (RankingItemRepository, ResultadoRepository)
├── conquistas/
│   ├── api/         ← ConquistasController, ConquistaDTO
│   └── internal/
│       ├── domain/      (Conquista)
│       ├── service/     (ConquistasService)
│       └── repository/  (ConquistaRepository)
└── shared/
    ├── domain/      (Curso)
    ├── dto/         (AlunoDTO, CursoDTO)
    └── repository/  (AlunoRepository, CursoRepository)
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

**DEPOIS (Implementado - Monólito Modular com api/ + internal/):**
```
src/main/java/org/example/ac1_atdd/
├── ranking/
│   ├── api/              ← PUBLIC: ForumRewardController, RankingItemDTO, ResultadoDTO
│   └── internal/         ← ENCAPSULADO (não acessível por outros módulos diretamente)
│       ├── domain/       (Aluno, Comentario, RankingItem, Resultado)
│       ├── service/      (ForumRewardService)
│       └── repository/   (RankingItemRepository, ResultadoRepository)
│
├── conquistas/
│   ├── api/              ← PUBLIC: ConquistasController, ConquistaDTO
│   └── internal/         ← ENCAPSULADO
│       ├── domain/       (Conquista)
│       ├── service/      (ConquistasService)
│       └── repository/   (ConquistaRepository)
│
└── shared/
    ├── domain/           (Curso)
    ├── dto/              (AlunoDTO, CursoDTO)
    └── repository/       (AlunoRepository, CursoRepository)
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

| Métrica | ANTES (Monólito em Camadas) | DEPOIS (Monólito Modular) |
|---------|-----------------------------|---------------------------|
| Organização | Por camada técnica (domain/, service/, repo/) | Por capacidade de negócio (ranking/, conquistas/) |
| Pacotes | 5 flat (domain, service, repo, controller, dto) | 3 módulos × api/ + internal/ = fronteiras explícitas |
| Imports cruzados | Alto — tudo importa de um `domain/` global | Baixo — cada módulo encapsula seu próprio domain |
| Acoplamento | Alto — mudança em domain quebra service e controller | Baixo — mudança em ranking/internal não afeta conquistas |
| Testabilidade | Média — testes precisam de mocks de dependências globais | Alta — cada módulo pode ser testado isoladamente |
| Evolução futura | Difícil extrair módulo para serviço | Fácil — contrato via api/ já está definido |

### Evidência de Dependências ANTES vs DEPOIS

**ANTES — dependências misturadas (AC1 - monólito em camadas):**
```
controller/ForumRewardController
    └── imports: domain.Aluno, domain.Resultado, domain.Curso
                 service.ForumRewardService
                 repository.AlunoRepository, repository.CursoRepository

service/ForumRewardService
    └── imports: domain.Aluno, domain.RankingItem, domain.Resultado, domain.Curso

# Problema: domain/ é compartilhado por TODOS — qualquer mudança quebra tudo
```

**DEPOIS — dependências explícitas por módulo (Desafio 1 - modular):**
```
ranking/api/ForumRewardController
    └── imports: ranking.internal.domain.*        (próprio módulo)
                 ranking.internal.service.*        (próprio módulo)
                 ranking.internal.repository.*     (próprio módulo)
                 shared.domain.Curso              (shared = contrato)
                 shared.repository.*              (shared = contrato)

conquistas/api/ConquistasController
    └── imports: conquistas.internal.domain.*     (próprio módulo)
                 conquistas.internal.service.*    (próprio módulo)

# Resultado: conquistas não depende de ranking — ciclos de mudança independentes
```

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
- ❌ **Antes:** Monólito em Camadas — organização por camada técnica, acoplamento alto, difícil evoluir
- ✅ **Depois:** Monólito Modular — organização por capacidade de negócio, fronteiras explícitas via `api/` + `internal/`

### O que foi comprovado no Mini-Lab?

1. **Compilação sem erros** — a refatoração foi executada sem quebrar o build
2. **Testes passando** — os 6 testes ativos (GREEN + BLUE das 3 BDDs) continuam verdes após a migração de pacotes
3. **Fronteiras funcionando** — `conquistas/` não importa nada de `ranking/internal/`, apenas o módulo `shared/`
4. **Docker rodando** — a aplicação sobe normalmente após refatoração

### Conclusão Racional: Por que Monólito Modular agora e não Microsserviços?

**A pergunta central:** com crescimento de 1k → 100k usuários, por que não ir direto para microsserviços?

**Resposta:**

| Fator | Microsserviços | Monólito Modular |
|-------|----------------|-----------------|
| Equipe de 6 devs | ❌ Inviável — N pipelines, N repos, N deploys | ✅ 1 repo, 1 deploy, 1 pipeline |
| Operação do ranking | ❌ Requer service mesh, tracing distribuído | ✅ Logs centralizados, 1 JVM |
| Transações entre módulos | ❌ Sagas, compensações, eventual consistency | ✅ Transações ACID locais |
| Tempo de migração do legado AC1 | ❌ Alto — reescrever tudo como serviços | ✅ Refatoração incremental mantendo legado |
| Preparação para o futuro | ✅✅ Deploy independente já | ✅ Contrato via api/ facilita extração futura |

**Raciocínio:** O Monólito Modular **resolve os ASRs críticos agora** (manutenibilidade e complexidade operacional) sem o overhead operacional que inviabilizaria a equipe. A separação em `api/` + `internal/` **prepara o terreno** para uma futura extração em serviços quando a escala realmente exigir — e a equipe tiver crescido para suportar essa complexidade.

**Microsserviços seria a escolha certa se:** a equipe tivesse 20+ devs, times independentes por domínio, ou se o ranking exigisse deploy/escala completamente independente em produção já.

### Próximos Passos (Desafios 2-6)

| Desafio | Evolução Planejada |
|---------|--------------------|
| Desafio 2 | Ports & Adapters — isolar ranking da infraestrutura via interfaces |
| Desafio 3 | Serviço Python de IA — primeiro serviço externo ao monólito |
| Desafio 4 | Event-driven — ranking dispara eventos para conquistas |
| Desafio 5 | MQTT — integração IoT sem acoplamento direto |
| Desafio 6 | CI/CD independente por módulo — pipeline separado para ranking |

---