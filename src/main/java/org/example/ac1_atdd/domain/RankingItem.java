package org.example.ac1_atdd.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "ranking_items")
public class RankingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false)
    private long pontuacao;

    @Column(name = "comentarios_contabilizados", nullable = false)
    private long comentariosContabilizados;

    @Column(name = "comentarios_nao_uteis", nullable = false)
    private long comentariosNaoUteis;

    public RankingItem() {}

    public RankingItem(Aluno aluno, long pontuacao) {
        this.aluno = aluno;
        this.pontuacao = pontuacao;
        this.comentariosContabilizados = aluno.getComentariosUteis();
        this.comentariosNaoUteis = aluno.getComentariosNaoUteis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public long getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(long pontuacao) {
        this.pontuacao = pontuacao;
    }

    public long getComentariosContabilizados() {
        return comentariosContabilizados;
    }

    public void setComentariosContabilizados(long comentariosContabilizados) {
        this.comentariosContabilizados = comentariosContabilizados;
    }

    public long getComentariosNaoUteis() {
        return comentariosNaoUteis;
    }

    public void setComentariosNaoUteis(long comentariosNaoUteis) {
        this.comentariosNaoUteis = comentariosNaoUteis;
    }
}