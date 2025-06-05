package com.hufs.algoing.submit.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.submit.dto.RecaptchaResponseDTO;
import com.hufs.algoing.submit.dto.SubmitRequestDTO;
import com.hufs.algoing.submit.service.SubmitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/submit")
@Slf4j
@Tag(name = "Submit API", description = "문제 제출 및 채점 API")
public class SubmitController {
    private final SubmitService submitService;

    @Operation(summary = "문제 코드 제출 및 채점", description = "사용자가 제출한 코드와 정보를 기반으로 문제를 채점합니다.")
    @PostMapping
    public ApiResponse<RecaptchaResponseDTO> solveCaptcha(@RequestBody SubmitRequestDTO dto) throws Exception {
        RecaptchaResponseDTO result = submitService.solveAndJudge(dto);

        return ApiResponse.onSuccess(result);
    }


}
