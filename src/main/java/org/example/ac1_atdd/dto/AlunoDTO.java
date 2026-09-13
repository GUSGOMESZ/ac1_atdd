// AlunoDTO.java
package org.example.ac1_atdd.dto;

public class AlunoDTO {
    private Long id;
    private String nome;
    private String email;
    private long comentariosUteis;
    private long comentariosNaoUteis;
    private long pontuacao;

    public AlunoDTO() {}

    public AlunoDTO(Long id, String nome, String email, long comentariosUteis, long comentariosNaoUteis) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.comentariosUteis = comentariosUteis;
        this.comentariosNaoUteis = comentariosNaoUteis;
        this.pontuacao = comentariosUteis - comentariosNaoUteis;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public long getComentariosUteis() { return comentariosUteis; }
    public void setComentariosUteis(long comentariosUteis) { this.comentariosUteis = comentariosUteis; }
    public long getComentariosNaoUteis() { return comentariosNaoUteis; }
    public void setComentariosNaoUteis(long comentariosNaoUteis) { this.comentariosNaoUteis = comentariosNaoUteis; }
    public long getPontuacao() { return pontuacao; }
    public void setPontuacao(long pontuacao) { this.pontuacao = pontuacao; }
}