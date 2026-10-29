# ADR-009 DB는 Cloud SQL PostgreSQL, 스키마는 Flyway로 관리한다

- 상태: 대체됨 → [ADR-010](010-single-vm-docker-compose.md) (Flyway 사용은 유지)
- 날짜: 2026-10-29

## 맥락

책 정보, 검색·수집 로그, 일별 최저가를 저장해야 한다. GCP 매니지드 서비스를 쓰기로 했다.

## 결정

서울 리전 Cloud SQL PostgreSQL 최소 사양(db-f1-micro)을 쓰고, 자동 백업은 매일·7일 보관한다. 스키마 변경은 Flyway 마이그레이션 파일로만 하고, 운영은 `ddl-auto=validate`로 둔다.

## 대안

- Firestore: 무료 할당량, 관계형 설계·JPA 포기
- Supabase: 무료 티어, GCP 밖
- JPA `ddl-auto`: 운영 스키마 변경 위험
- Liquibase

## 결과

Cloud SQL은 상시 무료 티어가 없어 최소 사양도 매달 고정 비용이 나간다. VM 한 대에 PostgreSQL을 함께 올리는 ADR-010으로 대체했고, Flyway는 그대로 쓴다.
