package com.hufs.algoing.global.controller;


import com.hufs.algoing.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;


@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LoginController {

    private final UserService userService;

    @GetMapping("/refresh")
    public String refresh(HttpServletRequest request, @CookieValue(name = "RefreshToken") Cookie cookie)
            throws AccessDeniedException {
        String refreshToken = cookie.getValue();
        return userService.refreshAccessToken(refreshToken);
    }
}
