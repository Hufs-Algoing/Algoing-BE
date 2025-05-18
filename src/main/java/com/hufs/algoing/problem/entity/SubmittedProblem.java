package com.hufs.algoing.problem.entity;

import com.hufs.algoing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "submitted_problem")
public class SubmittedProblem {
    @Id
    @Column(name = "submitted_problem_id", nullable = false, unique = true)
    private Long submittedProblemId;

    @ManyToOne(cascade = CascadeType.MERGE, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne(cascade = CascadeType.MERGE, targetEntity = Problem.class)
    @JoinColumn(name = "problem_id")
    private Problem problemId;

    @Column
    @Lob
    private String answer;

    @Column
    private String language;

    @Column
    private ProblemStatus status;

    @Column
    private LocalDateTime submittedAt;

    @Column
    private LocalDate submittedDate;

    @Builder
    public SubmittedProblem(
            Long submittedProblemId, User userId, Problem problemId,
            String answer, LocalDateTime submittedAt, LocalDate submittedDate, String language,
            ProblemStatus status) {
        this.submittedProblemId = submittedProblemId;
        this.userId = userId;
        this.problemId = problemId;
        this.answer = answer;
        this.submittedAt = submittedAt;
        this.language = language;
        this.status = status;
        this.submittedDate = submittedDate;
    }


}
