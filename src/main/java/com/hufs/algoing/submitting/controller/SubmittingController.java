package com.hufs.algoing.submitting.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.problem.entity.ProblemStatus;
import com.hufs.algoing.submitting.service.SubmittingService;
import com.hufs.algoing.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submit/")
public class SubmittingController {

    private final SubmittingService submittingService;

    @PostMapping("/{problemId}")
    public ApiResponse<ProblemStatus> submit(@PathVariable Long problemId, @RequestBody String code, @RequestParam String language, @AuthenticationPrincipal User p) throws InterruptedException {
//        String handle = p.getHandle();
        return ApiResponse.onSuccess(submittingService.submit(problemId, code, language, p));
    }


}
