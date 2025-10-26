# Docker 실행 스크립트
$env:FIREBASE_CREDENTIALS = Get-Content "src\main\resources\diarygarden-7bb2d-firebase-adminsdk-fbsvc-eb51c377a7.json" -Raw
$env:GCP_PROJECT_ID = "diarygarden-7bb2d"
$env:FIREBASE_SECRET_NAME = ""

Write-Host "환경 변수 설정 완료!"
Write-Host "Docker 실행 중..."

docker-compose up --build

