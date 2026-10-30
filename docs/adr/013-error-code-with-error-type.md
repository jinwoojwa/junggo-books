# ADR-013 에러 코드는 HTTP 상태 대신 ErrorType을 갖는다

- 상태: 채택
- 날짜: 2026-10-30

## 맥락

업무 오류는 `BusinessException(ErrorCode)`로 던지고, 컨텍스트마다 `ErrorCode`를 구현한 enum을 둔다. `ErrorCode`가 `HttpStatus`를 직접 들고 있으면 서비스(`application`)가 던지는 코드가 Spring에 의존하게 된다. ADR-012에서 `domain`은 Spring을 참조할 수 없고, 에러 코드는 서비스가 던지는 곳에 있어야 해서 둘이 부딪힌다.

## 결정

- `ErrorCode`는 `code()`, `type()`, `defaultMessage()`를 갖는다. `type()`은 프레임워크와 무관한 `ErrorType`(VALIDATION, NOT_FOUND, METHOD_NOT_ALLOWED, UNSUPPORTED_MEDIA_TYPE, TOO_MANY_REQUESTS, UPSTREAM, INTERNAL)이다.
- `ErrorType`을 HTTP 상태로 바꾸는 곳은 `GlobalExceptionHandler.toStatus` 하나다. switch 식이라 종류를 추가하고 변환을 빠뜨리면 컴파일되지 않는다.
- `ErrorCode`, `ErrorType`, `BusinessException`은 Spring을 참조하지 않는다. `ArchitectureTest`가 검사한다.
- 컨텍스트별 코드 enum은 `application`에 둔다(예: `book.application.BookErrorCode`). `domain`의 규칙 위반은 `domain`의 자체 예외로 던지고, 서비스나 어댑터가 `BusinessException`으로 바꾼다. 아직 `Isbn13`은 `IllegalArgumentException`을 던지며, `INVALID_ISBN`으로의 변환은 책 검색 API(B04)에서 한다.
- 코드 문자열은 클라이언트와의 계약이므로 한 번 정하면 바꾸지 않는다. 응답의 `status`, `code`, `detail`은 이 결정 전과 같다.
- 종류는 지금 쓰는 것만 둔다. FORBIDDEN(권한 없음)과 CONFLICT(충돌)는 쓸 곳이 생길 때 추가한다.

## 대안

- 코드 문자열만 두고 `Map<String, HttpStatus>`로 변환: 단순하지만 매핑을 빠뜨리거나 오타가 나도 컴파일되고 조용히 500으로 내려간다.
- 도메인 예외와 웹 어댑터의 에러 코드 매핑으로 완전히 분리: 계층은 가장 깨끗하지만 예외 클래스와 매핑 코드가 늘어난다. 개인 프로젝트 규모에는 과하다.
- `ErrorCode`가 `HttpStatus`를 그대로 보유: 가장 단순하지만 `application`이 Spring에 의존한다.

## 결과

`application`과 에러 코드 정의에서 Spring 의존이 사라지고, HTTP 상태 결정이 한 곳에 모인다. 대신 코드 하나가 응답 상태 하나에 대응하는 단순한 관계만 표현할 수 있다. 같은 종류인데 상태가 다른 코드(예: NOT_FOUND인데 410)가 필요해지면 새 `ErrorType`을 추가한다. 사용자에게 보이는 상태가 종류와 코드 둘로 나뉘는 일이 잦아지면 이 결정을 다시 본다.
