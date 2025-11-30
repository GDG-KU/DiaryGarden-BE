package com.GDG.worktree.team2.gardening_diary.security;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtProvider {

    private static final String SECRET_KEY = "SECRET_KEY_FOR_GARDENING_DIARY";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    public String createToken(String uid) {
        return Jwts.builder()
                .setSubject(uid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}