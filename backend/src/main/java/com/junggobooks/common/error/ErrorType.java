package com.junggobooks.common.error;

/**
 * 에러의 종류. 프레임워크와 무관한 분류이고, HTTP 상태로의 변환은 {@link GlobalExceptionHandler}가 맡는다.
 *
 * <p>새 종류를 추가하면 {@link GlobalExceptionHandler}의 변환 switch도 함께 고쳐야 컴파일된다.
 */
public enum ErrorType {
    /** 요청 값이 올바르지 않다. */
    VALIDATION,
    NOT_FOUND,
    METHOD_NOT_ALLOWED,
    UNSUPPORTED_MEDIA_TYPE,
    TOO_MANY_REQUESTS,
    /** 외부 서비스(서지 API 등)를 쓸 수 없다. */
    UPSTREAM,
    INTERNAL
}
