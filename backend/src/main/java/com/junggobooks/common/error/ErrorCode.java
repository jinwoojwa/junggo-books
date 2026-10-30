package com.junggobooks.common.error;

/**
 * API 에러 코드. 컨텍스트마다 enum으로 구현하고 응답의 {@code code} 필드로 내려간다.
 *
 * <p>코드 문자열은 클라이언트와의 계약이므로 한 번 정하면 바꾸지 않는다.
 */
public interface ErrorCode {

    /** 응답의 {@code code} 값. 대문자 스네이크 케이스 (예: INVALID_ISBN). */
    String code();

    /** 에러 종류. HTTP 상태는 이 값으로 정해진다. */
    ErrorType type();

    /** 예외에 메시지가 따로 없을 때 쓰는 기본 메시지. */
    String defaultMessage();
}
