package org.example.ac1_atdd.repository;

import org.example.ac1_atdd.domain.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    Optional<Curso> findByNome(String nome);
    Optional<Curso> findByTipo(String tipo);
}