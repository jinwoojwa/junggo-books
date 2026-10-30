# ADR-015 도메인 규칙 위반은 DomainException을 상속한 예외로 던진다

- 상태: 채택
- 날짜: 2026-10-30

## 맥락

ADR-013은 "`domain`의 규칙 위반은 `domain`의 자체 예외로 던지고, 서비스나 어댑터가 `BusinessException`으로 바꾼다"고 정했다. 그런데 `Isbn13`은 아직 `IllegalArgumentException`을 던진다. 이 예외는 범위가 너무 넓다. 서점 파서가 `Integer.parseInt`에 실패해 던지는 `NumberFormatException`도 `IllegalArgumentException`의 하위 클래스라, 어댑터가 `IllegalArgumentException`을 잡으면 도메인 규칙 위반과 파싱 버그를 구분하지 못한다. 매물(`Offer`, B06)에도 같은 규칙 위반이 생긴다.

## 결정

- `common.domain.DomainException`을 기반 예외(추상, `RuntimeException`)로 둔다. 컨텍스트마다 이를 상속해 규칙별 예외를 둔다(예: `book.domain.exception.InvalidIsbnException`, 이후 `price.domain.exception.InvalidOfferException`).
- `DomainException`은 Spring을 참조하지 않고, 에러 코드와 HTTP 정보를 담지 않는다. `ArchitectureTest`가 Spring 비참조를 검사한다. 에러 코드는 ADR-013대로 `application`에 둔다.
- 예외는 던진 곳에서 처리하지 않고 의미를 아는 바깥 계층이 처리한다.
  - 사용자 입력에서 온 위반은 유스케이스가 `BusinessException(ErrorCode)`로 바꾼다(예: `InvalidIsbnException` → `BookErrorCode.INVALID_ISBN`, B04).
  - 외부 데이터에서 온 위반은 어댑터가 잡아 그 항목만 건너뛰고 경고 로그를 남긴다(ADR-014).
  - 아무도 잡지 않은 `DomainException`은 버그로 보고 기존 `Exception` 핸들러가 내부 정보 없이 500 `INTERNAL_ERROR`로 내려보낸다. 별도 핸들러는 두지 않는다.
- 예외 메시지는 로그를 위한 것이다. 응답의 `detail`에는 에러 코드의 기본 메시지를 쓴다.
- `Isbn13`은 `InvalidIsbnException`을 던진다. 이로써 ADR-013의 "아직 `Isbn13`은 `IllegalArgumentException`을 던진다"는 부분은 이 결정으로 대체된다. ADR-013의 나머지 결정은 그대로다.

## 대안

- 컨텍스트마다 예외를 따로 두고 기반 클래스를 두지 않는다: 클래스는 적지만 "도메인 규칙 위반 전체"를 한 번에 다룰 수 없고, 컨텍스트가 늘면 공통 처리가 흩어진다.
- `IllegalArgumentException`을 그대로 쓴다: 클래스는 늘지 않지만 규칙 위반과 버그를 구분하지 못한다.
- `DomainException`이 `ErrorCode`를 직접 든다: 변환 단계가 줄지만 `domain`이 응답 계약(코드 문자열)을 알게 되어 ADR-013과 어긋난다.

## 결과

어댑터와 유스케이스가 도메인 규칙 위반만 정확히 잡을 수 있고, 처리되지 않은 위반은 500으로 안전하게 내려간다. 대신 VO마다 예외 클래스가 하나씩 늘어난다. 한 컨텍스트의 도메인 예외가 많아져 의미 단위로 묶고 싶어지면 컨텍스트별 중간 기반 클래스를 둔다. 도메인 예외를 항상 `BusinessException`으로 바꾸는 코드가 반복되면 변환을 한곳에 모으는 방법을 다시 본다.
