package com.hufs.algoing.recommendation.controller;

import com.hufs.algoing.recommendation.dto.DailyRecommendDTO;
import com.hufs.algoing.recommendation.service.DailyRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "데일리 문제 추천 API", description = "데일리 문제 추천 API")
@RestController
@RequestMapping("/dailyRecommendations")
@RequiredArgsConstructor
public class DailyRecommendController {

    private final DailyRecommendService dailyRecommendService;

    // 일일 추천 문제 조회
    @Operation(summary = "userId", description = "요청 받은 사용자의 티어와 선호 문제 유형을 기반으로 문제를 추천합니다.")
    @Parameter(name = "long", description = "추천 받을 사용자 id")
    @GetMapping("/{userId}")
    public List<DailyRecommendDTO> getDailyRecommendations(@PathVariable Long userId) {
        // 서비스 호출하여 추천 문제 목록 가져오기
        List<DailyRecommendDTO> recommendations = dailyRecommendService.getDailyRecommendations(userId);
        recommendations.forEach(recommendation -> System.out.println(recommendation.toString()));

        return recommendations;
    }
}
