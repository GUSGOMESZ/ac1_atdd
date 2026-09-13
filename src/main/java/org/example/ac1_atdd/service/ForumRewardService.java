package org.example.ac1_atdd.service;

import org.example.ac1_atdd.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForumRewardService {

    public Resultado processarRankingMensal(List<Aluno> alunos, Curso cursoGanho) {
        return processarRankingMensal(alunos, cursoGanho, 3L);
    }

    public Resultado processarRankingMensal(List<Aluno> alunos, Curso cursoGanho, Long minimo) {
        Resultado resultado = new Resultado();
        resultado.setCursoGanho(cursoGanho);

        if (alunos == null || alunos.isEmpty()) {
            resultado.setNotificacaoEnviada(false);
            return resultado;
        }

        // Calculate ranking items for all students
        List<RankingItem> rankingItems = criarRankingItems(alunos);

        // Sort ranking by score (descending)
        List<RankingItem> rankingOrdenado = rankingItems.stream()
                .sorted((a, b) -> Long.compare(b.getPontuacao(), a.getPontuacao()))
                .collect(Collectors.toList());

        // Add all ranking items to resultado (sorted)
        for (RankingItem item : rankingOrdenado) {
            resultado.adicionarRankingItem(item);
        }

        // Filter by minimum score to determine winners
        List<RankingItem> rankingFiltrado = rankingOrdenado.stream()
                .filter(item -> item.getPontuacao() >= minimo)
                .collect(Collectors.toList());

        // Determine winners - all students with highest score that meet minimum
        if (!rankingFiltrado.isEmpty()) {
            RankingItem vencedor = rankingFiltrado.get(0);
            resultado.setAlunoVencedor(vencedor.getAluno());
            resultado.adicionarAlunoVencedor(vencedor.getAluno());

            // Add ALL students that meet minimum score requirement
            for (RankingItem item : rankingFiltrado) {
                if (!item.getAluno().equals(vencedor.getAluno())) {
                    resultado.adicionarAlunoVencedor(item.getAluno());
                }
            }

            enviarNotificacao(resultado);
            resultado.setNotificacaoEnviada(true);
        }

        return resultado;
    }

    private List<RankingItem> criarRankingItems(List<Aluno> alunos) {
        return alunos.stream()
                .map(aluno -> {
                    long pontuacao = calcularPontuacao(aluno);
                    return new RankingItem(aluno, pontuacao);
                })
                .collect(Collectors.toList());
    }

    private long calcularPontuacao(Aluno aluno) {
        long comentariosUteis = aluno.getComentariosUteis();
        long comentariosNaoUteis = aluno.getComentariosNaoUteis();
        return (comentariosUteis * 1) - (comentariosNaoUteis * 1);
    }

    private void enviarNotificacao(Resultado resultado) {
        if (resultado != null && resultado.getAlunoVencedor() != null) {
            String mensagem = String.format(
                    "Parabéns %s! Você ganhou o curso %s",
                    resultado.getAlunoVencedor().getNome(),
                    resultado.getCursoGanho().getNome()
            );
            System.out.println("[NOTIFICAÇÃO] " + mensagem);
        }
    }

    public Resultado criarResultadoComPremio(Aluno aluno, Curso curso) {
        Resultado resultado = new Resultado();
        resultado.setAlunoVencedor(aluno);
        resultado.setCursoGanho(curso);
        resultado.setNotificacaoEnviada(true);
        return resultado;
    }

    public boolean ehVencedor(RankingItem item, long minimo) {
        return item.getPontuacao() >= minimo;
    }

    public List<Aluno> obterTopAlunos(List<Aluno> alunos, int top) {
        return alunos.stream()
                .sorted((a, b) -> Long.compare(calcularPontuacao(b), calcularPontuacao(a)))
                .limit(top)
                .collect(Collectors.toList());
    }
}