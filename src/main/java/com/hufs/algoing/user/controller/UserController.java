package com.hufs.algoing.user.controller;

import com.hufs.algoing.problem.entity.UserSolvedProblem;
import com.hufs.algoing.solvedac.dto.SolvedAcProfileDTO;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.problem.dto.UserSolvedProblemDTO;
import com.hufs.algoing.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
        SolvedAcProfileDTO profile = solvedAcService.getSolvedAcProfile(handle);
        return profile.getBio() + " " + profile.getTier() + " " + profile.getSolvedCount() + " " + profile.getProfileImageUrl();
    }

    @PutMapping("/{handle}")
    public String updateUser(@PathVariable String handle) {
        userService.updateUserData(handle);
        return "User data updated successfully";
    }

    @GetMapping("/{userId}/solved")
    public List<UserSolvedProblemDTO> getUserSolvedProblems(@PathVariable Long userId) {
        List<UserSolvedProblemDTO> solvedProblems = userService.searchUserSolve(userId);
        return solvedProblems;
    }
}
