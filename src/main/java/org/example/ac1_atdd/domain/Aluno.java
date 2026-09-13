package org.example.ac1_atdd.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alunos")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    public Aluno() {}

    public Aluno(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public void adicionarComentarioUtil(Comentario comentario) {
        if (comentario != null) {
            comentario.setUtil(true);
            comentario.setAluno(this);
            this.comentarios.add(comentario);
        }
    }

    public void adicionarComentarioNaoUtil(Comentario comentario) {
        if (comentario != null) {
            comentario.setUtil(false);
            comentario.setAluno(this);
            this.comentarios.add(comentario);
        }
    }

    public long getComentariosUteis() {
        return this.comentarios.stream()
                .filter(Comentario::isUtil)
                .count();
    }

    public long getComentariosNaoUteis() {
        return this.comentarios.stream()
                .filter(c -> !c.isUtil())
                .count();
    }
}
