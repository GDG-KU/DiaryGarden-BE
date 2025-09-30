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
        InputStream serviceAccount = new ClassPathResource("diarygarden-7bb2d-firebase-adminsdk-fbsvc-e77de73a02.json").getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully.");
        }
    }
}
