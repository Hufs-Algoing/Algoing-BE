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
    private String tag;
    private double score;

    // problemId, score만 받는 생성자
    public DailyRecommendDTO(Long problemId, double score) {
        this.problemId = problemId;
        this.score = score;
        this.title = null;
        this.level = 0;
        this.tag = null;
    }

    @Override
    public String toString() {
        return "DailyRecommendDTO{" +
                "problemId=" + problemId +
                ", title='" + title + '\'' +
                ", level=" + level +
                ", tag='" + tag + '\'' +
                ", score=" + score +
                '}';
    }
}
