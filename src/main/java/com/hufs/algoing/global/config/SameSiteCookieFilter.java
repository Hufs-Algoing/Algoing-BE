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
        Collection<String> headers = res.getHeaders("Set-Cookie");
        for (String header : headers) {
            if (header.contains("JSESSIONID")) {
                String updatedHeader = header;
                if (!header.contains("SameSite=None")) updatedHeader += "; SameSite=None";
                if (!header.contains("Secure")) updatedHeader += "; Secure";
                if (!header.contains("Domain=.al-going.com")) updatedHeader += "; Domain=.al-going.com";
                res.setHeader("Set-Cookie", updatedHeader);
            }
        }
    }
}
