package com.hufs.algoing.user.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.review.dto.SearchReviewResponseDTO;
import com.hufs.algoing.solvedac.dto.SolvedAcProfileDTO;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.problem.dto.SubmittedProblemDTO;
import com.hufs.algoing.user.dto.BookMarkDTO;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

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

    @Tag(name = "Mypage", description = "")
    @Operation(summary = "Mypage", description = "요청 받은 사용자가 제출한 문제를 조회합니다.")
    @Parameter(name = "userId", description = "사용자 id")
    @GetMapping("/{userId}/solved")
    public ApiResponse<List<SubmittedProblemDTO>> getUserSolvedProblems(@PathVariable Long userId) {
        List<SubmittedProblemDTO> solvedProblems = userService.searchUserSolve(userId);
        return ApiResponse.onSuccess(solvedProblems);
    }

    @Tag(name = "Mypage", description = "")
    @Operation(summary = "Mypage", description = "요청 받은 사용자가 리뷰받은 문제를 조회합니다.")
    @Parameter(name = "userId", description = "사용자 id")
    @GetMapping("/{userId}/reviewed")
    public ApiResponse<List<SearchReviewResponseDTO>> getUserReviewedProblems(@PathVariable Long userId) {
        List<SearchReviewResponseDTO> reviewedProblems = userService.searchUserReviewed(userId);
        return ApiResponse.onSuccess(reviewedProblems);

    }

    @Tag(name = "BookMark", description = "즐겨찾기 등록/삭제 API")
    @Operation(summary = "BookMark", description = "등록 true/ 삭제 false")
    @Parameter(name = "user id / problem id", description = "user id/problem id")
    @PatchMapping("/toggle")
    public ApiResponse<Boolean> toggleBookmark(
            @RequestParam Long userId,
            @RequestParam Long problemId
    ) {
        boolean status = userService.updateBookMark(userId, problemId, true);
        return ApiResponse.onSuccess(status);
    }

    @Tag(name = "Mypage", description = "")
    @Operation(summary = "Mypage", description = "요청 받은 사용자가 북마크한 문제를 조회합니다.")
    @Parameter(name = "userId", description = "사용자 id")
    @GetMapping("/{userId}/bookmarks")
    public ApiResponse<List<BookMarkDTO>> getUserBookmarks(@PathVariable Long userId) {
        List<BookMarkDTO> bookmarkedProblems = userService.getUserBookmarks(userId);
        return ApiResponse.onSuccess(bookmarkedProblems);
    }


}

