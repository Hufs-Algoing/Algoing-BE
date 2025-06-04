package com.hufs.algoing.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IncProblemRecommendDTO {
    Long problemId;
    String title;
    String tag;
    Long level;
}
