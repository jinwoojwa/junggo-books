package com.junggobooks.common.domain;

/**
 * 도메인 규칙(불변식) 위반을 나타내는 기반 예외. 컨텍스트마다 이를 상속해 규칙별 예외를 둔다.
 *
 * <p>에러 코드나 HTTP 정보는 담지 않는다. 응답으로 바꾸는 일은 유스케이스가 {@code BusinessException}으로 변환해서 맡고, 변환되지 않고 올라오면
 * 버그로 보고 500으로 내려간다. 메시지는 로그를 위한 것이라 응답에 그대로 쓰지 않는다.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
