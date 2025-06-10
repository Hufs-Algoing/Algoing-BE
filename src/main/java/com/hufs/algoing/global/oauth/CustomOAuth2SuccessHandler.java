package com.hufs.algoing.global.oauth;

import com.hufs.algoing.global.jwt.JwtUtil;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();


        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        request.getSession(true).setAttribute("userEmail", email);
        request.getSession(true).setAttribute("user", user);
        String token = jwtUtil.createToken(principalDetails.getUser());

        response.addCookie(createCookie(token));


        if (user.getBojId() == null) {
            response.sendRedirect("http://localhost:3000/profile");
        } else {
            response.sendRedirect("http://localhost:3000/main");
        }
    }


    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie("Authorization", value);
        cookie.setMaxAge(60 * 60 * 60); // 60 hours
        cookie.setSecure(true); // Set to true if using HTTPS
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("al-going.com");
        return cookie;
    }
}
