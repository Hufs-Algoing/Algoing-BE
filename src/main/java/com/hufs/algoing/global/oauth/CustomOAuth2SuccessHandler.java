package com.hufs.algoing.global.oauth;

import com.hufs.algoing.global.jwt.JwtTokenResponse;
import com.hufs.algoing.global.jwt.JwtUtil;
import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import com.hufs.algoing.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserService userService;

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
        Long userId = user.getUserId();

        request.getSession(true).setAttribute("userEmail", email);
        request.getSession(true).setAttribute("user", user);

        JwtTokenResponse jwtToken = jwtUtil.generateToken(userId);
        userService.saveRefreshToken(userId, jwtToken);

        String url = makeRedirectUrl(jwtToken.getAccessToken(), jwtToken.getRefreshToken());

        if (response.isCommitted()) {
            logger.debug("응답이 이미 커밋된 상태입니다. " + url + "로 리다이렉트하도록 바꿀 수 없습니다.");
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, url);
    }

//        //TODO: 배포할 때 프론트 서버 주소로 바꿔야합니다
//        if (user.getBojId() == null) {
//            response.sendRedirect("https://al-going.com/profile");
//        } else {
//            response.sendRedirect("https://al-going.com/main");
//        }
//    }
    private String makeRedirectUrl(String accessToken, String refreshToken) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(3000)
                .path("/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }
}
