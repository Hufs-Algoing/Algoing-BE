package com.hufs.algoing.user.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.solvedac.dto.SolvedAcProfileDTO;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SolvedAcService solvedAcService;

    @GetMapping("/{handle}")
    public ApiResponse<String> getUser(@PathVariable String handle) {
        //임시
        SolvedAcProfileDTO profile = solvedAcService.getSolvedAcProfile(handle);
        return ApiResponse.onSuccess(profile.getBio() + " " + profile.getTier() + " " + profile.getSolvedCount() + " " + profile.getProfileImageUrl());
    }

    @PutMapping("/{handle}")
    public ApiResponse<String> updateUser(@PathVariable String handle) {
        userService.updateUserData(handle);
        return ApiResponse.onSuccess("User data updated successfully");
    }
}
