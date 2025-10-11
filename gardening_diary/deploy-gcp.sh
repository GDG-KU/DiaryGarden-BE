#!/bin/bash

# GCP 배포 스크립트
set -e

# 색상 코드
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}GCP Cloud Run 배포 스크립트${NC}"
echo -e "${GREEN}========================================${NC}"

# 환경 변수 확인
if [ -z "$GCP_PROJECT_ID" ]; then
    echo -e "${YELLOW}GCP_PROJECT_ID 환경 변수를 설정해주세요.${NC}"
    read -p "GCP 프로젝트 ID를 입력하세요: " GCP_PROJECT_ID
fi

if [ -z "$FIREBASE_KEY_FILE" ]; then
    echo -e "${YELLOW}FIREBASE_KEY_FILE 환경 변수를 설정해주세요.${NC}"
    read -p "Firebase 키 파일 경로를 입력하세요: " FIREBASE_KEY_FILE
fi

# GCP 프로젝트 설정
echo -e "${GREEN}[1/5] GCP 프로젝트 설정 중...${NC}"
gcloud config set project $GCP_PROJECT_ID

# Secret Manager에 Firebase 키 저장 (처음만)
echo -e "${GREEN}[2/5] Secret Manager에 Firebase 자격 증명 저장 중...${NC}"
if gcloud secrets describe firebase-credentials --project=$GCP_PROJECT_ID >/dev/null 2>&1; then
    echo -e "${YELLOW}Secret이 이미 존재합니다. 새 버전을 추가합니다.${NC}"
    gcloud secrets versions add firebase-credentials --data-file="$FIREBASE_KEY_FILE"
else
    echo -e "${GREEN}새 Secret을 생성합니다.${NC}"
    gcloud secrets create firebase-credentials --data-file="$FIREBASE_KEY_FILE"
fi

# Docker 이미지 빌드
echo -e "${GREEN}[3/5] Docker 이미지 빌드 중...${NC}"
docker build -t gcr.io/$GCP_PROJECT_ID/gardening-diary:latest -f Dockerfile.prod .

# Container Registry에 푸시
echo -e "${GREEN}[4/5] Container Registry에 이미지 푸시 중...${NC}"
docker push gcr.io/$GCP_PROJECT_ID/gardening-diary:latest

# Cloud Run에 배포
echo -e "${GREEN}[5/5] Cloud Run에 배포 중...${NC}"
gcloud run deploy gardening-diary \
    --image gcr.io/$GCP_PROJECT_ID/gardening-diary:latest \
    --region asia-northeast3 \
    --platform managed \
    --allow-unauthenticated \
    --set-env-vars SPRING_PROFILES_ACTIVE=prod \
    --set-secrets FIREBASE_CREDENTIALS=firebase-credentials:latest \
    --memory 512Mi \
    --cpu 1 \
    --max-instances 10 \
    --min-instances 0

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}배포가 완료되었습니다! 🎉${NC}"
echo -e "${GREEN}========================================${NC}"

# 서비스 URL 출력
SERVICE_URL=$(gcloud run services describe gardening-diary --region=asia-northeast3 --format='value(status.url)')
echo -e "${GREEN}서비스 URL: ${NC}$SERVICE_URL"
