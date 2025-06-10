package com.hufs.algoing.problem.repository;

import com.hufs.algoing.problem.dto.ZandiDTO;
import com.hufs.algoing.problem.entity.ProblemStatus;
import com.hufs.algoing.problem.entity.SubmittedProblem;
import com.hufs.algoing.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmittedProblemRepository extends JpaRepository<SubmittedProblem, Long> {

    List<SubmittedProblem> findByUserId(User userId);

    @Query("SELECT new com.hufs.algoing.problem.dto.ZandiDTO(s.submittedDate, COUNT(s)) " +
            "FROM SubmittedProblem s " +
            "WHERE s.userId = :userId " +
            "AND s.status = :status " +
            "AND s.submittedDate = (" +
            "SELECT MIN(s2.submittedDate) " +
            "FROM SubmittedProblem s2 " +
            "WHERE s2.userId = :userId " +
            "AND s2.status = :status " +
            "AND s2.problemId = s.problemId" +
            ") " +
            "GROUP BY s.submittedDate " +
            "ORDER BY s.submittedDate ASC")
    List<ZandiDTO> findGroupedByDate
            (@Param("userId") User userId,
             @Param("status") ProblemStatus status);



}
