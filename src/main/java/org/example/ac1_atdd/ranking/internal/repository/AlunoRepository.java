package org.example.ac1_atdd.ranking.internal.repository;

import org.example.ac1_atdd.ranking.internal.domain.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByEmail(String email);
    Optional<Aluno> findByNome(String nome);
}
