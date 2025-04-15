package com.hufs.algoing.user.controller;

import com.hufs.algoing.solvedac.entity.SolvedAcProfile;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.user.dto.UserDTO;
import com.hufs.algoing.user.entity.User;
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
    public String getUser(@PathVariable String handle) {
        //임시
        SolvedAcProfile profile = solvedAcService.getSolvedAcProfile(handle);
        return profile.getBio() + " " + profile.getTier() + " " + profile.getSolvedCount() + " " + profile.getProfileImageUrl();
    }

    @PutMapping("/{handle}")
    public String updateUser(@PathVariable String handle) {
        userService.updateUserData(handle);
        return "User data updated successfully";
    }
}
