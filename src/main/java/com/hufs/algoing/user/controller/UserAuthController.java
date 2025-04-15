
package com.hufs.algoing.user.controller;

import com.hufs.algoing.user.dto.UserDTO;
import com.hufs.algoing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserAuthController {

    private final UserService userService;


    // 회원가입

//    @GetMapping("/signup")
//    public String testSignUp() {
//        return "redirect:/test/signup.html";
//    }

    @PostMapping("/signup")
    public String signup(UserDTO dto){
        userService.signup(dto);
        return "redirect:/login";
    }
}
