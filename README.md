# AC1 ATDD Spring Boot Docker

Projeto de implementação de **TDD (Test-Driven Development)** e **BDD (Behavior-Driven Development)** usando Spring Boot, Cucumber e Testes Unitários com JUnit.

---

## 👥 User Stories

### US-01: [ADM] Configurar cursos disponíveis no plano básico

```
Como Administrador de uma plataforma que vende cursos online e EAD no modelo de assinaturas
Quero configurar quais cursos ficam disponíveis no plano básico
Para controlar o catálogo de cursos oferecidos aos alunos
```

### US-02: [ALUNO] Desbloquear novos cursos ao atingir média 7.0

```
Como Aluno dedicado e que almeja desbloquear mais cursos no término de outro curso
Quero desbloquear 3 novos cursos ao terminar um curso com média acima de 7,0
Para continuar meu progresso de forma gamificada
```

### US-03: [ALUNO] Ganho de curso por participação no fórum ⭐ **SELECIONADA**

```
Como Aluno que quer ganhar mais cursos ao contribuir fortemente no fórum
Quero ganhar 1 curso ao final do mês por ser o mais ativo no fórum
Para ser recompensado por ajudar outros participantes
```

---

## 🎯 BDD - Behavior-Driven Development

### BDD-01: Aluno ganha curso ao ser mais ativo no fórum no mês

```gherkin
Cenário: Aluno mais ativo do fórum ganha um curso no final do mês
  Given que o aluno "João" tem 25 comentários úteis no fórum durante o mês
  And que o aluno "Maria" tem 15 comentários úteis no mês
  And que o aluno "Pedro" tem 10 comentários úteis no mês
  When chegar o final do mês
  And o sistema processar os rankings do fórum
  Then o aluno "João" deve receber 1 curso gratuito
  And uma notificação deve ser enviada informando sobre o prêmio
  And o status do aluno "João" deve mudar para "Prêmio Recebido"
```

### BDD-02: Aluno não ganha curso se não tiver participação mínima

```gherkin
Cenário: Aluno com participação insuficiente não ganha curso
  Given que o aluno "Carlos" tem apenas 2 comentários no fórum durante o mês
  And que a participação mínima exigida é de 5 comentários úteis
  And que existem outros alunos com participação acima do mínimo
  When chegar o final do mês
  And o sistema processar os rankings do fórum
  Then o aluno "Carlos" não deve receber nenhum curso
  And nenhuma notificação de prêmio deve ser enviada para "Carlos"
  And "Carlos" deve aparecer fora da lista de vencedores
```

### BDD-03: Apenas comentários úteis contam para ranking

```gherkin
Cenário: Apenas comentários com avaliação positiva contam no ranking
  Given que o aluno "Ana" tem 20 comentários no fórum
  And que 15 deles foram marcados como "útil" (positivos)
  And que 5 foram marcados como "não útil" (negativos)
  When chegar o final do mês
  And o sistema processar os rankings do fórum
  Then apenas os 15 comentários úteis devem ser contabilizados
  And "Ana" deve aparecer no ranking com 15 pontos
  And os comentários marcados como "não útil" não devem impactar o ranking
```

---

## 🧪 TDD - Test-Driven Development

### BDD-01: Aluno ganha curso ao ser mais ativo no fórum

#### 🔴 TDD-01.1: RED (Teste para Falhar)

```java
@Test
public void deveProcessarRankingEPremiarAlunoMaisAtivo() {
  var joao = new Aluno("João");
  var maria = new Aluno("Maria");
  var pedro = new Aluno("Pedro");
  joao.adicionarComentarioUtil(25);
  maria.adicionarComentarioUtil(15);
  pedro.adicionarComentarioUtil(10);
  var forum = new ForumRewardService();
  var resultado = forum.processarRankingMensal(Arrays.asList(joao, maria, pedro));
  
  assertEquals("João", resultado.getAlunoVencedor().getNome());
  assertNotNull(resultado.getCursoGanho());
  assertEquals("gratuito", resultado.getCursoGanho().getTipo());
  assertTrue(resultado.getNotificacaoEnviada());
}
```

**Status:** 🔴 FALHA - `ForumRewardService` não existe

---

#### 🟢 TDD-01.2: GREEN (Código Mínimo para Passar)

**Implementação da classe ForumRewardService:**

```java
public class ForumRewardService {
  
  public Resultado processarRankingMensal(List<Aluno> alunos) {
    Aluno alunoMaisAtivo = alunos.stream()
      .max(Comparator.comparingInt(Aluno::getComentariosUteis))
      .orElse(null);
    
    Curso curso = new Curso("Curso Gratuito", "gratuito");
    return new Resultado(alunoMaisAtivo, curso, true);
  }
}
```

**Teste correspondente:**

```java
@Test
public void deveProcessarRankingEPremiarAlunoMaisAtivo() {
  var joao = new Aluno("João");
  var maria = new Aluno("Maria");
  var pedro = new Aluno("Pedro");
  joao.adicionarComentarioUtil(25);
  maria.adicionarComentarioUtil(15);
  pedro.adicionarComentarioUtil(10);
  var forum = new ForumRewardService();
  var resultado = forum.processarRankingMensal(Arrays.asList(joao, maria, pedro));
  
  assertEquals("João", resultado.getAlunoVencedor().getNome());
  assertNotNull(resultado.getCursoGanho());
  assertEquals("gratuito", resultado.getCursoGanho().getTipo());
  assertTrue(resultado.getNotificacaoEnviada());
}
```

**Status:** 🟢 PASSA - Testes passando (código funcional)

---

#### 🔵 TDD-01.3: BLUE (Refatoração do GREEN)

**Implementação refatorada da classe ForumRewardService:**

```java
public class ForumRewardService {
  
  private static final String TIPO_CURSO = "gratuito";
  
  public Resultado processarRankingMensal(List<Aluno> alunos) {
    Aluno alunoMaisAtivo = encontrarAlunoMaisAtivo(alunos);
    Curso cursoGanho = criarCursoGratuito();
    enviarNotificacao(alunoMaisAtivo);
    return new Resultado(alunoMaisAtivo, cursoGanho, true);
  }
  
  private Aluno encontrarAlunoMaisAtivo(List<Aluno> alunos) {
    return alunos.stream()
      .max(Comparator.comparingInt(Aluno::getComentariosUteis))
      .orElseThrow(() -> new IllegalArgumentException("Lista vazia"));
  }
  
  private Curso criarCursoGratuito() {
    return new Curso("Curso Gratuito", TIPO_CURSO);
  }
  
  private void enviarNotificacao(Aluno aluno) {
    System.out.println("Notificação enviada para " + aluno.getNome());
  }
}
```

**Teste correspondente:**

```java
@Test
public void deveProcessarRankingEPremiarAlunoMaisAtivo() {
  var joao = new Aluno("João");
  var maria = new Aluno("Maria");
  var pedro = new Aluno("Pedro");
  joao.adicionarComentarioUtil(25);
  maria.adicionarComentarioUtil(15);
  pedro.adicionarComentarioUtil(10);
  var forum = new ForumRewardService();
  var resultado = forum.processarRankingMensal(Arrays.asList(joao, maria, pedro));
  
  assertEquals("João", resultado.getAlunoVencedor().getNome());
  assertNotNull(resultado.getCursoGanho());
  assertEquals("gratuito", resultado.getCursoGanho().getTipo());
  assertTrue(resultado.getNotificacaoEnviada());
}
```

**Status:** 🔵 PASSA - Código refatorado e limpo

---

### BDD-02: Aluno não ganha curso se não tiver participação mínima

#### 🔴 TDD-02.1: RED (Teste para Falhar)

```java
@Test
public void deveValidarParticipacaoMinimaENaoPremiar() {
  var carlos = new Aluno("Carlos");
  carlos.adicionarComentarioUtil(2);
  int participacaoMinima = 5;
  var forum = new ForumRewardService();
  var resultado = forum.processarRankingMensal(
    Arrays.asList(carlos),
    participacaoMinima
  );
  
  assertNull(resultado.getCursoGanho());
  assertFalse(resultado.getNotificacaoEnviada());
  assertFalse(resultado.getAlunosVencedores().contains(carlos));
}
```

**Status:** 🔴 FALHA - Parâmetro `participacaoMinima` não é aceito

---

#### 🟢 TDD-02.2: GREEN (Código Mínimo para Passar)

**Implementação da classe ForumRewardService com validação de participação:**

```java
public class ForumRewardService {
  
  public Resultado processarRankingMensal(List<Aluno> alunos, int minimo) {
    Aluno alunoMaisAtivo = alunos.stream()
      .filter(a -> a.getComentariosUteis() >= minimo)
      .max(Comparator.comparingInt(Aluno::getComentariosUteis))
      .orElse(null);
    
    if (alunoMaisAtivo == null) {
      return new Resultado(null, null, false);
    }
    
    Curso curso = new Curso("Curso Gratuito", "gratuito");
    return new Resultado(alunoMaisAtivo, curso, true);
  }
}
```

**Teste correspondente:**

```java
@Test
public void deveValidarParticipacaoMinimaENaoPremiar() {
  var carlos = new Aluno("Carlos");
  carlos.adicionarComentarioUtil(2);
  int participacaoMinima = 5;
  var forum = new ForumRewardService();
  var resultado = forum.processarRankingMensal(
    Arrays.asList(carlos),
    participacaoMinima
  );
  
  assertNull(resultado.getCursoGanho());
  assertFalse(resultado.getNotificacaoEnviada());
  assertFalse(resultado.getAlunosVencedores().contains(carlos));
}
```

**Status:** 🟢 PASSA - Testes passando (código funcional)

---

#### 🔵 TDD-02.3: BLUE (Refatoração do GREEN)

**Implementação refatorada da classe ForumRewardService:**

```java
public class ForumRewardService {
  
  private static final int PARTICIPACAO_PADRAO = 5;
  
  public Resultado processarRankingMensal(List<Aluno> alunos, int minimo) {
    Aluno alunoMaisAtivo = encontrarAlunoComParticipacaoMinima(alunos, minimo);
    
    if (!temVencedor(alunoMaisAtivo)) {
      return criarResultadoSemPremio();
    }
    
    return criarResultadoComPremio(alunoMaisAtivo);
  }
  
  private Aluno encontrarAlunoComParticipacaoMinima(List<Aluno> alunos, int minimo) {
    return alunos.stream()
      .filter(a -> a.getComentariosUteis() >= minimo)
      .max(Comparator.comparingInt(Aluno::getComentariosUteis))
      .orElse(null);
  }
  
  private boolean temVencedor(Aluno aluno) {
    return aluno != null;
  }
  
  private Resultado criarResultadoSemPremio() {
    return new Resultado(null, null, false);
  }
  
  private Resultado criarResultadoComPremio(Aluno aluno) {
    Curso curso = new Curso("Curso Gratuito", "gratuito");
    return new Resultado(aluno, curso, true);
  }
}
```

**Teste correspondente:**

```java
@Test
public void deveValidarParticipacaoMinimaENaoPremiar() {
  var carlos = new Aluno("Carlos");
  carlos.adicionarComentarioUtil(2);
  int participacaoMinima = 5;
  var forum = new ForumRewardService();
  var resultado = forum.processarRankingMensal(
    Arrays.asList(carlos),
    participacaoMinima
  );
  
  assertNull(resultado.getCursoGanho());
  assertFalse(resultado.getNotificacaoEnviada());
  assertFalse(resultado.getAlunosVencedores().contains(carlos));
}
```

**Status:** 🔵 PASSA - Código refatorado e limpo

---

### BDD-03: Apenas comentários úteis contam para ranking

#### 🔴 TDD-03.1: RED (Teste para Falhar)

```java
@Test
public void deveContabilizarApenasComentariosUteis() {
  var ana = new Aluno("Ana");
  ana.adicionarComentarioUtil(15);
  ana.adicionarComentarioNaoUtil(5);
  var forum = new ForumRewardService();
  var resultado = forum.processarRankingMensal(Arrays.asList(ana));
  var ranking = resultado.getRanking().get(0);
  
  assertEquals(15, ranking.getPontuacao());
  assertNotEquals(20, ranking.getPontuacao());
  assertEquals(15, ranking.getComentariosContabilizados());
  assertEquals(0, ranking.getComentariosNaoUteis());
}
```

**Status:** 🔴 FALHA - Método `getRanking()` não existe

---

#### 🟢 TDD-03.2: GREEN (Código Mínimo para Passar)

**Implementação da classe ForumRewardService com ranking:**

```java
public class ForumRewardService {
  
  public Resultado processarRankingMensal(List<Aluno> alunos) {
    List<RankingItem> ranking = new ArrayList<>();
    
    for (Aluno aluno : alunos) {
      int pontuacao = aluno.getComentariosUteis();
      RankingItem item = new RankingItem(
        aluno.getNome(), 
        pontuacao, 
        aluno.getComentariosUteis(), 
        0
      );
      ranking.add(item);
    }
    
    return new Resultado(ranking);
  }
}
```

**Teste correspondente:**

```java
@Test
public void deveContabilizarApenasComentariosUteis() {
  var ana = new Aluno("Ana");
  ana.adicionarComentarioUtil(15);
  ana.adicionarComentarioNaoUtil(5);
  var forum = new ForumRewardService();
  var resultado = forum.processarRankingMensal(Arrays.asList(ana));
  var ranking = resultado.getRanking().get(0);
  
  assertEquals(15, ranking.getPontuacao());
  assertNotEquals(20, ranking.getPontuacao());
  assertEquals(15, ranking.getComentariosContabilizados());
  assertEquals(0, ranking.getComentariosNaoUteis());
}
```

**Status:** 🟢 PASSA - Testes passando (código funcional)

---

#### 🔵 TDD-03.3: BLUE (Refatoração do GREEN)

**Implementação refatorada da classe ForumRewardService:**

```java
public class ForumRewardService {
  
  public Resultado processarRankingMensal(List<Aluno> alunos) {
    List<RankingItem> ranking = construirRanking(alunos);
    return new Resultado(ranking);
  }
  
  private List<RankingItem> construirRanking(List<Aluno> alunos) {
    return alunos.stream()
      .map(this::criarRankingItem)
      .collect(Collectors.toList());
  }
  
  private RankingItem criarRankingItem(Aluno aluno) {
    int pontuacao = calcularPontuacao(aluno);
    return new RankingItem(
      aluno.getNome(), 
      pontuacao, 
      aluno.getComentariosUteis(), 
      0
    );
  }
  
  private int calcularPontuacao(Aluno aluno) {
    return aluno.getComentariosUteis();
  }
}
```

**Teste correspondente:**

```java
@Test
public void deveContabilizarApenasComentariosUteis() {
  var ana = new Aluno("Ana");
  ana.adicionarComentarioUtil(15);
  ana.adicionarComentarioNaoUtil(5);
  var forum = new ForumRewardService();
  var resultado = forum.processarRankingMensal(Arrays.asList(ana));
  var ranking = resultado.getRanking().get(0);
  
  assertEquals(15, ranking.getPontuacao());
  assertNotEquals(20, ranking.getPontuacao());
  assertEquals(15, ranking.getComentariosContabilizados());
  assertEquals(0, ranking.getComentariosNaoUteis());
}
```

**Status:** 🔵 PASSA - Código refatorado e limpo

---

## 🏗️ Estrutura do Projeto

```
ac1_atdd_spring_boot_docker/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── org/example/ac1_atdd_spring_boot_docker/
│   │           ├── domain/
│   │           │   ├── Aluno.java
│   │           │   ├── Curso.java
│   │           │   ├── Resultado.java
│   │           │   ├── RankingItem.java
│   │           │   └── ForumRewardService.java
│   │           └── ...
│   └── test/
│       └── java/
│           └── org/example/ac1_atdd_spring_boot_docker/
│               └── domain/
│                   └── ForumRewardServiceTest.java
├── pom.xml
└── README.md
```

---

## 🛠️ Tecnologias Utilizadas

- **Java 11+**
- **Spring Boot 2.x/3.x**
- **Spring Data JPA**
- **JUnit 5**
- **H2 Database (Desenvolvimento)**
- **PostgreSQL (Produção)**
- **Maven**
- **Docker** (Opcional)

---

## 📦 Dependências Spring Boot

- Spring WEB
- Spring Data JPA
- H2 Database
- PostgreSQL Driver

---

## 📊 Resumo TDD

| Fase | Status | Objetivo |
|------|--------|----------|
| 🔴 RED | FALHA | Escrever teste que falha |
| 🟢 GREEN | PASSA | Código mínimo para passar |
| 🔵 BLUE | PASSA | Refatoração e limpeza |

