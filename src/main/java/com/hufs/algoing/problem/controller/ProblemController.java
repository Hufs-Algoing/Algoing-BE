package com.hufs.algoing.problem.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Problem API", description = "문제 API")
@RestController
@RequestMapping("/api/problems/")
@RequiredArgsConstructor
public class ProblemController {

//    @Autowired
    private final ProblemService problemService;


    @Operation(summary = "문제 조회", description = "문제 ID를 기반으로 문제를 조회합니다.")
    @GetMapping("/{problemId}")
    public ApiResponse<Problem> getProblemId(@PathVariable Long problemId){
        return ApiResponse.onSuccess(problemService.getOrCrawlProblem(problemId));
    }

    @Operation(summary = "태그별 문제 크롤링", description = "태그, 티어 범위를 지정해 여러 문제를 크롤링하고 저장합니다.")
    @PostMapping("/crawl")
    public ApiResponse<String> crawlProblemsByTagAndTier(@RequestParam String tag) {
        problemService.crawlProblemsByTagWithTier(tag);
        return ApiResponse.onSuccess("크롤링 완료");
    }

    @Operation(summary = "문제 검색", description = "제목 또는 태그이름으로 문제를 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<List<Problem>> searchProblems(@RequestParam String keyword) {
        List<Problem> result = problemService.searchByTitleOrTagNamesOrDescriptionOrId(keyword);
        return ApiResponse.onSuccess(result);
    }




}
