package com.hufs.algoing.problem.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.problem.entity.Problem;
import com.hufs.algoing.problem.service.ProblemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
