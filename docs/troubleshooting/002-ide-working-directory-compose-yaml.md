# TS-002 IDE에서 실행하면 compose.yaml을 찾지 못한다

- 날짜: 2026-10-29
- 영역: backend

## 증상

IntelliJ에서 `JunggoBooksApplication`을 실행하면 시작 직후 종료된다. 같은 코드가 터미널 `./gradlew bootRun`에서는 정상 기동한다.

```
java.lang.IllegalArgumentException: 'files' content [compose.yaml] must exist
    at org.springframework.boot.docker.compose.core.DockerComposeFile.of(...)
```

## 원인

`spring-boot-docker-compose`는 작업 디렉터리에서 `compose.yaml`을 찾는다. 이 파일은 `backend/compose.yaml`인데, IDE 실행 구성의 Working directory가 프로젝트 루트였다(로그: `started by jinwoo in /Users/jinwoo/dev/projects/junggo-books`). `bootRun`은 작업 디렉터리가 자동으로 `backend`라서 문제가 없었다.

## 해결

실행 구성의 Working directory를 `backend`(`$MODULE_WORKING_DIR$`)로 바꾼다. IntelliJ에서 `backend`를 Gradle 프로젝트로 링크하면 기본값이 모듈 디렉터리가 된다.

## 재발 방지

`AGENTS.md`의 명령어 섹션에 IDE 작업 디렉터리 주의를 적었다. `application-local.yml`에 `spring.docker.compose.file: backend/compose.yaml`을 넣는 방법은 `bootRun`(작업 디렉터리 `backend`)에서 경로가 깨져서 쓰지 않는다.
