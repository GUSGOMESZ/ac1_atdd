package org.example.ac1_atdd.conquistas.api;

public class ConquistaDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String criterio;

    public ConquistaDTO() {}

    public ConquistaDTO(Long id, String nome, String descricao, String criterio) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.criterio = criterio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCriterio() { return criterio; }
    public void setCriterio(String criterio) { this.criterio = criterio; }
}
