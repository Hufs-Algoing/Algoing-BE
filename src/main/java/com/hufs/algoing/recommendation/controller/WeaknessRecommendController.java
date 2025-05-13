package com.hufs.algoing.recommendation.controller;

import com.hufs.algoing.recommendation.service.WeaknessRecommendService;
import com.hufs.algoing.recommendation.dto.WeaknessRecommendDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "약점 기반 문제 추천 API", description = "약점 기반 문제 추천 API")
@RestController
@RequestMapping("/weaknessRecommendations")
@RequiredArgsConstructor

public class WeaknessRecommendController {

    private final WeaknessRecommendService weaknessRecommendService;

    @Operation(summary = "userId", description = "요청 받은 사용자의 약점을 기반으로 문제를 추천합니다.")
    @Parameter(name = "long", description = "추천 받을 사용자 id")
    @GetMapping("/{userId}")
    public List<WeaknessRecommendDTO> getWeaknessRecommendations(@PathVariable Long userId){
        List<WeaknessRecommendDTO> recommendations = weaknessRecommendService.getWeaknessRecommendations(userId);
        recommendations.forEach(recommendation -> System.out.println(recommendation));
        return recommendations;
    }

}
