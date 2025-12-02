package com.GDG.worktree.team2.gardening_diary;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GardeningDiaryApplication {

    public static void main(String[] args) {
        // .env 파일 로드 (애플리케이션 시작 시점에 실행)
        Dotenv dotenv = Dotenv.configure()
                .directory("./")  // 프로젝트 루트 디렉토리
                .ignoreIfMissing()  // .env 파일이 없어도 에러 발생하지 않음
                .load();
        
        // .env 파일의 환경변수를 시스템 프로퍼티로 설정 (Spring에서 사용)
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            // 따옴표 제거 (있는 경우)
            if (value != null) {
                value = value.replaceAll("^\"|\"$", "").replaceAll("^'|'$", "");
            }
            // 시스템 프로퍼티로 설정 (Spring의 @Value가 읽을 수 있음)
            System.setProperty(key, value);
            System.out.println("Loaded environment variable: " + key + " = " + (value != null && value.length() > 10 ? value.substring(0, 10) + "..." : value));
        });
        
        SpringApplication.run(GardeningDiaryApplication.class, args);
    }
    
}
