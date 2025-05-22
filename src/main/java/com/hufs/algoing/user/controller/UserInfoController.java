package com.hufs.algoing.user.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.problem.dto.ZandiDTO;
import com.hufs.algoing.user.dto.UserInfoDTO;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User Info API", description = "유저 정보 API")
@RestController
@RequestMapping("/api/myinfo")
public class UserInfoController {
    private final UserService userService;

    public UserInfoController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "유저 정보 조회", description = "해당 유저 ID를 기반으로 유저 정보를 조회합니다.")
    @GetMapping
    public ApiResponse<UserInfoDTO> getUserInfo(@AuthenticationPrincipal User p) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(p.getUserId());
        userInfoDTO.setHandle(p.getHandle());
        userInfoDTO.setEmail(p.getEmail());
        userInfoDTO.setNickname(p.getNickname());
        userInfoDTO.setBio(p.getBio());
        userInfoDTO.setProfileImageUrl(p.getProfileImageUrl());
        userInfoDTO.setTier(p.getTier());
        userInfoDTO.setSolvedCount(p.getSolvedCount());
        userInfoDTO.setUserPoint(p.getUserPoint());
        userInfoDTO.setCreatedAt(p.getCreatedAt());

        return ApiResponse.onSuccess(userInfoDTO);
    }

    @Operation(summary = "Zandi", description = "해당 유저 ID를 기반으로 유저 제출문제를 조회하여 날짜와 날짜별 성공문제 수 반환.")
    @GetMapping("/zandi")
    public ApiResponse<List<ZandiDTO>> getUserZandi(
            @AuthenticationPrincipal User p) {
        List<ZandiDTO> zandiDTOList = userService.getUserActivity(p);
        return ApiResponse.onSuccess(zandiDTOList);
    }

}
