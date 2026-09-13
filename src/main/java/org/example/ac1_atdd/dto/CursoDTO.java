package org.example.ac1_atdd.dto;

public class CursoDTO {
    private Long id;
    private String nome;
    private String tipo;

    public CursoDTO() {}

    public CursoDTO(Long id, String nome, String tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}