# ADR-012 패키지는 컨텍스트별 헥사고널 구조로 나누고 ArchUnit으로 강제한다

- 상태: 채택
- 날짜: 2026-10-29

## 맥락

백엔드는 단일 Spring Boot 앱이지만(ADR-002) 서점 어댑터(ADR-005)와 도서 API(ADR-004)처럼 외부와 닿는 부분이 많고, 수집 부하가 커지면 어댑터만 분리할 수 있어야 한다. 구현 전에 의존 방향을 정해 두지 않으면 코드가 쌓이면서 경계가 무너진다.

## 결정

컨텍스트 아래에 헥사고널(ports & adapters) 구조를 둔다.

```
com.junggobooks
├── book/                      # 책 컨텍스트 (ISBN13, 서지 정보)
│   ├── domain/                # 순수 자바. Spring·JPA 의존 금지
│   ├── application/
│   │   ├── port/in/           # 유스케이스 인터페이스
│   │   ├── port/out/          # 외부로 나가는 포트 (저장소, 도서 API)
│   │   └── service/           # 유스케이스 구현
│   └── adapter/
│       ├── in/web/            # REST 컨트롤러, 요청·응답 DTO
│       └── out/               # persistence(JPA 엔티티), bookapi(국립중앙도서관)
├── price/                     # 가격 비교 컨텍스트 (서점 조회, 총액, 최저가 추이)
│   └── (book과 같은 구성. adapter/out/store에 서점별 StoreAdapter 구현)
├── common/                    # 컨텍스트 공통 (에러 응답, 공통 예외)
└── config/                    # 스프링 설정과 빈 조립
```

의존 규칙은 `ArchitectureTest`(ArchUnit)가 빌드에서 검사한다.

- `domain`은 `application`, `adapter`, Spring을 참조하지 않는다.
- `application`은 `adapter`를 참조하지 않는다.
- `adapter.in`과 `adapter.out`은 서로 참조하지 않는다.
- `book`은 `price`를 참조하지 않는다. `price`는 `book`의 `application.port.in`과 `domain`만 쓴다.
- `common`은 `book`, `price`를 참조하지 않는다.
- JPA 엔티티는 `adapter.out.persistence`에 두고 도메인 모델과 매핑한다.

## 대안

- 도메인 우선 구조(컨텍스트 안에 controller/service/repository): 보일러플레이트가 적지만 외부 연동 경계가 코드로 드러나지 않는다.
- 계층 우선 구조: 익숙하지만 서점 어댑터만 분리하기 어렵다.
- 규칙을 문서로만 기록: 구현하다 보면 어겨져도 알아채지 못한다.

## 결과

서점 어댑터와 도서 API 클라이언트를 포트 뒤에 두어 테스트하기 쉽고 나중에 분리할 수 있다. 대신 도메인 모델과 JPA 엔티티를 따로 두는 만큼 매핑 코드가 늘어난다. 작은 컨텍스트에는 과하다고 느껴지면 `domain`에서 JPA 어노테이션을 허용하는 쪽으로 이 결정을 갱신한다.
