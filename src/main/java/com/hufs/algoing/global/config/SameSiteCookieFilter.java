package com.hufs.algoing.global.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

public class SameSiteCookieFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        chain.doFilter(request, response);
        // 응답이 커밋된 후에는 헤더를 수정할 수 없으므로, 필터 체인 실행 전에 처리
        addSameSiteCookieAttribute(res);
    }

    private void addSameSiteCookieAttribute(HttpServletResponse response) {
        Collection<String> headers = response.getHeaders("Set-Cookie");
        for (String header : headers) {
            if (header.contains("JSESSIONID")) {
                String updatedHeader = header + "; SameSite=None; Secure";
                response.setHeader("Set-Cookie", updatedHeader);
            }
        }
    }
}
