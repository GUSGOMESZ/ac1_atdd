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

        List<RankingItem> rankingOrdenado = criarRankingItems(alunos).stream()
                .sorted((a, b) -> Long.compare(b.getPontuacao(), a.getPontuacao()))
                .collect(Collectors.toList());

        rankingOrdenado.forEach(resultado::adicionarRankingItem);

        rankingOrdenado.stream()
                .filter(item -> item.getPontuacao() >= minimo)
                .collect(Collectors.toList())
                .stream()
                .findFirst()
                .ifPresent(vencedor -> {
                    resultado.setAlunoVencedor(vencedor.getAluno());
                    resultado.adicionarAlunoVencedor(vencedor.getAluno());
                    rankingOrdenado.stream()
                            .filter(item -> item.getPontuacao() >= minimo
                                    && !item.getAluno().equals(vencedor.getAluno()))
                            .forEach(item -> resultado.adicionarAlunoVencedor(item.getAluno()));
                    enviarNotificacao(resultado);
                    resultado.setNotificacaoEnviada(true);
                });

        return resultado;
    }

    private List<RankingItem> criarRankingItems(List<Aluno> alunos) {
        return alunos.stream()
                .map(aluno -> new RankingItem(aluno, calcularPontuacao(aluno)))
                .collect(Collectors.toList());
    }

    private long calcularPontuacao(Aluno aluno) {
        return aluno.getComentariosUteis() - aluno.getComentariosNaoUteis();
    }

    private void enviarNotificacao(Resultado resultado) {
        String mensagem = String.format(
                "Parabéns %s! Você ganhou o curso %s",
                resultado.getAlunoVencedor().getNome(),
                resultado.getCursoGanho().getNome()
        );
        System.out.println("[NOTIFICAÇÃO] " + mensagem);
    }
}
