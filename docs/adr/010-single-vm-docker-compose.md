# ADR-010 백엔드·DB는 Compute Engine VM 한 대에 Docker Compose로 운영한다

- 상태: 채택
- 날짜: 2026-10-29
- 대체: [ADR-008](008-cloud-run.md), [ADR-009](009-cloud-sql-flyway.md)

## 맥락

Cloud Run(최소 0)을 검토한 결과, 이 프로젝트의 핵심인 서점 조회와 맞지 않는 점이 있었다.

- 트래픽이 적어 인스턴스가 자주 내려가면 3시간 캐시가 거의 유지되지 않아 서점 요청이 늘어난다.
- 콜드 스타트가 서점 조회 시간(최대 5초) 위에 더해진다.
- 외부 요청 IP가 고정되지 않는다.
- Playwright가 필요해지면 서버리스의 제약이 더 커진다.

## 결정

서울 리전 Compute Engine e2-small(2GB)에 고정 IP를 붙이고, Docker Compose로 Caddy(HTTPS·리버스 프록시), Spring Boot, PostgreSQL을 함께 운영한다.

- DB: 같은 VM의 PostgreSQL 컨테이너, 스키마는 Flyway 유지
- 백업: 매일 `pg_dump` → Cloud Storage(7일 보관)
- 주기 작업: Spring `@Scheduled`와 cron
- 비밀값: GitHub Secrets → 배포 시 VM의 `.env`로 생성
- 월 예산: 3만 원

## 대안

- Cloud Run + Cloud SQL (ADR-008·009): 운영 부담은 최소지만 위 문제
- Cloud Run 최소 인스턴스 1: 콜드 스타트·캐시는 해결, IP 고정은 별도 비용
- VM + Cloud SQL: 관리형 백업이지만 비용 합산 증가
- e2-micro: 비용 절반이지만 메모리 부족

## 결과

캐시와 요청 간격 제한이 설계대로 동작하고, 서점 요청이 고정 IP에서 나간다. 대신 OS 업데이트, HTTPS(Caddy 자동), 백업, 디스크 관리를 직접 한다. VM 한 대가 단일 장애 지점이 되지만 데모 단계에서는 감수한다. 트래픽이 커지면 DB를 Cloud SQL로 분리하는 것부터 검토한다.
