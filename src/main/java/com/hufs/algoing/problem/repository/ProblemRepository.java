package com.hufs.algoing.problem.repository;

import com.hufs.algoing.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    boolean existsByProblemId(Long problemId);

    Optional<Object> findByProblemId(Long problemId);


    @Query(value = "SELECT * FROM problem WHERE " +
            "UPPER(title) LIKE UPPER(CONCAT('%', :keyword, '%')) OR " +
            "UPPER(tag_names) LIKE UPPER(CONCAT('%', :keyword, '%')) OR " +
            "UPPER(description) LIKE UPPER(CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    List<Problem> searchByKeyword(@Param("keyword") String keyword);




}
