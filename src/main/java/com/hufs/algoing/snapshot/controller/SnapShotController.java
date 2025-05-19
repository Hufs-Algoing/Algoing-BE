package com.hufs.algoing.snapshot.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.snapshot.Service.SnapShotService;
import com.hufs.algoing.snapshot.dto.SnapShotDTO;

import com.hufs.algoing.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SanpShot API", description = "유저의 스냅샷 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/snapshots")
public class SnapShotController {
    private final SnapShotService snapShotService;

    @Operation(summary = "유저 가장 최신 스냅샷 조회", description = "해당 유저 ID를 기반으로 스냅샷 조회합니다.")
    @GetMapping
    public ApiResponse<SnapShotDTO> snapShot(@AuthenticationPrincipal User user){
        SnapShotDTO snapShotDTO = snapShotService.getRecentSnapShot(user.getUserId());
        return ApiResponse.onSuccess(snapShotDTO);
    }
}
