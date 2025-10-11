package com.GDG.worktree.team2.gardening_diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class GardeningDiaryApplication {

    @Value("${firebase.service-account-file}")
    private String serviceAccountFile;

    public static void main(String[] args) {
        SpringApplication.run(GardeningDiaryApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeFirebase() throws IOException {
        InputStream serviceAccount;
        
        // 환경 변수에서 Firebase 자격 증명 확인
        String firebaseCredentials = System.getenv("FIREBASE_CREDENTIALS");
        
        if (firebaseCredentials != null && !firebaseCredentials.isEmpty()) {
            // 환경 변수에서 JSON을 읽어옴 (GCP 배포 시)
            serviceAccount = new java.io.ByteArrayInputStream(firebaseCredentials.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            System.out.println("Firebase credentials loaded from environment variable.");
        } else {
            // 로컬 개발 환경에서는 파일에서 읽어옴
            serviceAccount = new ClassPathResource("diarygarden-7bb2d-firebase-adminsdk-fbsvc-e77de73a02.json").getInputStream();
            System.out.println("Firebase credentials loaded from classpath.");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully.");
        }
    }
}
