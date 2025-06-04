package com.hufs.algoing.recommendation.dto;

import com.hufs.algoing.problem.entity.Problem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeaknessRecommendDTO {
    private Long problemId;
    private String title;
    private String tag;
    private Long level;
    private double finalScore;

}
