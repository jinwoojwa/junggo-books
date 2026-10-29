# 1단계: 빌드
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN ./gradlew --no-daemon dependencies > /dev/null
COPY src src
RUN ./gradlew --no-daemon bootJar -x test

# 2단계: 실행
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 1001 app
COPY --from=build /workspace/build/libs/*.jar app.jar
USER app
EXPOSE 8080
# 힙 크기 등은 운영 compose에서 JAVA_TOOL_OPTIONS로 지정한다 (예: -Xmx768m)
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
