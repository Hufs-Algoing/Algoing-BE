package com.hufs.algoing.user.controller;

import com.hufs.algoing.global.code.ApiResponse;
import com.hufs.algoing.global.exception.custom.BojIdExistException;
import com.hufs.algoing.global.exception.custom.BojIdNotExistException;
import com.hufs.algoing.global.oauth.PrincipalDetails;
import com.hufs.algoing.user.dto.BojInsertDTO;
import com.hufs.algoing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Auth API", description = "유저 로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserAuthController {

    private final UserService userService;


    @Operation(summary = "백준 정보 입력", description = "회원가입 후 핸들을 입력합니다.")
    @PostMapping("/insertboj")
    public ApiResponse<String> insertBoj(@RequestBody BojInsertDTO dto, @AuthenticationPrincipal PrincipalDetails principal) throws BojIdExistException, BojIdNotExistException {

        try{
            userService.insertBoj(dto, principal);
            return ApiResponse.onSuccess("백준 정보가 성공적으로 입력되었습니다.");
        } catch (BojIdExistException e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), "백준 아이디가 이미 존재합니다.");
        } catch (BojIdNotExistException e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), "백준 아이디가 존재하지 않습니다.");
        } catch (Exception e) {
            return ApiResponse.onFailure("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다.", null);
        }
    }
}
