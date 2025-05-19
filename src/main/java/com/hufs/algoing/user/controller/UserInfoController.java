package com.hufs.algoing.user.controller;

import com.hufs.algoing.problem.dto.ZandiDTO;
import com.hufs.algoing.user.dto.UserInfoDTO;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/myinfo")
public class UserInfoController {
    private final UserService userService;

    public UserInfoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserInfoDTO> getUserInfo(@AuthenticationPrincipal User p){
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

        return ResponseEntity.ok(userInfoDTO);
    }

    @GetMapping("/zandi")
    public ResponseEntity<List<ZandiDTO>> getUserZandi(
            @AuthenticationPrincipal User p){
        List<ZandiDTO> zandiDTOList = userService.getUserActivity(p);
        return ResponseEntity.ok(zandiDTOList);
    }

}
