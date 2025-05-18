package com.hufs.algoing.problem.repository;

import com.hufs.algoing.problem.dto.SubmittedProblemDTO;
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

//    List<SubmittedProblem> findByUserIdOrderBySubmittedAtAsc(User userId);

    @Query("SELECT new com.hufs.algoing.problem.dto.SubmittedProblemDTO(s.submittedDate, COUNT(s)) " +
                    "FROM SubmittedProblem s " +
                    "WHERE s.userId = :userId " +
                    "AND s.status = 1 " +
                    "GROUP BY s.submittedDate " +
                    "ORDER BY s.submittedDate ASC")
    List<SubmittedProblemDTO> findGroupedByDate
            (@Param("userId") User userId);

}
