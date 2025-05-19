package com.hufs.algoing.hint.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.hint.dto.HintResponseDTO;
import com.hufs.algoing.hint.entity.Hint;
import com.hufs.algoing.hint.service.HintService;
import com.hufs.algoing.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "힌트 API", description = "힌트 요청 API")
@RequestMapping("/api/problems/{problemId}/hints")
@RequiredArgsConstructor
public class HintController {

    private final HintService hintService;
    @GetMapping("/{hintOrder}")
    @Operation(summary = "힌트 요청 API", description = "특정 문제 번호의 특정 순서 힌트를 가져옵니다.")
    @Parameter(name = "problemId", description = "요청 힌트 문제 번호")
    @Parameter(name = "hintOrder", description = "요청 힌트 순서")
    public ApiResponse<HintResponseDTO> getHint(@PathVariable Long problemId, @PathVariable int hintOrder, @AuthenticationPrincipal User user) {
        Hint hint = hintService.getHint(problemId, hintOrder);
        hintService.minusPoint(user);
        return ApiResponse.onSuccess(HintResponseDTO.fromEntity(hint.getContent(),hint.getOrder()));
    }

}
