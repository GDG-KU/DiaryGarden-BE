package com.GDG.worktree.team2.gardening_diary.controller;

import com.GDG.worktree.team2.gardening_diary.dto.ApiResponse;
import com.GDG.worktree.team2.gardening_diary.dto.DiaryRequest;
import com.GDG.worktree.team2.gardening_diary.entity.Diary;
import com.GDG.worktree.team2.gardening_diary.service.DiaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Lightweight unit tests that exercise the authentication-aware branches in {@link DiaryController}.
 */
class DiaryControllerTest {

    private DiaryService diaryService;
    private DiaryController controller;

    @BeforeEach
    void setUp() {
        diaryService = Mockito.mock(DiaryService.class);
        controller = new DiaryController(diaryService);
    }

    @Test
    void createDiaryReturnsUnauthorizedWhenPrincipalMissing() {
        DiaryRequest request = new DiaryRequest("tree-1", "content");

        ResponseEntity<ApiResponse<Diary>> response = controller.createDiary(null, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("인증");
    }

    @Test
    void createDiaryReturnsSavedEntityWhenPrincipalPresent() throws Exception {
        DiaryRequest request = new DiaryRequest("tree-1", "content");
        Diary savedDiary = new Diary("user-1", request.getTreeId(), request.getContent());

        when(diaryService.createDiary("user-1", request)).thenReturn(savedDiary);

        ResponseEntity<ApiResponse<Diary>> response = controller.createDiary("user-1", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(savedDiary);
    }

    @Test
    void getDiaryRejectsWhenUserDoesNotOwnEntry() throws Exception {
        Diary diary = new Diary("owner", "tree-1", "content");
        diary.setId("diary-1");
        when(diaryService.getDiaryById("diary-1")).thenReturn(diary);

        ResponseEntity<ApiResponse<Diary>> response = controller.getDiary("other-user", "diary-1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("권한");
    }
}
