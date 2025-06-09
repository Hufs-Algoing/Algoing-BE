package com.hufs.algoing.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DailyRecommendDTO {

    private Long problemId;
    private String title;
    private int level;
    private String tagNames;
    private double score;
    
}
