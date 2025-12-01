package com.GDG.worktree.team2.gardening_diary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.GDG.worktree.team2.gardening_diary.dto.AuthResponse;
import com.GDG.worktree.team2.gardening_diary.dto.LoginRequest;
import com.GDG.worktree.team2.gardening_diary.dto.RegisterRequest;
import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.GDG.worktree.team2.gardening_diary.repository.UserRepository;
import com.GDG.worktree.team2.gardening_diary.security.JwtProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

/**
 * 인증 서비스
 */
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final FirebaseAuth firebaseAuth;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, FirebaseAuth firebaseAuth) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.firebaseAuth = firebaseAuth; // Spring Bean으로 주입
    }
    
    /**
     * 아이디/비밀번호 회원가입
     */
    public AuthResponse register(RegisterRequest request) {
        try {
            User existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser != null) {
                // 이미 존재하는 사용자면 자동 로그인 처리 후 토큰 반환
                try {
                    String customToken = firebaseAuth.createCustomToken(existingUser.getUid());
                    return new AuthResponse(customToken, existingUser.getUid(),
                            request.getUsername(), existingUser.getDisplayName());
                } catch (FirebaseAuthException e) {
                    return new AuthResponse("로그인 실패: " + e.getMessage());
                }
            }
            
            String firebaseEmail = request.getUsername() + "@gardening-diary.app";
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(firebaseEmail)
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getDisplayName());
            
            UserRecord userRecord = firebaseAuth.createUser(createRequest);
            
            User user = new User();
            user.setUid(userRecord.getUid());
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setDisplayName(userRecord.getDisplayName());
            user.setNickname(userRecord.getDisplayName());
            user.setAuthProvider("USERNAME");
            
            userRepository.save(user);
            
            String customToken = firebaseAuth.createCustomToken(userRecord.getUid());
            
            return new AuthResponse(customToken, userRecord.getUid(),
                    request.getUsername(), userRecord.getDisplayName());
            
        } catch (FirebaseAuthException e) {
            // Firebase에 이미 존재하지만 Firestore에는 없는 경우 처리
            if (e.getErrorCode() != null && e.getErrorCode().equals("auth/email-already-exists")) {
                User existingUser = userRepository.findByUsername(request.getUsername());
                if (existingUser != null) {
                    try {
                        String customToken = firebaseAuth.createCustomToken(existingUser.getUid());
                        return new AuthResponse(customToken, existingUser.getUid(),
                                request.getUsername(), existingUser.getDisplayName());
                    } catch (FirebaseAuthException ex) {
                        return new AuthResponse("로그인 실패: " + ex.getMessage());
                    }
                }
            }
            return new AuthResponse("회원가입 실패: " + e.getMessage());
        } catch (Exception e) {
            return new AuthResponse("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsername());
            if (user == null) return new AuthResponse("존재하지 않는 아이디입니다");
            
            if (user.getPassword() == null) return new AuthResponse("비밀번호 로그인을 지원하지 않습니다");
            if (request.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new AuthResponse("비밀번호가 일치하지 않습니다");
            }
            
            // Custom Token 생성해서 반환
            try {
                String customToken = firebaseAuth.createCustomToken(user.getUid());
                return new AuthResponse(customToken, user.getUid(), request.getUsername(), user.getDisplayName());
            } catch (FirebaseAuthException e) {
                return new AuthResponse("토큰 생성 실패: " + e.getMessage());
            }
            
        } catch (Exception e) {
            return new AuthResponse("로그인 실패: " + e.getMessage());
        }
    }
    
    public AuthResponse verifyToken(String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            
            User user = userRepository.findByUid(uid);
            if (user == null) return new AuthResponse("사용자 정보를 찾을 수 없습니다");
            
            String identifier = user.getEmail() != null ? user.getEmail() : user.getUsername();
            return new AuthResponse(idToken, uid, identifier, user.getDisplayName());
            
        } catch (FirebaseAuthException e) {
            return new AuthResponse("토큰 검증 실패: " + e.getMessage());
        } catch (Exception e) {
            return new AuthResponse("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    public User getUserByUid(String uid) {
        try {
            return userRepository.findByUid(uid);
        } catch (Exception e) {
            System.err.println("사용자 조회 실패: " + e.getMessage());
            return null;
        }
    }
    
    public User updateUser(String uid, String displayName, String profileImageUrl) {
        try {
            User user = userRepository.findByUid(uid);
            if (user == null) return null;
            
            if (displayName != null) user.setDisplayName(displayName);
            if (profileImageUrl != null) user.setProfileImageUrl(profileImageUrl);
            
            return userRepository.save(user);
            
        } catch (Exception e) {
            System.err.println("사용자 업데이트 실패: " + e.getMessage());
            return null;
        }
    }
    
    public boolean deleteUser(String uid) {
        try {
            firebaseAuth.deleteUser(uid);
            userRepository.deleteByUid(uid);
            return true;
        } catch (Exception e) {
            System.err.println("사용자 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public AuthResponse generateTokenForSocialUser(User user) {
        String token = jwtProvider.createToken(user.getUid());

        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage("소셜 로그인 성공");
        response.setToken(token);
        response.setUid(user.getUid());
        return response;
    }
}
