// ResultadoDTO.java
package org.example.ac1_atdd.dto;

import java.util.List;

public class ResultadoDTO {
    private Long id;
    private AlunoDTO alunoVencedor;
    private CursoDTO cursoGanho;
    private boolean notificacaoEnviada;
    private List<RankingItemDTO> ranking;
    private List<AlunoDTO> alunosVencedores;

    public ResultadoDTO() {}

    public ResultadoDTO(Long id, AlunoDTO alunoVencedor, CursoDTO cursoGanho, boolean notificacaoEnviada, List<RankingItemDTO> ranking, List<AlunoDTO> alunosVencedores) {
        this.id = id;
        this.alunoVencedor = alunoVencedor;
        this.cursoGanho = cursoGanho;
        this.notificacaoEnviada = notificacaoEnviada;
        this.ranking = ranking;
        this.alunosVencedores = alunosVencedores;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AlunoDTO getAlunoVencedor() { return alunoVencedor; }
    public void setAlunoVencedor(AlunoDTO alunoVencedor) { this.alunoVencedor = alunoVencedor; }
    public CursoDTO getCursoGanho() { return cursoGanho; }
    public void setCursoGanho(CursoDTO cursoGanho) { this.cursoGanho = cursoGanho; }
    public boolean isNotificacaoEnviada() { return notificacaoEnviada; }
    public void setNotificacaoEnviada(boolean notificacaoEnviada) { this.notificacaoEnviada = notificacaoEnviada; }
    public List<RankingItemDTO> getRanking() { return ranking; }
    public void setRanking(List<RankingItemDTO> ranking) { this.ranking = ranking; }
    public List<AlunoDTO> getAlunosVencedores() { return alunosVencedores; }
    public void setAlunosVencedores(List<AlunoDTO> alunosVencedores) { this.alunosVencedores = alunosVencedores; }
}
