# junggo-books

책 한 권을 검색하면 서점이 직접 파는 중고 매물의 가격을 배송비 포함 총액으로 비교해 주는 서비스. 비영리 개인 프로젝트.

## 구조

- `backend/` — Spring Boot 4.1 · Java 25 (Gradle). 빌드 파일은 이 디렉터리에 있다.
- `frontend/` — Next.js 16 · App Router · TypeScript (pnpm)
- `docs/` — 설계 문서 안내와 ADR (`docs/adr/`)

## 명령어

모든 Gradle 명령은 `backend/`에서 실행한다.

```bash
cd backend
./gradlew bootRun   # 서버 실행. compose.yaml의 PostgreSQL을 자동으로 띄운다 (Docker 필요)
./gradlew test      # 테스트
./gradlew build     # 빌드 + 테스트 + 포맷 검사(spotlessCheck)
./gradlew spotlessApply  # 포맷 자동 정리 (google-java-format AOSP: 4칸, 100자)
```

프론트엔드 명령은 `frontend/`에서 `corepack pnpm <script>`로 실행한다(`dev`, `lint`, `typecheck`, `build`). pnpm 버전은 `package.json`의 `packageManager`가 정한다.

변경을 마치기 전에는 루트에서 `scripts/verify.sh`를 실행해 백엔드 빌드·테스트와 프론트엔드 lint·typecheck·build가 통과하는지 확인한다. 마이그레이션 테스트가 Testcontainers로 PostgreSQL을 띄우므로 Docker가 실행 중이어야 한다.

- 헬스체크: `http://localhost:8080/actuator/health`, API 문서: `http://localhost:8080/swagger-ui`
- IDE에서 `main`을 직접 실행할 때는 Working directory를 `backend`로 맞춘다. 아니면 `compose.yaml`을 찾지 못한다.

## 설정

- 기본 프로파일은 `local`이다. 필수 환경 변수는 `prod`에만 있다 (`DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` 등).
- 환경 변수 예시는 `backend/.env.example`에 둔다. 실제 `.env`와 비밀 값은 커밋하지 않는다.
- `application.yml`의 값에 `: `(콜론+공백)가 들어가면 반드시 따옴표로 감싼다. YAML이 key-value로 오해해 기동에 실패한다.

## 규칙

- 스키마 변경은 Flyway 마이그레이션(`backend/src/main/resources/db/migration/V{n}__*.sql`)으로만 한다. 이미 있는 파일은 수정하지 않고 항상 새 버전을 추가한다. 절차와 SQL 관례는 `add-flyway-migration` Skill을 따른다.
- 패키지는 `book`·`price` 컨텍스트별 헥사고널 구조(`domain` / `application` / `adapter`)를 따른다. 상세와 의존 규칙은 ADR-012, 검사는 `ArchitectureTest`.
- 책 식별 기준키는 ISBN13이다. ISBN10은 입력 시 변환한다.
- 서점별 수집은 어댑터 패턴을 따르고, HTML 파싱은 Jsoup을 우선한다.
- 책 정보는 국립중앙도서관 서지 API를 사용한다. 연동 설정(환경 변수 이름 등)은 아직 확정 전이다.

## 에러 응답

- 모든 에러 응답은 RFC 9457 `ProblemDetail`이고 `code` 확장 필드(대문자 스네이크 케이스 문자열)를 항상 포함한다. 검증 실패는 `errors[]`(field, message)를 더한다.
- 업무 오류는 `BusinessException(ErrorCode)`로 던진다. 컨텍스트별 코드는 `common.error.ErrorCode`를 구현한 enum으로 정의하고, 한 번 정한 코드 문자열은 바꾸지 않는다.
- 처리되지 않은 예외는 내부 정보 없이 500 `INTERNAL_ERROR`로 내려간다. 응답 변환은 `GlobalExceptionHandler`가 맡는다.

## 라벨

커밋 type과 같은 이름의 라벨을 쓴다: ✨ feat, 🐛 fix, ♻️ refactor, ✅ test, 📝 docs, 🔧 chore, ⚡ perf, 📦 dependencies (Dependabot PR). 라벨 이름은 이모지를 포함해서 참조한다 (이슈 폼, `dependabot.yml`).

## Git hook

clone 후 `scripts/setup-hooks.sh`를 한 번 실행해 `.githooks/`를 활성화한다.

- `pre-commit`: 스테이지된 Java 파일의 포맷(Spotless)만 검사한다.
- `commit-msg`: 제목이 Conventional Commits 형식인지, 끝에 구두점이 없는지 검사한다 (`scripts/test-hooks.sh`로 테스트).
- `pre-push`: `scripts/verify.sh` 전체를 실행한다. 급할 때만 `--no-verify`를 쓰고, CI가 어차피 다시 검사한다.

## 브랜치와 머지

- `main`에 직접 커밋하지 않는다. 작업마다 `feat/*`, `fix/*`, `chore/*`, `docs/*` 같은 타입별 브랜치를 만들어 PR로 머지한다.
- 머지는 squash merge만 쓴다. PR 제목을 Conventional Commits 형식으로 쓰면 그대로 커밋 메시지가 된다.
- CI(`.github/workflows/ci.yml`)가 PR과 `main` push마다 `scripts/verify.sh`를 실행한다. 통과해야 머지한다.

## 문서

- 설계 결정은 `docs/adr/`에 ADR로 남긴다. 결정을 바꿀 때는 기존 기록을 지우지 않고 상태를 "대체됨"으로 바꾼 뒤 새 기록을 추가한다 (`docs/adr/template.md`).
- 겪은 문제는 `docs/troubleshooting/`에 증상·원인·해결·재발 방지로 남긴다 (`docs/troubleshooting/template.md`). 원인을 찾아 해결했으면 기록하고 `docs/README.md` 표에 추가한다. 비슷한 증상을 만나면 먼저 여기를 찾아본다.
- 설계서 원본(API 명세, ERD, 기능 명세 등)은 노션에서 관리한다. 목록은 `docs/README.md`.
