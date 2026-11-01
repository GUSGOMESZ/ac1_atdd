# Desafio 1 — Drivers e Evolução do Core

O registro completo do Desafio 1 (RF → RNF → ASR → RPC → alternativas → ADR-001 → C4 → evidência) está em:

➡️ **[docs/arquitetura/README.md](docs/arquitetura/README.md)**

**Resumo:** o Core da AC1 evoluiu de **Monólito em Camadas** para **Monólito Modular** (`ranking`, `conquistas`, `shared`, cada um com `api/` + `internal/`), mantendo um único deploy. As fronteiras entre módulos são verificadas pelo teste `ArquiteturaModularTest`.
