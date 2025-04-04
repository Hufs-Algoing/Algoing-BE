package com.hufs.algoing.problem.entity;

import com.hufs.algoing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_solved_problem")
public class UserSolvedProblem {
    @Id
    @Column(name = "user_solved_problem_id", nullable = false, unique = true)
    private Long userSolvedId;

    @ManyToOne(cascade = CascadeType.MERGE, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne(cascade = CascadeType.MERGE, targetEntity = Problem.class)
    @JoinColumn(name = "problem_num")
    private Problem problemNum;

    @Column
    @Lob
    private String answer;

    @Column(name = "solved_at")
    private LocalDateTime solvedAt;

    @Builder
    public UserSolvedProblem(Long userSolvedId, User userId, Problem problemNum, String answer, LocalDateTime solvedAt) {
        this.userSolvedId = userSolvedId;
        this.userId = userId;
        this.problemNum = problemNum;
        this.answer = answer;
        this.solvedAt = solvedAt;
    }

}
