package com.hufs.algoing.problem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SubmittedProblemDTO {
    private LocalDate date;
    private Long count;
}
