package com.hufs.algoing.global.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // JWT 토큰 쿠키 삭제
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        // 프론트엔드에서 처리할 수 있도록 응답
        return ResponseEntity.ok()
                .header("Location", "/login")
                .body(Map.of("redirectUrl", "/login"));
    }
}