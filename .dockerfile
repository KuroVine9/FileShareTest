FROM gradle:7.4-jdk17-alpine as builder
WORKDIR /build

# 그래들 파일이 변경되었을 때만 새롭게 의존패키지 다운로드 받게함.
COPY build.gradle.kts settings.gradle.kts /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 빌더 이미지에서 애플리케이션 빌드
COPY . /build
RUN gradle bootwar

# APP
FROM openjdk:17.0-slim
WORKDIR /app

# 빌더 이미지에서 jar 파일만 복사
COPY --from=builder /build/build/libs/FileShare-0.0.1-SNAPSHOT.war .

# root 대신 nobody 권한으로 실행
USER nobody
ENTRYPOINT ["java","-jar","./FileShare-0.0.1-SNAPSHOT.war"]

# 출처 : https://findstar.pe.kr/2022/05/13/gradle-docker-cache/