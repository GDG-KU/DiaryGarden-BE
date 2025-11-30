//User.java
package com.GDG.worktree.team2.gardening_diary.entity;

import java.util.Date;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티
 */
@Getter
@NoArgsConstructor
public class User {
    @DocumentId
    private String id;
    
    private String uid; // Firebase Auth UID
    private String username; // 아이디 (일반 회원가입용)
    private String password; // 비밀번호 (암호화되지 않은 상태)
    private String email; // 이메일 (구글 로그인용)
    private String nickname;
    private String displayName;
    private String profileImageUrl;
    private String authProvider; // "USERNAME" 또는 "GOOGLE"
    
    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;
    
    // 생성자
    public User(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
    
    // Setters
    public void setId(String id) {
        this.id = id;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", authProvider='" + authProvider + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
