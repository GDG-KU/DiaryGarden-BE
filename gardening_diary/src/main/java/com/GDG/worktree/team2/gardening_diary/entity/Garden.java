package com.GDG.worktree.team2.gardening_diary.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 정원/나무 엔티티
 */
@Getter
@NoArgsConstructor
public class Garden {
    @DocumentId
    private String id;
    
    private String userId; // 사용자 ID
    private String treeType; // 나무 종류 (예: "벚나무")
    private String status; // 상태 (예: "성장 중")
    private int diaryCount; // 다이어리 개수
    private List<String> leafColors; // 잎 색상 목록
    private String treeSnapshot; // 나무 스냅샷 이미지 URL
    private LocalDateTime weekStartDate; // 주 시작 날짜
    private LocalDateTime weekEndDate; // 주 종료 날짜
    
    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;
    
    // 생성자
    public Garden(String userId, String treeType) {
        this.userId = userId;
        this.treeType = treeType;
        this.status = "성장 중";
        this.diaryCount = 0;
    }
    
    // Getters and Setters
    public void setId(String id) {
        this.id = id;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setDiaryCount(int diaryCount) {
        this.diaryCount = diaryCount;
    }
    
    public void setLeafColors(List<String> leafColors) {
        this.leafColors = leafColors;
    }
    
    public void setTreeSnapshot(String treeSnapshot) {
        this.treeSnapshot = treeSnapshot;
    }
    
    public void setWeekStartDate(LocalDateTime weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
    
    public void setWeekEndDate(LocalDateTime weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Garden{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", treeType='" + treeType + '\'' +
                ", status='" + status + '\'' +
                ", diaryCount=" + diaryCount +
                ", leafColors=" + leafColors +
                ", treeSnapshot='" + treeSnapshot + '\'' +
                ", weekStartDate=" + weekStartDate +
                ", weekEndDate=" + weekEndDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


