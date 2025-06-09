//package com.hufs.algoing.global.jwt;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    public static final String AUTHORIZATION_HEADER = "Authorization";
//    public static final String BEARER_PREFIX = "bearer ";
//    private final JwtUtil jwtUtil;
//
//    // JWT의 인증 정보를 현재 쓰레드의 SecurityContext에 저장
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain filterChain) throws IOException, ServletException {
//
//        String jwt = resolveToken(request);
//        jwtUtil.isValidToken(jwt);
//        filterChain.doFilter(request, response);
//
//    }
//
//    // request header 에서 토큰 꺼내오는 메소드
//    private String resolveToken(HttpServletRequest request) {
//        String bearToken = request.getHeader(AUTHORIZATION_HEADER);
//        if(StringUtils.hasText(bearToken) && bearToken.startsWith(BEARER_PREFIX)) {
//            return bearToken.substring(7);
//        }
//        return null;
//    }
//
//
//}
