package com.hufs.algoing.problem.dto;

import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.entity.SubmittedProblem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedProblemDTO {

    private Long submittedProblemId;
    private Long userId;
    private Long problemId;
    private String title;
    private String answer;
    private String tag;
    private Long level;
    private String language;
    private LocalDateTime submittedAt;
    private LocalDate submittedDate;


    public SubmittedProblemDTO(SubmittedProblem submittedProblem) {
        this.submittedProblemId = submittedProblem.getSubmittedProblemId();
        this.userId = submittedProblem.getUserId().getUserId();
        Problem problem = submittedProblem.getProblemId();
        this.problemId = problem.getProblemId();
        this.title = problem.getTitle();
        this.answer = submittedProblem.getAnswer();
        this.tag = problem.getTag();
        this.level = problem.getLevel();
        this.language = submittedProblem.getLanguage();
        this.submittedAt = submittedProblem.getSubmittedAt();
        this.submittedDate = submittedProblem.getSubmittedDate();
    }
}
