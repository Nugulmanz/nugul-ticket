# 1. Java 17 JDK를 기반으로 한 경량 Alpine 이미지 사용
FROM openjdk:17-jdk-alpine

# 2. 작업 디렉터리 설정
WORKDIR /app

# 3. JAR 파일을 컨테이너의 작업 디렉터리로 복사
COPY build/libs/*.jar /app/nugul-ticket.jar

# 4. Spring Profile을 동적으로 전달받기 위한 ARG 및 ENV 설정
ARG SPRING_PROFILES_ACTIVE=prod
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# 5. 애플리케이션 실행 명령어 설정
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "nugul-ticket.jar"]