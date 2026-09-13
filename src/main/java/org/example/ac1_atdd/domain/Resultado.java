package org.example.ac1_atdd.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resultados")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_vencedor_id")
    private Aluno alunoVencedor;

    @ManyToOne
    @JoinColumn(name = "curso_ganho_id")
    private Curso cursoGanho;

    @Column(nullable = false)
    private boolean notificacaoEnviada = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "resultado_id")
    private List<RankingItem> ranking = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "resultado_alunos_vencedores",
            joinColumns = @JoinColumn(name = "resultado_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private List<Aluno> alunosVencedores = new ArrayList<>();

    public Resultado() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aluno getAlunoVencedor() {
        return alunoVencedor;
    }

    public void setAlunoVencedor(Aluno alunoVencedor) {
        this.alunoVencedor = alunoVencedor;
    }

    public Curso getCursoGanho() {
        return cursoGanho;
    }

    public void setCursoGanho(Curso cursoGanho) {
        this.cursoGanho = cursoGanho;
    }

    public boolean isNotificacaoEnviada() {
        return notificacaoEnviada;
    }

    public void setNotificacaoEnviada(boolean notificacaoEnviada) {
        this.notificacaoEnviada = notificacaoEnviada;
    }

    public List<RankingItem> getRanking() {
        return ranking;
    }

    public void setRanking(List<RankingItem> ranking) {
        this.ranking = ranking;
    }

    public List<Aluno> getAlunosVencedores() {
        return alunosVencedores;
    }

    public void setAlunosVencedores(List<Aluno> alunosVencedores) {
        this.alunosVencedores = alunosVencedores;
    }

    public void adicionarAlunoVencedor(Aluno aluno) {
        if (!this.alunosVencedores.contains(aluno)) {
            this.alunosVencedores.add(aluno);
        }
    }

    public void adicionarRankingItem(RankingItem item) {
        this.ranking.add(item);
    }
}
