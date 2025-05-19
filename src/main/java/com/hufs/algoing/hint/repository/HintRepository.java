package com.hufs.algoing.hint.repository;

import com.hufs.algoing.hint.entity.Hint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HintRepository extends JpaRepository<Hint, Long> {
    Optional<Hint> findHintByAiSolved_Problem_ProblemIdAndOrder(Long problemId, int order);
}
