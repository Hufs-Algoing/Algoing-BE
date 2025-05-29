package com.hufs.algoing.user.controller;

import com.hufs.algoing.global.oauth.PrincipalDetails;
import com.hufs.algoing.user.dto.UserDTO;
import com.hufs.algoing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "User Auth API", description = "유저 로그인 API")
@Controller
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;


    // 회원가입

//    @GetMapping("/signup")
//    public String testSignUp() {
//        return "redirect:/test/signup.html";
//    }

//    @Operation(summary = "회원가입", description = "회원가입을 처리합니다.")
//    @PostMapping("/signup")
//    public String signup(UserDTO dto){
//        userService.signup(dto);
//        return "redirect:/login";
//    }

    //TODO : 회원가입 후 아래 엔티티 null일경우 입력하게 하는 페이지로 리다이렉트하게 하기
    @Operation(summary = "백준 정보 입력", description = "회원가입 후 핸들을 입력합니다.")
    @PostMapping("/insertboj")
    public String insertBoj(UserDTO dto, @AuthenticationPrincipal PrincipalDetails principal) throws Exception {
        userService.insertBoj(dto, principal);
        return "redirect:/test";
    }
}
