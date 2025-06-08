package com.hufs.algoing.global.oauth;

import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomOAuth2SuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Value("${app.oauth.redirect.profile}")
    private String profileRedirect;

    @Value("${app.oauth.redirect.main}")
    private String mainRedirect;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        request.getSession(true).setAttribute("userEmail", email);

        //TODO: 배포할 때 프론트 서버 주소로 바꿔야합니다
        if (user.getBojId() == null) {
            response.sendRedirect(profileRedirect);
        } else {
            response.sendRedirect(mainRedirect);
        }
    }
}
