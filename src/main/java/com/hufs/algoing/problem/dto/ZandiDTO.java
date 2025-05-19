package com.hufs.algoing.problem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ZandiDTO {
    private LocalDate date;
    private Long count;
}
