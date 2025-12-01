package com.GDG.worktree.team2.gardening_diary.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

    // 최소 256비트(32바이트) 이상 문자열 필요
    private static final String SECRET = "SECRET_KEY_FOR_GARDENING_DIARY_SECRET_KEY_FOR_GARDENING_DIARY";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * UID로 JWT 토큰 생성
     */
    public String createToken(String uid) {
        return Jwts.builder()
                .setSubject(uid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰에서 UID 추출
     */
    public String getUidFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
