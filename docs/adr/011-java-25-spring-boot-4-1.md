# ADR-011 Java 25와 Spring Boot 4.1을 쓴다

- 상태: 채택
- 날짜: 2026-10-29

## 맥락

설계 초안은 Java 21을 기준으로 했다. 프로젝트를 시작하는 2026년 10월 기준으로 Java LTS 최신 버전은 25이고, Spring Boot는 따로 LTS 버전이 없으며 최신 안정 버전은 4.1(OSS 지원 2027년 7월까지)이다.

## 결정

- Java 25 (Gradle 툴체인으로 고정, 로컬에 없으면 자동 다운로드)
- Spring Boot 4.1.x, Gradle 9.x, springdoc-openapi 3.2.x
- Boot 4의 모듈형 스타터(`spring-boot-starter-webmvc`, `spring-boot-starter-flyway` 등)를 쓴다.

## 대안

- Java 21 + Spring Boot 3.5: 자료가 많지만 3.5는 OSS 지원이 2026년 6월에 끝났다.
- Spring Boot 4.0: OSS 지원이 2026년 12월에 끝난다.

## 결과

가상 스레드 등 최신 기능을 그대로 쓰고, 지원 기간이 가장 길다. 대신 Boot 4 기준 자료가 3.x보다 적고, 일부 라이브러리는 Boot 4 호환 버전을 확인해야 한다. 4.1 지원이 끝나기 전(2027년 7월)에 다음 버전으로 올린다.
