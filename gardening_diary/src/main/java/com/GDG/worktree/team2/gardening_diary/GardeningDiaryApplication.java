package com.GDG.worktree.team2.gardening_diary;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@SpringBootApplication
public class GardeningDiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GardeningDiaryApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
public void initializeFirebase() throws IOException {
    GoogleCredentials creds;
    String json = System.getenv("FIREBASE_CREDENTIALS");

    if (json != null && !json.isBlank()) {
        // env 문자열로 주입된 경우
        creds = GoogleCredentials.fromStream(
                new java.io.ByteArrayInputStream(json.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        System.out.println("Firebase: env(FIREBASE_CREDENTIALS) 사용");
    } else {
        // ✅ ADC 우선 시도 (Cloud Run/로컬 gcloud ADC)
        try {
            creds = GoogleCredentials.getApplicationDefault();
            System.out.println("Firebase: ADC(Application Default Credentials) 사용");
        } catch (IOException e) {
            // 마지막 수단: classpath 파일
            var resource = new org.springframework.core.io.DefaultResourceLoader()
                    .getResource("classpath:diarygarden-7bb2d-firebase-adminsdk-fbsvc-e77de73a02.json");
            creds = GoogleCredentials.fromStream(resource.getInputStream());
            System.out.println("Firebase: classpath 파일 사용");
        }
    }

    var options = FirebaseOptions.builder().setCredentials(creds).build();
    if (FirebaseApp.getApps().isEmpty()) {
        FirebaseApp.initializeApp(options);
        System.out.println("🚀 Firebase initialized successfully!");
        }
    }
}
