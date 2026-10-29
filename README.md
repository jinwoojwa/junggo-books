# 중고책 최저가 (가제)

책 한 권을 검색하면 서점이 직접 파는 중고 매물의 가격을 **배송비 포함 총액**으로 비교하고, 가장 싼 곳으로 연결해 주는 서비스입니다.

- 비교 서점: 알라딘(알라딘 중고, 이 광활한 우주점), YES24(직접 판매 중고)
- 책 정보는 공식 도서 API에서, 서점에서는 판매 가격·배송비·상태·링크만 검색 시점에 조회합니다.
- 비영리 개인 프로젝트 (포트폴리오·학습 목적)

## 구조

```
junggo-books/
├── backend/    # Spring Boot 4.1 · Java 25
├── frontend/   # Next.js (TypeScript)
└── docs/       # 설계 문서 안내, ADR
```

## 기술 스택

| 영역 | 선택 |
| --- | --- |
| 백엔드 | Java 25, Spring Boot 4.1, Spring Data JPA, Flyway, Caffeine |
| DB | PostgreSQL 18 |
| 프론트엔드 | Next.js (TypeScript), Vercel |
| 인프라 | GCP Compute Engine VM + Docker Compose (Caddy · Spring Boot · PostgreSQL) |
| CI/CD | GitHub Actions → Artifact Registry → VM |

## 로컬 실행 (backend)

필요한 것: Docker. Java 25는 Gradle 툴체인이 없으면 자동으로 내려받습니다.

```bash
cd backend
./gradlew bootRun
```

- `bootRun`이 `compose.yaml`의 PostgreSQL 컨테이너를 자동으로 띄우고 Flyway 마이그레이션을 적용합니다.
- API 문서: http://localhost:8080/swagger-ui
- 헬스체크: http://localhost:8080/actuator/health

## 문서

설계 문서와 기술 의사결정 기록(ADR)은 [docs](docs/README.md)를 보세요.
