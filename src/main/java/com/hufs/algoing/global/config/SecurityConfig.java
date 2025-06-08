package com.hufs.algoing.global.config;

import com.hufs.algoing.global.oauth.CustomOAuth2SuccessHandler;
import com.hufs.algoing.global.oauth.PrincipalOauth2UserService;
import com.hufs.algoing.user.entity.Role;
import com.hufs.algoing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;
    @Autowired
    private UserRepository userRepository;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors((corsConfig) ->
                        corsConfig.configurationSource(corsConfigurationSource())
                )
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/login", "/signup").permitAll()
                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        .requestMatchers(
                                "/swagger.html", "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login((oauth) -> oauth
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(authorizationRequestRepository())
                        )
                        .loginPage("/login")
                        .successHandler(new CustomOAuth2SuccessHandler(userRepository))
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(principalOauth2UserService))
                )
                .logout((logout) ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true)
                );
        return http.build();
    }

    // CORS 설정 (CorsFilter는 불필요)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "https://www.al-going.com",
                "https://al-going.com",
                "http://localhost:*",
                "http://43.200.206.181:*"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Set-Cookie", "Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebFilter addSameSiteCookieFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> {
                String cookieHeader = exchange.getResponse().getHeaders().getFirst("Set-Cookie");
                if (cookieHeader != null) {
                    String updatedCookie = cookieHeader + "; SameSite=None; Secure";
                    exchange.getResponse().getHeaders().set("Set-Cookie", updatedCookie);
                }
                return Mono.empty();
            });
            return chain.filter(exchange);
        };
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setSameSite("None");
        serializer.setUseSecureCookie(true);
        serializer.setDomainName(".al-going.com");
        serializer.setCookiePath("/");
        return serializer;
    }

    @Bean
    public HttpSessionOAuth2AuthorizationRequestRepository authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }
}
