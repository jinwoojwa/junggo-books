# 문서

## 설계 문서

설계 문서 원본은 노션(개인 워크스페이스)에서 관리하고, 기술 의사결정 기록(ADR)은 코드와 함께 이 저장소에 남깁니다.

| 문서 | 내용 |
| --- | --- |
| 프로젝트 개요서 | 기본 정보, 배경·목적, 타겟 사용자·요구사항, 기능 범위, 기술 스택 |
| 서점 수집 설계서 | 수집 원칙·범위, 공통 처리 흐름, 어댑터, 정규화 규칙, 조회 결과 상태 |
| API 명세서 | `/api/v1` REST API, 에러 코드, 요청 제한 |
| ERD · 데이터 모델 | 테이블 명세, 캐시 구조, Flyway 관리 |
| 기능 명세서 | 화면별 동작·예외 (S-01 ~ S-04) |
| 화면 설계 | 모바일·데스크톱 와이어프레임 |
| 개발 일정 · 백로그 | 단계(M0 ~ M6)와 이슈 목록 |
| 배포 · 운영 설계 | VM·Docker Compose 구성, CI/CD, 백업, 알림, 비용 |

## 기술 의사결정 기록 (ADR)

결정을 바꿀 때는 기존 기록을 지우지 않고 상태를 "대체됨"으로 바꾼 뒤 새 기록을 추가합니다. 새 기록은 [template.md](adr/template.md)를 복사해 씁니다.

| 번호 | 결정 | 상태 |
| --- | --- | --- |
| [001](adr/001-query-stores-on-search-only.md) | 서점 데이터는 검색 시점에만 조회한다 | 채택 |
| [002](adr/002-single-spring-boot-backend.md) | 백엔드는 Spring Boot 단일 애플리케이션으로 둔다 | 채택 |
| [003](adr/003-isbn13-as-book-key.md) | 책 식별 기준키는 ISBN13이다 | 채택 |
| [004](adr/004-book-info-from-official-api.md) | 책 정보는 공식 도서 API에서 받는다 | 채택 |
| [005](adr/005-store-adapter-pattern.md) | 서점별 어댑터 패턴을 쓴다 | 채택 |
| [006](adr/006-jsoup-first-playwright-fallback.md) | HTML 파싱은 Jsoup 우선, Playwright는 예외적으로만 | 채택 |
| [007](adr/007-caffeine-cache.md) | 캐시는 Caffeine으로 시작하고 Redis는 보류한다 | 채택 |
| [008](adr/008-cloud-run.md) | 백엔드는 GCP Cloud Run(최소 0·최대 1)에 배포한다 | 대체됨 → 010 |
| [009](adr/009-cloud-sql-flyway.md) | DB는 Cloud SQL PostgreSQL, 스키마는 Flyway로 관리한다 | 대체됨 → 010 |
| [010](adr/010-single-vm-docker-compose.md) | 백엔드·DB는 Compute Engine VM 한 대에 Docker Compose로 운영한다 | 채택 |
| [011](adr/011-java-25-spring-boot-4-1.md) | Java 25와 Spring Boot 4.1을 쓴다 | 채택 |
| [012](adr/012-hexagonal-package-structure.md) | 패키지는 컨텍스트별 헥사고널 구조로 나누고 ArchUnit으로 강제한다 | 채택 |
| [013](adr/013-error-code-with-error-type.md) | 에러 코드는 HTTP 상태 대신 ErrorType을 갖는다 | 채택 |

## 트러블슈팅

겪은 문제의 증상·원인·해결·재발 방지를 남깁니다. 같은 문제를 다시 만났을 때 먼저 여기를 찾아봅니다. 새 기록은 [template.md](troubleshooting/template.md)를 복사해 `NNN-slug.md`로 쓰고, 아래 표에 추가합니다.

| 번호 | 문제 | 영역 |
| --- | --- | --- |
| [001](troubleshooting/001-yaml-colon-in-default-value.md) | application.yml 기본값의 콜론 때문에 부트런이 실패한다 | backend |
| [002](troubleshooting/002-ide-working-directory-compose-yaml.md) | IDE에서 실행하면 compose.yaml을 찾지 못한다 | backend |
