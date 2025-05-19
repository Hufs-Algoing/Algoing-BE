package com.hufs.algoing.problem.entity;

import com.hufs.algoing.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table
public class SubmittedProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
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
    @Enumerated(EnumType.STRING)
    private ProblemStatus status;

    @CreatedDate
    @Column
    private LocalDateTime submittedAt;

    @Column
    private LocalDate submittedDate;

    // submittedDate는 submittedAt에서 자동 생성
    @PrePersist
    public void prePersist() {
        this.submittedDate = LocalDate.from(submittedAt);
    }

    @Builder
    public SubmittedProblem(
            User userId, Problem problemId,
            String answer, LocalDateTime submittedAt, LocalDate submittedDate, String language,
            ProblemStatus status) {
        this.userId = userId;
        this.problemId = problemId;
        this.answer = answer;
        this.submittedAt = LocalDateTime.now();
        this.language = language;
        this.status = status;
        this.submittedDate = LocalDate.now();
    }


}
