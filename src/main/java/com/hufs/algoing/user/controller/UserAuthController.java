
package com.hufs.algoing.user.controller;

import com.hufs.algoing.user.dto.UserDTO;
import com.hufs.algoing.user.entity.User;
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

    @Operation(summary = "회원가입", description = "회원가입을 처리합니다.")
    @PostMapping("/signup")
    public String signup(UserDTO dto){
        userService.signup(dto);
        return "redirect:/login";
    }

    @Operation(summary = "핸들 입력", description = "회원가입 후 핸들을 입력합니다.")
    @PostMapping("/inserthandle")
    public String insertHandle(UserDTO dto, @AuthenticationPrincipal User principal) {
        userService.insertHandle(dto,principal);
        userService.updateUserData(dto.getHandle());
        return "redirect:/";
    }
}
