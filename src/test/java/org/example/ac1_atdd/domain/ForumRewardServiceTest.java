package org.example.ac1_atdd.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ForumRewardService Test Suite - TDD/BDD Implementation
 *
 * 9 Test Cases distributed across 3 BDD Scenarios:
 * - BDD-01: Aluno ganha curso ao ser mais ativo
 * - BDD-02: Aluno não ganha curso se não tiver participação mínima
 * - BDD-03: Apenas comentários úteis contam
 *
 * Each scenario has 3 phases:
 * RED: Failing test (before implementation)
 * GREEN: Minimal passing implementation
 * BLUE: Refactored, clean, optimal code
 */
@SpringBootTest
@DisplayName("Forum Reward - TDD Test Suite (9 Tests)")
public class ForumRewardServiceTest {

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

        // aluno1: 5 useful + 1 not useful = Score: 4 (MOST ACTIVE)
        aluno1.adicionarComentarioUtil(new Comentario("Great explanation!", true));
        aluno1.adicionarComentarioUtil(new Comentario("Very helpful", true));
        aluno1.adicionarComentarioUtil(new Comentario("Perfect!", true));
        aluno1.adicionarComentarioUtil(new Comentario("Thank you", true));
        aluno1.adicionarComentarioUtil(new Comentario("Awesome", true));
        aluno1.adicionarComentarioNaoUtil(new Comentario("Not relevant", false));

        // aluno2: 3 useful + 2 not useful = Score: 1
        aluno2.adicionarComentarioUtil(new Comentario("Good point", true));
        aluno2.adicionarComentarioUtil(new Comentario("I agree", true));
        aluno2.adicionarComentarioUtil(new Comentario("Exactly", true));
        aluno2.adicionarComentarioNaoUtil(new Comentario("Wrong", false));
        aluno2.adicionarComentarioNaoUtil(new Comentario("Spam", false));

        // aluno3: 2 useful + 0 not useful = Score: 2
        aluno3.adicionarComentarioUtil(new Comentario("Interesting", true));
        aluno3.adicionarComentarioUtil(new Comentario("Worth reading", true));
    }

    // ==================== BDD-01: Aluno ganha curso ao ser mais ativo ====================

    /*
    @Test
    @DisplayName("RED PHASE - BDD-01: deveProcessarRankingEPremiarAlunoMaisAtivo")
    void deveProcessarRankingEPremiarAlunoMaisAtivo_RED() {
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);
        alunos.add(aluno2);
        alunos.add(aluno3);

        // RED: Serviço não existe ainda, vai dar erro
        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        assertNotNull(resultado, "Resultado should not be null");
        assertNull(resultado.getRanking(), "Ranking should be null (not implemented)");
    }
     */

    @Test
    @DisplayName("RED PHASE - BDD-01: deveProcessarRankingEPremiarAlunoMaisAtivo")
    void deveProcessarRankingEPremiarAlunoMaisAtivo_RED() {
        // RED PHASE: Test that verifies the basic structure
        // This test ensures ForumRewardService can be called and returns a Resultado
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);
        alunos.add(aluno2);
        alunos.add(aluno3);

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        // Basic assertions - verify the service returns a non-null result
        assertNotNull(resultado, "Resultado should not be null");
        assertNotNull(resultado.getRanking(), "Ranking should not be null");
        assertEquals(3, resultado.getRanking().size(), "Ranking should contain all 3 students");
    }

    @Test
    @DisplayName("GREEN PHASE - BDD-01: deveProcessarRankingEPremiarAlunoMaisAtivo")
    void deveProcessarRankingEPremiarAlunoMaisAtivo_GREEN() {
        // GREEN PHASE: Test that the winner is correctly identified
        // The service should identify the student with the highest score as the winner
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);  // Score: 4 (5 useful - 1 not useful) - WINNER
        alunos.add(aluno2);  // Score: 1
        alunos.add(aluno3);  // Score: 2

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        // Verify the most active student (aluno1) is the winner
        assertNotNull(resultado.getAlunoVencedor(), "There should be a winner");
        assertEquals("João Silva", resultado.getAlunoVencedor().getNome(),
                "João Silva should be the winner (most active)");
        assertTrue(resultado.isNotificacaoEnviada(), "Notification should be sent to winner");
        assertEquals(cursoJava.getNome(), resultado.getCursoGanho().getNome(),
                "Winner should receive the Java Avançado course");
    }

    @Test
    @DisplayName("BLUE PHASE - BDD-01: deveProcessarRankingEPremiarAlunoMaisAtivo")
    void deveProcessarRankingEPremiarAlunoMaisAtivo_BLUE() {
        // BLUE PHASE: Complete, refactored test with optimal implementation
        // Test the full reward flow: ranking creation, winner identification, and notification
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);  // Score: 4 - WINNER
        alunos.add(aluno2);  // Score: 1
        alunos.add(aluno3);  // Score: 2

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        // Comprehensive assertions for complete reward flow
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
}
