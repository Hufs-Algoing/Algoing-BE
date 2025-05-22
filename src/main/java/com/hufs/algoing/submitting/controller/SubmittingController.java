package com.hufs.algoing.submitting.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.problem.entity.ProblemStatus;
import com.hufs.algoing.submitting.service.SubmittingService;
import com.hufs.algoing.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Submitting API", description = "제출 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submit/")
public class SubmittingController {

    private final SubmittingService submittingService;

    @Operation(summary = "제출 API", description = "문제 ID를 기반으로 코드를 제출합니다.")
    @PostMapping("/{problemId}")
    public ApiResponse<ProblemStatus> submit(@PathVariable Long problemId, @RequestBody String code, @RequestParam String language, @AuthenticationPrincipal User p) throws InterruptedException {
//        String handle = p.getHandle();
        return ApiResponse.onSuccess(submittingService.submit(problemId, code, language, p));
    }


}
