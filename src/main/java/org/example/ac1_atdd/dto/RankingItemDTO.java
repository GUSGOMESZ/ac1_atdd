// RankingItemDTO.java
package org.example.ac1_atdd.dto;

public class RankingItemDTO {
    private Long id;
    private Long alunoId;
    private String alunoNome;
    private long pontuacao;
    private long comentariosContabilizados;
    private long comentariosNaoUteis;

    public RankingItemDTO() {}

    public RankingItemDTO(Long id, Long alunoId, String alunoNome, long pontuacao, long comentariosContabilizados, long comentariosNaoUteis) {
        this.id = id;
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.pontuacao = pontuacao;
        this.comentariosContabilizados = comentariosContabilizados;
        this.comentariosNaoUteis = comentariosNaoUteis;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAlunoId() { return alunoId; }
    public void setAlunoId(Long alunoId) { this.alunoId = alunoId; }
    public String getAlunoNome() { return alunoNome; }
    public void setAlunoNome(String alunoNome) { this.alunoNome = alunoNome; }
    public long getPontuacao() { return pontuacao; }
    public void setPontuacao(long pontuacao) { this.pontuacao = pontuacao; }
    public long getComentariosContabilizados() { return comentariosContabilizados; }
    public void setComentariosContabilizados(long comentariosContabilizados) { this.comentariosContabilizados = comentariosContabilizados; }
    public long getComentariosNaoUteis() { return comentariosNaoUteis; }
    public void setComentariosNaoUteis(long comentariosNaoUteis) { this.comentariosNaoUteis = comentariosNaoUteis; }
}