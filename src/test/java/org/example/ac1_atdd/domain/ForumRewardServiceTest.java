    // ==================== BDD-03: Apenas comentários úteis contam ====================

    /*
    @Test
    @DisplayName("RED PHASE - BDD-03: deveContabilizarApenasComentariosUteis")
    void deveContabilizarApenasComentariosUteis_RED() {
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);
        alunos.add(aluno2);
        alunos.add(aluno3);

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        // RED: Ranking items não têm pontuação calculada ainda
        assertNotNull(resultado.getRanking(), "Ranking should exist");
        assertNull(resultado.getRanking().get(0).getPontuacao(), "Score not calculated in RED phase");
    }
     */

    @Test
    @DisplayName("RED PHASE - BDD-03: deveContabilizarApenasComentariosUteis")
    void deveContabilizarApenasComentariosUteis_RED() {
        // RED PHASE: Test that ranking system is implemented
        // This test verifies that the service creates a ranking
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);
        alunos.add(aluno2);
        alunos.add(aluno3);

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        // Verify ranking exists
        assertNotNull(resultado.getRanking(), "Ranking should exist");
        assertTrue(resultado.getRanking().size() > 0, "Ranking should contain items");
    }

    @Test
    @DisplayName("GREEN PHASE - BDD-03: deveContabilizarApenasComentariosUteis")
    void deveContabilizarApenasComentariosUteis_GREEN() {
        // GREEN PHASE: Test that scoring correctly accounts for useful and non-useful comments
        // Score = useful comments - non-useful comments
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno1);  // Score: 5 useful - 1 not useful = 4
        alunos.add(aluno2);  // Score: 3 useful - 2 not useful = 1
        alunos.add(aluno3);  // Score: 2 useful - 0 not useful = 2

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        // Verify ranking is sorted by score (useful minus non-useful)
        List<RankingItem> ranking = resultado.getRanking();

        // Find the top student's ranking item
        RankingItem topItem = ranking.stream()
                .filter(r -> r.getAluno().getId().equals(1L))
                .findFirst()
                .orElse(null);

        assertNotNull(topItem, "Top student should be in ranking");
        assertEquals(4, topItem.getPontuacao(),
                "João's score should be 4 (5 useful - 1 not useful)");
        assertEquals(5, topItem.getComentariosContabilizados(),
                "Should have 5 useful comments counted");
        assertEquals(1, topItem.getComentariosNaoUteis(),
                "Should have 1 non-useful comment counted");
    }

    @Test
    @DisplayName("BLUE PHASE - BDD-03: deveContabilizarApenasComentariosUteis")
    void deveContabilizarApenasComentariosUteis_BLUE() {
        List<Aluno> alunos = new ArrayList<>();
        alunos.add(aluno2);  // Score: 1 (3 useful - 2 not useful)
        alunos.add(aluno3);  // Score: 2 (2 useful - 0 not useful)
        alunos.add(aluno1);  // Score: 4 (5 useful - 1 not useful)

        Resultado resultado = forumRewardService.processarRankingMensal(alunos, cursoJava);

        List<RankingItem> ranking = resultado.getRanking();
        assertEquals(3, ranking.size(), "All students should be in ranking");

        assertEquals(4, ranking.get(0).getPontuacao(),
                "Highest score should be first (João with 4)");
        assertEquals(2, ranking.get(1).getPontuacao(),
                "Second highest should be 2 (Pedro)");
        assertEquals(1, ranking.get(2).getPontuacao(),
                "Lowest score should be 1 (Maria)");

        RankingItem joaoItem = ranking.get(0);
        assertEquals(5, joaoItem.getComentariosContabilizados(),
                "João has 5 useful comments");
        assertEquals(1, joaoItem.getComentariosNaoUteis(),
                "João has 1 non-useful comment");
        assertEquals(4, (joaoItem.getComentariosContabilizados() - joaoItem.getComentariosNaoUteis()),
                "Score formula: 5 - 1 = 4");
    }