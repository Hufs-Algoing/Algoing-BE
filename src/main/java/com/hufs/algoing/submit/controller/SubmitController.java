package com.hufs.algoing.submit.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.submit.dto.RecaptchaRequestDTO;
import com.hufs.algoing.submit.dto.RecaptchaResponseDTO;
import com.hufs.algoing.submit.dto.SubmitRequestDTO;
import com.hufs.algoing.submit.service.SubmitService;
import com.hufs.algoing.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
//
@RestController
@RequiredArgsConstructor
@RequestMapping("/submit")
@Slf4j
public class SubmitController {
    private final SubmitService submitService;


    // 캡차 해결 + 자동 제출

    @PostMapping
    public ApiResponse<RecaptchaResponseDTO> solveCaptcha(@RequestBody SubmitRequestDTO dto) {
        RecaptchaResponseDTO result = submitService.solveAndJudge(dto);
        return ApiResponse.onSuccess(result);
    }



}
