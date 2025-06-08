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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SanpShot API", description = "유저의 스냅샷 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/snapshots")
public class SnapShotController {
    private final SnapShotService snapShotService;

    @Operation(summary = "유저 가장 최신 스냅샷 조회", description = "해당 유저 ID를 기반으로 스냅샷 조회합니다.")
    @Parameter(name = "userId", description = "사용자 ID")
    @GetMapping
    public ApiResponse<SnapShotDTO> snapShot(@RequestParam Long userId){
        SnapShotDTO snapShotDTO = snapShotService.getRecentSnapShot(userId);
        return ApiResponse.onSuccess(snapShotDTO);
    }

    @Operation(summary = "유저의 전체 스냅샷 이력 조회", description = "그래프 생성용 데이터입니다.")
    @Parameter(name = "userId", description = "사용자 ID")
    @GetMapping("/history")
    public ApiResponse<List<SnapShotDTO>> getSnapshotHistory(@RequestParam Long userId){
        List<SnapShotDTO> history = snapShotService.getSnapshotHistory(userId);
        return ApiResponse.onSuccess(history);
    }
}
