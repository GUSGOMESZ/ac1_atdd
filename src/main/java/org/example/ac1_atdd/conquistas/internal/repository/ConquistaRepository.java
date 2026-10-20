package org.example.ac1_atdd.conquistas.internal.repository;

import org.example.ac1_atdd.conquistas.internal.domain.Conquista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConquistaRepository extends JpaRepository<Conquista, Long> {
    Optional<Conquista> findByNome(String nome);
}
