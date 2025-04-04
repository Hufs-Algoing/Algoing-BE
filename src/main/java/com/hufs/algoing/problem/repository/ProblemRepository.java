package com.hufs.algoing.problem.repository;

import com.hufs.algoing.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

}
