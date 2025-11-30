package com.GDG.worktree.team2.gardening_diary.service;

import com.GDG.worktree.team2.gardening_diary.entity.EmotionAnalysis;
import com.GDG.worktree.team2.gardening_diary.repository.EmotionAnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 감정 분석 AI API 연동을 담당하는 서비스.
 */
@Service
public class EmotionAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(EmotionAnalysisService.class);

    private final EmotionAnalysisRepository emotionAnalysisRepository;
    private final RestTemplate restTemplate;
    private final String emotionApiUrl;
    private final String emotionApiKey;

    @Autowired
    public EmotionAnalysisService(EmotionAnalysisRepository emotionAnalysisRepository,
                                  RestTemplateBuilder restTemplateBuilder,
                                  @Value("${emotion.api.url:}") String emotionApiUrl,
                                  @Value("${emotion.api.key:}") String emotionApiKey) {
        this.emotionAnalysisRepository = emotionAnalysisRepository;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.emotionApiUrl = emotionApiUrl;
        this.emotionApiKey = emotionApiKey;
    }

    /**
     * 다이어리 내용을 분석하고 결과를 저장 또는 갱신한다.
     */
    public EmotionAnalysis analyzeAndSave(String diaryId, String content)
            throws ExecutionException, InterruptedException {
        Map<String, Double> scores = requestEmotionScores(content);

        EmotionAnalysis analysis = emotionAnalysisRepository.findByDiaryId(diaryId);
        if (analysis == null) {
            analysis = new EmotionAnalysis();
            analysis.setDiaryId(diaryId);
        }
        analysis.setResult(scores);
        return emotionAnalysisRepository.save(analysis);
    }

    /**
     * 다이어리의 감정 분석 결과를 조회한다.
     */
    public EmotionAnalysis getByDiaryId(String diaryId)
            throws ExecutionException, InterruptedException {
        return emotionAnalysisRepository.findByDiaryId(diaryId);
    }

    /**
     * 다이어리 삭제 시 분석 결과도 함께 제거한다.
     */
    public void deleteByDiaryId(String diaryId) throws ExecutionException, InterruptedException {
        EmotionAnalysis existing = emotionAnalysisRepository.findByDiaryId(diaryId);
        if (existing != null) {
            emotionAnalysisRepository.deleteById(existing.getId());
        }
    }

    /**
     * AI 팀에서 제공할 감정 분석 API를 호출하는 스켈레톤 로직.
     * 실 서비스 연동 시 이 메서드만 수정하면 된다.
     */
    private Map<String, Double> requestEmotionScores(String content) {
        if (content == null || content.isBlank()) {
            return Map.of("neutral", 1.0);
        }

        if (emotionApiUrl == null || emotionApiUrl.isBlank()) {
            logger.warn("emotion.api.url이 설정되지 않아 감정 분석을 건너뜁니다.");
            return Map.of("neutral", 1.0);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (emotionApiKey != null && !emotionApiKey.isBlank()) {
            headers.set("X-API-KEY", emotionApiKey);
        }

        EmotionRequest payload = new EmotionRequest(content);
        HttpEntity<EmotionRequest> entity = new HttpEntity<>(payload, headers);

        try {
            EmotionResponse response = restTemplate.postForObject(
                    emotionApiUrl,
                    entity,
                    EmotionResponse.class
            );

            if (response != null && response.results != null && !response.results.isEmpty()) {
                return response.results;
            }

            logger.warn("감정 분석 API에서 빈 결과를 반환했습니다.");
        } catch (RestClientException ex) {
            logger.error("감정 분석 API 호출 실패", ex);
        }

        return Collections.singletonMap("neutral", 1.0);
    }

    /**
     * 외부 API 요청 페이로드 DTO.
     */
    private record EmotionRequest(String text) {}

    /**
     * 외부 API 응답 DTO (필드는 AI 팀 사양에 맞게 조정 예정).
     */
    private static class EmotionResponse {
        private Map<String, Double> results;

        public Map<String, Double> getResults() {
            return results;
        }

        public void setResults(Map<String, Double> results) {
            this.results = results;
        }
    }
}
