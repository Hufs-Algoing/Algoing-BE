package com.hufs.algoing.global.jwt;

import com.hufs.algoing.user.entity.User;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;
    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Long getUserId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Long.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(user.getEmail()) // 보통 email이나 id
                .claim("userId", user.getUserId())
                .claim("role", user.getRole().getValue()) // 예: "ROLE_USER"
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();
    }
}


//@Component
//public class JwtUtil {
//
//    private final Key key;
//    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; //access 60분
//    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14; //refresh 14일;
//
//    //    SECRET: 256 bits (32 byte) 이상
//    public JwtUtil(@Value("${jwt.secret}") String SECRET) {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public JwtTokenResponse generateToken(Long userId) {
//        System.out.println("generate: ");
//        System.out.println(userId);
//        long now = System.currentTimeMillis();
//
//        String accessToken = Jwts.builder()
//                .claim("userId", userId)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME)) // 1시간
//                .signWith(key, SignatureAlgorithm.HS512)
//                .compact();
//
//        // claim 없이 만료 시간만 담기
//        String refreshToken = Jwts.builder()
//                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
//                .signWith(key, SignatureAlgorithm.HS512)
//                .compact();
//
//        return JwtTokenResponse.builder()
//                .grantType("bearer")
//                .accessToken(accessToken)
//                .accessTokenExpiresIn(new Date(now + ACCESS_TOKEN_EXPIRE_TIME).getTime())
//                .refreshToken(refreshToken)
//                .build();
//    }
//    /**
//     * 토큰 유효여부 확인
//     */
//    /* 토큰 유효성 검증, boolean */
//    public boolean isValidToken(String token) {
//        try{
//            getAllClaims(token);
//            return true;
//        } catch (MalformedJwtException e) {
//            log.info("JWT 서명의 형식이 잘못되었습니다.");
//        } catch (ExpiredJwtException e) {
//            log.info("만료된 JWT 입니다.");
//        } catch (UnsupportedJwtException e) {
//            log.info("지원하지 않는 JWT 입니다.");
//        } catch (IllegalArgumentException e) {
//            log.info("잘못된 JWT 입니다.");
//        }
//        return false;
//    }
//
//    /**
//     * 토큰의 Claim 디코딩
//     */
//    public Claims getAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith((SecretKey) key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//}
