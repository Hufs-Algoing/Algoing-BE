package com.hufs.algoing.aisolved.repository;

import com.hufs.algoing.aisolved.entity.AISolved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AISolvedRepository extends JpaRepository<AISolved, Long> {

     AISolved findByProblem_ProblemNum(Long problemNum);
}
