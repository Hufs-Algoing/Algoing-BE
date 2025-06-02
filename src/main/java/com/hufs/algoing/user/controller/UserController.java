package com.hufs.algoing.user.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.problem.dto.SubmittedProblemDTO;
import com.hufs.algoing.review.dto.SearchReviewResponseDTO;
import com.hufs.algoing.solvedac.dto.SolvedAcProfileDTO;
import com.hufs.algoing.solvedac.service.SolvedAcService;
import com.hufs.algoing.user.dto.BookMarkDTO;
import com.hufs.algoing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User API", description = "유저 API")
@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UserController {
    //    @Autowired
    private final UserService userService;
    //    @Autowired
    private final SolvedAcService solvedAcService;

    @Operation(summary = "유저 정보 조회", description = "해당 유저 ID를 기반으로 유저 정보를 조회합니다.")
    @GetMapping("/{bojId}")
    public ApiResponse<String> getUser(@PathVariable String bojId) throws Exception {
        //임시
        SolvedAcProfileDTO profile = solvedAcService.getSolvedAcProfile(bojId);
        return ApiResponse.onSuccess(profile.getBio() + " " + profile.getTier() + " " + profile.getSolvedCount() + " " + profile.getProfileImageUrl());
    }

    @Operation(summary = "Solved.ac 정보 업데이트", description = "해당 유저 ID를 기반으로 Solved.ac에서의 정보를 불러옵니다.")
    @PutMapping("/{bojId}")
    public ApiResponse<String> updateUser(@PathVariable String bojId) throws Exception {
        userService.updateUserSolvedAcData(bojId);
        return ApiResponse.onSuccess(bojId + "의 정보가 업데이트 되었습니다.");
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

