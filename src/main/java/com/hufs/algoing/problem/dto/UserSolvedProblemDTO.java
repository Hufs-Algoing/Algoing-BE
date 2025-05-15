package com.hufs.algoing.problem.dto;

import com.hufs.algoing.problem.entity.Problem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSolvedProblemDTO {

    private Long userSolvedId;
    private Long userId;
    private Long problemId;
    private String title;
    private String answer;
    private String tag;
    private Long level;
    private LocalDateTime solvedAt;
}