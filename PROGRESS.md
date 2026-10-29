# PROGRESS

## 현재 상태

- 기반 구축 단계. 백엔드 뼈대(Spring Boot 4.1 / Java 25), Flyway 마이그레이션 V1~V3, ADR 11개, 검증 스크립트와 Claude Code 하네스(Hook, Skill, reviewer)가 있다.
- 도메인·API 코드는 아직 없다(`JunggoBooksApplication`만 존재). 구현은 사용자가 직접 수행한다.
- 배포 방식은 ADR-010(VM + Docker Compose)으로 확정되어 있다. ADR-008·009는 이미 "대체됨"이다.
- 프론트엔드는 스캐폴딩만 있다(Next.js 16, App Router, TypeScript, pnpm; 화면 없음). `verify.sh`와 CI가 lint·typecheck·build를 검사한다.
- CI(GitHub Actions)가 동작하고 main은 PR + squash merge + `verify` 통과로 보호된다. 포맷은 Spotless(google-java-format AOSP)로 검사한다.

## 세션 로그

### 2026-10-29

- 프로젝트 진행 상황 점검.
- F02: CI 워크플로 추가, main 브랜치 보호와 squash merge 전용 설정 (PR #1).
- F03: Spotless(google-java-format AOSP) 도입, 기존 코드 재포맷, `.editorconfig` 추가.
- F04: ADR-012(컨텍스트별 헥사고널 구조)와 ArchUnit `ArchitectureTest` 추가. JPA 엔티티는 `adapter.out.persistence`에 분리한다.
- F05: `common.error`에 ErrorCode, CommonErrorCode, BusinessException, GlobalExceptionHandler 추가. 응답은 ProblemDetail + `code`(문자열 상수) + 검증 오류 시 `errors[]`.
- F07: `CorsConfig`(/api/** GET만, 허용 출처는 환경 변수)와 prod 필수 환경 변수(CORS_ALLOWED_ORIGINS, CRAWLER_USER_AGENT) 기본값 제거 + 검증 테스트.
- F06(서지 API 연동 설정)은 API 세부가 정해질 때까지 보류.
- F08: `frontend/`에 Next.js 스캐폴딩(Tailwind 제외, `typecheck` 스크립트 추가, Node 24), verify.sh·CI에 frontend 단계 연동.
- F09: Dependabot 설정(주 1회 월요일, 생태계별 minor/patch 묶음, PR 제목 `chore(deps)`).
- 기반 구축에서 추가할 항목을 정리하고 `feature_list.json`(기반 F, 백엔드 B, 프론트 FE, 배포 D)과 `PROGRESS.md`를 작성했다 (F01).

## 다음 세션에서 할 일

- 기반 구축 항목을 하나씩 확정하고 진행한다: F10 → F11. F06은 서지 API 세부 확정 후.
