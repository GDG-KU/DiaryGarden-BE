package com.GDG.worktree.team2.gardening_diary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.GDG.worktree.team2.gardening_diary.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@Service
public class GoogleAuthService {
    @Autowired
    private UserRepository userRepository;

    public User loginWithGoogle(String idToken) throws Exception {
        FirebaseToken decodedToken;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Google ID Token");
        }

        String email = decodedToken.getEmail();
        String uid = decodedToken.getUid();
        String displayName = decodedToken.getName();
        String profileUrl = decodedToken.getPicture();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User(email, displayName);
            user.setUid(uid);
            user.setDisplayName(displayName);
            user.setProfileImageUrl(profileUrl);
            user.setAuthProvider("GOOGLE");
            userRepository.save(user);
        }

        return user;
    }
}
