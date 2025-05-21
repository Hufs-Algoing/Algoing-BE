package com.hufs.algoing.recommendation.dto;

import com.hufs.algoing.problem.entity.Problem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class WeaknessRecommendDTO {
    private Problem problem;
    private double finalScore;

    @Override
    public String toString() {
        return "WeaknessRecommendDTO{" +
                "problem=" + problem.getProblemId() +
                ", finalScore=" + finalScore +
                '}';}
}
