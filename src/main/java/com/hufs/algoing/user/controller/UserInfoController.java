package com.hufs.algoing.user.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.global.oauth.PrincipalDetails;
import com.hufs.algoing.problem.dto.ZandiDTO;
import com.hufs.algoing.user.dto.UserInfoDTO;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import com.hufs.algoing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User Info API", description = "유저 정보 API")
@RestController
@RequestMapping("/api/myinfo")
public class UserInfoController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserInfoController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "유저 정보 조회", description = "해당 유저 ID를 기반으로 유저 정보를 조회합니다.")
    @GetMapping
    public ApiResponse<UserInfoDTO> getUserInfo(@AuthenticationPrincipal PrincipalDetails p) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(p.getUser().getUserId());
        userInfoDTO.setEmail(p.getUser().getEmail());
        userInfoDTO.setHandle(p.getUser().getHandle());
        userInfoDTO.setRole(p.getUser().getRole());
        userInfoDTO.setName(p.getUser().getName());
        userInfoDTO.setBojId(p.getUser().getBojId());
        userInfoDTO.setBio(p.getUser().getBio());
        userInfoDTO.setPicture(p.getUser().getPicture());
        userInfoDTO.setTier(p.getUser().getTier());
        userInfoDTO.setSolvedCount(p.getUser().getSolvedCount());
        userInfoDTO.setUserPoint(p.getUser().getUserPoint());
        userInfoDTO.setCreatedAt(p.getUser().getCreatedAt());

        return ApiResponse.onSuccess(userInfoDTO);
    }

    @Operation(summary = "Zandi", description = "해당 유저 ID를 기반으로 유저 제출문제를 조회하여 날짜와 날짜별 성공문제 수 반환.")
    @GetMapping("/zandi")
    public ApiResponse<List<ZandiDTO>> getUserZandi(
            @RequestParam("userId") Long userId){
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        List<ZandiDTO> zandiDTOList = userService.getUserActivity(u);
        return ApiResponse.onSuccess(zandiDTOList);
    }

    @Operation(summary = "Zandi", description = "해당 유저 ID를 기반으로 유저 제출문제를 조회하여 날짜와 날짜별 성공문제 수 반환. 로그인 되어있어야 합니다.")
    @GetMapping("/zandiii")
    public ApiResponse<List<ZandiDTO>> getUserZandiii(
            @AuthenticationPrincipal PrincipalDetails p) {
        List<ZandiDTO> zandiDTOList = userService.getUserActivity(p.getUser());
        return ApiResponse.onSuccess(zandiDTOList);
    }

}
