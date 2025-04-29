package com.hufs.algoing.recommendation.controller;

import com.hufs.algoing.recommendation.dto.DailyRecommendDTO;
import com.hufs.algoing.recommendation.service.DailyRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dailyRecommendations")
@RequiredArgsConstructor
public class DailyRecommendController {

    private final DailyRecommendService dailyRecommendService;

    // 일일 추천 문제 조회
    @GetMapping("/{userId}")
    public List<DailyRecommendDTO> getDailyRecommendations(@PathVariable Long userId) {
        // 서비스 호출하여 추천 문제 목록 가져오기
        List<DailyRecommendDTO> recommendations = dailyRecommendService.getDailyRecommendations(userId);
        recommendations.forEach(recommendation -> System.out.println(recommendation.toString()));

        return recommendations;
    }
}
