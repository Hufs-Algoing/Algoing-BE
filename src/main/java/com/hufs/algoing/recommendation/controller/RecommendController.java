package com.hufs.algoing.recommendation.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.recommendation.dto.CombinedRecommendationsDTO;
import com.hufs.algoing.recommendation.dto.DailyRecommendDTO;
import com.hufs.algoing.recommendation.dto.IncProblemRecommendDTO;
import com.hufs.algoing.recommendation.dto.WeaknessRecommendDTO;
import com.hufs.algoing.recommendation.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Recommend API", description = "추천 API")
@RestController
@RequestMapping(value = "/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @Operation(summary = "일간 추천", description = "요청 받은 사용자의 티어와 선호 문제 유형을 기반으로 문제를 추천합니다.")
    @Parameter(name = "userId", description = "추천 받을 사용자 id")
    @GetMapping("daily/{userId}")
    public ApiResponse<List<DailyRecommendDTO>> getDailyRecommendations(@PathVariable Long userId) {
        // 단일 추천
        String recommendationSessionId = UUID.randomUUID().toString();
        // 서비스 호출하여 추천 문제 목록 가져오기
        List<DailyRecommendDTO> recommendations = recommendService.getDailyRecommendations(userId, recommendationSessionId);
        recommendations.forEach(recommendation -> System.out.println(recommendation.toString()));

        return ApiResponse.onSuccess(recommendations);
    }

    @Operation(summary = "약점 보완 추천", description = "요청 받은 사용자의 약점을 기반으로 문제를 추천합니다.")
    @Parameter(name = "userId", description = "추천 받을 사용자 id")
    @GetMapping("weakness/{userId}")
    public ApiResponse<List<WeaknessRecommendDTO>> getWeaknessRecommendations(@PathVariable Long userId){
        // 단일 추천
        String recommendationSessionId = UUID.randomUUID().toString();
        List<WeaknessRecommendDTO> recommendations = recommendService.getWeaknessRecommendations(userId, recommendationSessionId);
        recommendations.forEach(recommendation -> System.out.println(recommendation));
        return ApiResponse.onSuccess(recommendations);
    }

    @Operation(summary = "틀린 문제 유형 기반 추천", description = "요청 받은 사용자의 많이 틀린 문제 유형 기반으로 문제를 추천합니다.")
    @Parameter(name = "userId", description = "추천 받을 사용자 id")
    @GetMapping("incproblem/{userId}")
    public ApiResponse<List<IncProblemRecommendDTO>> getIncProblemRecommendation(@PathVariable Long userId) {
        // 단일 추천
        String recommendationSessionId = UUID.randomUUID().toString();
        List<IncProblemRecommendDTO> recommendations = recommendService.getIncProblemRecommendation(userId, recommendationSessionId);
        recommendations.forEach(recommendation -> System.out.println(recommendation.toString()));

        return ApiResponse.onSuccess(recommendations);
    }

    @Operation(summary = "모든 추천 가져오기", description = "요청 받은 사용자를 위한 모든 추천 유형(Daily, Weakness, IncProblem)을 한 번에 추천합니다.")
    @Parameter(name = "userId", description = "추천 받을 사용자 id", example = "1")
    @GetMapping("/all/{userId}")
    public ApiResponse<CombinedRecommendationsDTO> getAllRecommendationsForUser(@PathVariable Long userId) {
        CombinedRecommendationsDTO response = recommendService.getAllRecommendations(userId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "추천 문제 클릭 기록", description = "사용자가 추천된 문제를 클릭했음을 기록합니다.")
    @Parameter(name = "userId", description = "클릭한 사용자 ID", example = "1")
    @Parameter(name = "problemId", description = "클릭된 문제 ID", example = "1000")
    @Parameter(name = "sessionId", description = "추천 세션 ID (클릭된 문제가 속한 세션)", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    @PostMapping("/click")
    public ApiResponse<String> recordClick(@RequestParam Long userId, @RequestParam Long problemId, @RequestParam String sessionId) {
        recommendService.recordRecommendationClick(userId, problemId, sessionId);
        return ApiResponse.onSuccess("클릭을 성공적으로 기록했습니다");
    }
}
