---
name: add-flyway-migration
description: DB 스키마를 바꿀 때 Flyway 마이그레이션 파일을 추가하는 절차와 이 프로젝트의 SQL 관례. 테이블·컬럼·인덱스·제약을 추가하거나 바꾸는 작업, "마이그레이션 추가", "스키마 변경" 요청에 사용한다.
---

# Flyway 마이그레이션 추가

스키마는 Flyway 마이그레이션으로만 바꾼다. 위치는 `backend/src/main/resources/db/migration/`이다.

## 절대 규칙

- **이미 있는 `V*.sql`은 수정하지 않는다.** 항상 다음 버전을 새로 추가한다. (커밋된 파일은 Hook이 막는다.)
- 번호는 가장 큰 `V{n}`에 1을 더한 연속 번호를 쓴다. 건너뛰거나 되돌리지 않는다.

## 절차

1. `ls backend/src/main/resources/db/migration/`으로 마지막 버전을 확인한다.
2. `V{n+1}__{동사}_{대상}.sql`을 만든다. 예: `V4__add_condition_to_book.sql`, `V5__create_price_alert.sql`
3. 아래 SQL 관례를 따라 작성한다.
4. 테이블 정의가 바뀌면 JPA 엔티티를 같이 바꾼다. `ddl-auto: validate`라서 어긋나면 기동이 실패한다. (엔티티가 생기기 전에는 해당 없음)
5. `scripts/verify.sh`를 실행한다. `MigrationTest`가 빈 PostgreSQL에 전체 마이그레이션을 적용해 검증한다. Docker가 떠 있어야 한다.
6. 새 테이블·제약이 있으면 `MigrationTest`에 그 동작을 확인하는 테스트를 추가한다. (예: CHECK 제약이 잘못된 값을 막는지)
7. 필요하면 `bootRun`으로 실제 기동을 확인한다.

## SQL 관례

| 항목 | 규칙 |
|---|---|
| 헤더 | 파일 맨 위 주석에 테이블의 **목적과 보관·갱신 정책**을 적는다 (예: "90일 지나면 삭제") |
| 식별자 | `snake_case`. 인덱스 `idx_{table}_{cols}`, 제약 `chk_{table}_{col}` |
| 시각 | `TIMESTAMPTZ` |
| ISBN | `CHAR(13)` (ISBN13이 기준키) |
| PK | 대리키는 `BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY` |
| 금액 | `INTEGER` (원 단위) |
| 열거형 | PostgreSQL ENUM을 쓰지 않는다. `VARCHAR` + `CHECK (col IN (...))` |
| FK | `REFERENCES book (isbn13)`처럼 대상 컬럼까지 명시한다 |

기존 파일(`V1`~`V3`)이 기준 예시다. 새로 쓰기 전에 하나 읽어 형식을 맞춘다.

## 주의

- **파괴적 변경**(컬럼 삭제, 타입 변경, 기존 컬럼에 `NOT NULL` 추가)은 기존 데이터가 있으면 실패하거나 손실된다. 단계로 나누고(추가 → 이관 → 제거), 운영 적용 전 DB 백업 시점을 확인한다. 하려는 변경이 파괴적이면 먼저 사용자에게 알린다.
- 마이그레이션 안에서 비밀 값이나 환경 의존 값을 쓰지 않는다.
