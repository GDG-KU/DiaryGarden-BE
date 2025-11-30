package com.GDG.worktree.team2.gardening_diary.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 감정 분석 엔티티
 */
@Getter
@NoArgsConstructor
public class EmotionAnalysis {
    @DocumentId
    private String id;
    
    private String diaryId; // 다이어리 ID
    private Map<String, Double> result; // 감정 분석 결과 (예: {"기쁨": 0.8, "슬픔": 0.2})
    
    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;
    
    // 생성자
    public EmotionAnalysis(String diaryId, Map<String, Double> result) {
        this.diaryId = diaryId;
        this.result = result;
    }
    
    // Getters and Setters
    public void setId(String id) {
        this.id = id;
    }
    
    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }
    
    public void setResult(Map<String, Double> result) {
        this.result = result;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "EmotionAnalysis{" +
                "id='" + id + '\'' +
                ", diaryId='" + diaryId + '\'' +
                ", result=" + result +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


