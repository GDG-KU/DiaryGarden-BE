package com.GDG.worktree.team2.gardening_diary.controller;

import com.GDG.worktree.team2.gardening_diary.dto.ApiResponse;
import com.GDG.worktree.team2.gardening_diary.entity.Diary;
import com.GDG.worktree.team2.gardening_diary.entity.EmotionAnalysis;
import com.GDG.worktree.team2.gardening_diary.service.DiaryService;
import com.GDG.worktree.team2.gardening_diary.service.EmotionAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

/**
 * 감정 분석 관련 API.
 */
@RestController
@RequestMapping("/api/emotions")
@CrossOrigin(origins = "*")
public class EmotionAnalysisController {

    private final EmotionAnalysisService emotionAnalysisService;
    private final DiaryService diaryService;

    @Autowired
    public EmotionAnalysisController(EmotionAnalysisService emotionAnalysisService,
                                     DiaryService diaryService) {
        this.emotionAnalysisService = emotionAnalysisService;
        this.diaryService = diaryService;
    }

    /**
     * 다이어리의 감정 분석 결과 조회.
     */
    @GetMapping("/{diaryId}")
    public ResponseEntity<ApiResponse<EmotionAnalysis>> getEmotionAnalysis(
            @AuthenticationPrincipal String userId,
            @PathVariable String diaryId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("인증이 필요합니다"));
        }

        try {
            Diary diary = diaryService.getDiaryById(diaryId);
            if (diary == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("다이어리를 찾을 수 없습니다"));
            }
            if (!userId.equals(diary.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>("조회 권한이 없습니다"));
            }

            EmotionAnalysis analysis = emotionAnalysisService.getByDiaryId(diaryId);
            if (analysis == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("감정 분석 결과가 존재하지 않습니다"));
            }

            return ResponseEntity.ok(new ApiResponse<>(analysis, "감정 분석 조회 성공"));

        } catch (ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("감정 분석 조회 실패: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("감정 분석 조회가 중단되었습니다"));
        }
    }

    /**
     * 감정 분석 재실행 (다이어리 내용 변경시 사용).
     */
    @PostMapping("/{diaryId}/recompute")
    public ResponseEntity<ApiResponse<EmotionAnalysis>> recomputeEmotionAnalysis(
            @AuthenticationPrincipal String userId,
            @PathVariable String diaryId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>("인증이 필요합니다"));
        }

        try {
            Diary diary = diaryService.getDiaryById(diaryId);
            if (diary == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("다이어리를 찾을 수 없습니다"));
            }
            if (!userId.equals(diary.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>("재분석 권한이 없습니다"));
            }

            EmotionAnalysis analysis = emotionAnalysisService
                    .analyzeAndSave(diary.getId(), diary.getContent());

            return ResponseEntity.ok(new ApiResponse<>(analysis, "감정 분석이 갱신되었습니다"));

        } catch (ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("감정 분석 실패: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("감정 분석이 중단되었습니다"));
        }
    }
}
