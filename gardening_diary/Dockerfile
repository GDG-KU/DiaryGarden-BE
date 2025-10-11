# 빌드 스테이지
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Gradle 의존성을 먼저 복사하고 캐싱
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 의존성 다운로드 (캐싱 최적화)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew bootJar --no-daemon -x test

# 런타임 스테이지
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 보안을 위한 비-root 사용자 생성
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 빌드 스테이지에서 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 애플리케이션 포트 노출 (Spring Boot 기본 포트)
EXPOSE 8080

# JVM 옵션 설정 (메모리 최적화)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
