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
        // 서비스 호출 전 로그 추가
        System.out.println("Fetching daily recommendations for userId: " + userId);

        // 서비스 호출하여 추천 문제 목록 가져오기
        List<DailyRecommendDTO> recommendations = dailyRecommendService.getDailyRecommendations(userId);

        // 추천 문제 목록 로그 출력 (각 문제의 상세 정보를 확인할 수 있음)
        System.out.println("Daily recommendations: ");
        recommendations.forEach(recommendation -> System.out.println(recommendation.toString()));

        return recommendations;
    }
}
