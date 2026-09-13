package org.example.ac1_atdd.repository;

import org.example.ac1_atdd.domain.RankingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RankingItemRepository extends JpaRepository<RankingItem, Long> {
    List<RankingItem> findAllByOrderByPontuacaoDesc();
}