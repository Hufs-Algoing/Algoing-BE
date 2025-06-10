package com.hufs.algoing.global.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    Cookie deleteCookie = new Cookie("Authorization", null);
                    deleteCookie.setMaxAge(0);
                    deleteCookie.setPath("/");  // 원래 쿠키와 동일한 path
                    deleteCookie.setHttpOnly(true);
                    // deleteCookie.setDomain(".al-going.com");
                    response.addCookie(deleteCookie);
                    break;
                }
            }
        }

        return ResponseEntity.ok()
                .body(Map.of("redirectUrl", "https://www.al-going.com/login"));
    }
}