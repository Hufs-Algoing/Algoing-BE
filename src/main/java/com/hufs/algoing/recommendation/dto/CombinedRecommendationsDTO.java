package com.hufs.algoing.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CombinedRecommendationsDTO {
    private String recommendationSessionId; // 추천 받은 페이지 세션 ID
    private List<DailyRecommendDTO> dailyRecommendations;
    private List<WeaknessRecommendDTO> weaknessRecommendations;
    private List<IncProblemRecommendDTO> incProblemRecommendations;
}
