package org.example.ac1_atdd.domain;

import org.example.ac1_atdd.service.ForumRewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ForumRewardService Test Suite - TDD/BDD Implementation
 * 9 Test Cases distributed across 3 BDD Scenarios:
 * - BDD-01: Aluno ganha curso ao ser mais ativo
 * - BDD-02: Aluno não ganha curso se não tiver participação mínima
 * - BDD-03: Apenas comentários úteis contam
 * Each scenario has 3 phases:
 * RED:   Teste falha — comportamento desejado ainda não implementado
 * GREEN: RED corrigido — implementação mínima faz o teste passar
 * BLUE:  GREEN refatorado — código limpo, cobertura completa
 */
@SpringBootTest
@DisplayName("Forum Reward Service - TDD Test Suite")
class ForumRewardServiceTest {

    @Autowired
    private ForumRewardService forumRewardService;

    private Aluno aluno1;
    private Aluno aluno2;
    private Aluno aluno3;
    private Curso cursoJava;

    @BeforeEach
    void setUp() {
        aluno1 = new Aluno("João Silva", "joao@email.com");
        aluno1.setId(1L);

        aluno2 = new Aluno("Maria Santos", "maria@email.com");
        aluno2.setId(2L);

        aluno3 = new Aluno("Pedro Costa", "pedro@email.com");
        aluno3.setId(3L);

        cursoJava = new Curso("Java Avançado", "PROGRAMMING");
        cursoJava.setId(1L);

        // aluno1: 5 úteis + 1 não útil = Score: 4 (MAIS ATIVO)
        aluno1.adicionarComentarioUtil(new Comentario("Great explanation!", true));
        aluno1.adicionarComentarioUtil(new Comentario("Very helpful", true));
        aluno1.adicionarComentarioUtil(new Comentario("Perfect!", true));
        aluno1.adicionarComentarioUtil(new Comentario("Thank you", true));
        aluno1.adicionarComentarioUtil(new Comentario("Awesome", true));
        aluno1.adicionarComentarioNaoUtil(new Comentario("Not relevant", false));

        // aluno2: 3 úteis + 2 não úteis = Score: 1
        aluno2.adicionarComentarioUtil(new Comentario("Good point", true));
        aluno2.adicionarComentarioUtil(new Comentario("I agree", true));
        aluno2.adicionarComentarioUtil(new Comentario("Exactly", true));
        aluno2.adicionarComentarioNaoUtil(new Comentario("Wrong", false));
        aluno2.adicionarComentarioNaoUtil(new Comentario("Spam", false));

        // aluno3: 2 úteis + 0 não úteis = Score: 2
        aluno3.adicionarComentarioUtil(new Comentario("Interesting", true));
        aluno3.adicionarComentarioUtil(new Comentario("Worth reading", true));
    }

    // ==================== BDD-01: Aluno ganha curso ao ser mais ativo ====================

    @Test
    @DisplayName("RED PHASE - BDD-01: deveProcessarRankingEPremiarAlunoMaisAtivo")
    void deveProcessarRankingEPremiarAlunoMaisAtivo_RED() {
        System.out.println("\n===== BDD-01 - RED =====");
        // RED: Ranking ainda não é implementado — assertNull falha pois ranking existe
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);
        alunos.add(aluno2);
        alunos.add(aluno3);

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        assertNotNull(resultado, "Resultado should not be null");
        assertNull(resultado.getRanking(), "Ranking should be null (not implemented)");
    }

    @Test
    @DisplayName("GREEN PHASE - BDD-01: deveProcessarRankingEPremiarAlunoMaisAtivo")
    void deveProcessarRankingEPremiarAlunoMaisAtivo_GREEN() {
        System.out.println("\n===== BDD-01 - GREEN =====");
        // GREEN: RED corrigido — assertNull vira assertNotNull, verifica estrutura básica do ranking
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);
        alunos.add(aluno2);
        alunos.add(aluno3);

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        assertNotNull(resultado, "Resultado should not be null");
        assertNotNull(resultado.getRanking(), "Ranking should not be null");
        assertEquals(3, resultado.getRanking().size(), "Ranking should contain all 3 students");
    }

    @Test
    @DisplayName("BLUE PHASE - BDD-01: deveProcessarRankingEPremiarAlunoMaisAtivo")
    void deveProcessarRankingEPremiarAlunoMaisAtivo_BLUE() {
        System.out.println("\n===== BDD-01 - BLUE =====");
        // BLUE: GREEN refatorado — valida o fluxo completo de premiação
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);  // Score: 4 — VENCEDOR
        alunos.add(aluno2);  // Score: 1
        alunos.add(aluno3);  // Score: 2

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        assertNotNull(resultado, "Resultado should be created");
        assertNotNull(resultado.getAlunoVencedor(), "Winner should be identified");
        assertEquals("João Silva", resultado.getAlunoVencedor().getNome(),
                "João Silva (score 4) should be the winner");
        assertEquals(cursoJava.getId(), resultado.getCursoGanho().getId(),
                "Winner should receive Java Avançado course");
        assertTrue(resultado.isNotificacaoEnviada(), "Winner notification should be sent");
        assertTrue(resultado.getAlunosVencedores().contains(aluno1),
                "Winner list should contain João Silva");
        assertEquals(1, resultado.getAlunosVencedores().size(),
                "Only one winner should be selected");
    }

    // ==================== BDD-02: Aluno não ganha curso se não tiver participação mínima ====================

    @Test
    @DisplayName("RED PHASE - BDD-02: deveValidarParticipacaoMinimaENaoPremiar")
    void deveValidarParticipacaoMinimaENaoPremiar_RED() {
        System.out.println("\n===== BDD-02 - RED =====");
        // RED: Método com mínimo ainda não existe — espera exceção que nunca vem, falha no fail()
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);
        alunos.add(aluno2);
        alunos.add(aluno3);

        try {
            Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava, 3L);
            fail("Method with minimum parameter should not exist yet");
        } catch (Exception e) {
            assertTrue(true, "Method not implemented in RED phase");
        }
    }

    @Test
    @DisplayName("GREEN PHASE - BDD-02: deveValidarParticipacaoMinimaENaoPremiar")
    void deveValidarParticipacaoMinimaENaoPremiar_GREEN() {
        System.out.println("\n===== BDD-02 - GREEN =====");
        // GREEN: RED corrigido — método existe, verifica que retorna resultado e ranking válidos
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);  // Score: 4
        alunos.add(aluno2);  // Score: 1
        alunos.add(aluno3);  // Score: 2

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava, 3L);

        assertNotNull(resultado, "Resultado should be created");
        assertNotNull(resultado.getRanking(), "Ranking should be created");
    }

    @Test
    @DisplayName("BLUE PHASE - BDD-02: deveValidarParticipacaoMinimaENaoPremiar")
    void deveValidarParticipacaoMinimaENaoPremiar_BLUE() {
        System.out.println("\n===== BDD-02 - BLUE =====");
        // BLUE: GREEN refatorado — valida filtragem por mínimo e múltiplos vencedores
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);  // Score: 4 (qualifica: 4 >= 2)
        alunos.add(aluno3);  // Score: 2 (borderline: 2 >= 2 — qualifica)
        alunos.add(aluno2);  // Score: 1 (não qualifica: 1 < 2)

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava, 2L);

        assertNotNull(resultado.getAlunoVencedor(), "Winner should be identified from qualifiers");
        assertEquals("João Silva", resultado.getAlunoVencedor().getNome(),
                "João Silva (score 4) has highest score among qualifiers");
        assertTrue(resultado.isNotificacaoEnviada(), "Notification sent to qualifying winner");
        assertTrue(resultado.getRanking().size() >= 2, "Ranking should include all students");
        assertEquals(2, resultado.getAlunosVencedores().size(),
                "Two students meet minimum participation (scores 4 and 2)");
        assertTrue(resultado.getAlunosVencedores().contains(aluno1),
                "Winner list should include João (score 4)");
        assertTrue(resultado.getAlunosVencedores().contains(aluno3),
                "Winner list should include Pedro (score 2)");
    }

    // ==================== BDD-03: Apenas comentários úteis contam ====================

    @Test
    @DisplayName("RED PHASE - BDD-03: deveContabilizarApenasComentariosUteis")
    void deveContabilizarApenasComentariosUteis_RED() {
        System.out.println("\n===== BDD-03 - RED =====");
        // RED: Pontuação ainda não calculada — assertNull falha pois pontuação retorna 4
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);
        alunos.add(aluno2);
        alunos.add(aluno3);

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        assertNotNull(resultado.getRanking(), "Ranking should exist");
        assertNull(resultado.getRanking().get(0).getPontuacao(), "Score not calculated in RED phase");
    }

    @Test
    @DisplayName("GREEN PHASE - BDD-03: deveContabilizarApenasComentariosUteis")
    void deveContabilizarApenasComentariosUteis_GREEN() {
        System.out.println("\n===== BDD-03 - GREEN =====");
        // GREEN: RED corrigido — ranking existe e tem itens com pontuação calculada
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);
        alunos.add(aluno2);
        alunos.add(aluno3);

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        assertNotNull(resultado.getRanking(), "Ranking should exist");
        assertTrue(resultado.getRanking().size() > 0, "Ranking should contain items");
    }

    @Test
    @DisplayName("BLUE PHASE - BDD-03: deveContabilizarApenasComentariosUteis")
    void deveContabilizarApenasComentariosUteis_BLUE() {
        System.out.println("\n===== BDD-03 - BLUE =====");
        // BLUE: GREEN refatorado — valida ordem do ranking e fórmula de pontuação
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno2);  // Score: 1 (3 úteis - 2 não úteis)
        alunos.add(aluno3);  // Score: 2 (2 úteis - 0 não úteis)
        alunos.add(aluno1);  // Score: 4 (5 úteis - 1 não útil)

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        List<RankingItem> ranking = resultado.getRanking();
        assertEquals(3, ranking.size(), "All students should be in ranking");

        assertEquals(4, ranking.get(0).getPontuacao(), "Highest score should be first (João with 4)");
        assertEquals(2, ranking.get(1).getPontuacao(), "Second highest should be 2 (Pedro)");
        assertEquals(1, ranking.get(2).getPontuacao(), "Lowest score should be 1 (Maria)");

        RankingItem joaoItem = ranking.get(0);
        assertEquals(5, joaoItem.getComentariosContabilizados(), "João has 5 useful comments");
        assertEquals(1, joaoItem.getComentariosNaoUteis(), "João has 1 non-useful comment");
        assertEquals(4, joaoItem.getComentariosContabilizados() - joaoItem.getComentariosNaoUteis(),
                "Score formula: 5 - 1 = 4");
    }
}
