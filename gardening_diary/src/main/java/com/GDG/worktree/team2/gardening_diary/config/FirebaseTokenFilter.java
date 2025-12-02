package com.GDG.worktree.team2.gardening_diary.config;

import com.GDG.worktree.team2.gardening_diary.security.JwtProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseTokenFilter.class);
    
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String uid = null;
            
            // 1. 먼저 JWT 토큰으로 시도 (로그인/회원가입에서 발급한 토큰)
            try {
                uid = jwtProvider.getUidFromToken(token);
                logger.debug("JWT token validated successfully for uid: {}", uid);
            } catch (Exception jwtEx) {
                // JWT 검증 실패 시 Firebase ID Token으로 시도
                try {
                    FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
                    uid = decoded.getUid();
                    logger.debug("Firebase ID token validated successfully for uid: {}", uid);
                } catch (Exception firebaseEx) {
                    logger.warn("Invalid token: JWT error - {}, Firebase error - {}", 
                               jwtEx.getMessage(), firebaseEx.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                    return;
                }
            }
            
            // 인증 성공
            if (uid != null) {
                var auth = new UsernamePasswordAuthenticationToken(uid, null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}