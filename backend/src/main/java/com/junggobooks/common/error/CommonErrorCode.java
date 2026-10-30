package com.junggobooks.common.error;

/** 컨텍스트와 무관하게 쓰는 에러 코드. */
public enum CommonErrorCode implements ErrorCode {
    INVALID_REQUEST(ErrorType.VALIDATION, "요청이 올바르지 않습니다."),
    NOT_FOUND(ErrorType.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(ErrorType.METHOD_NOT_ALLOWED, "지원하지 않는 요청 방식입니다."),
    UNSUPPORTED_MEDIA_TYPE(ErrorType.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 요청 형식입니다."),
    TOO_MANY_REQUESTS(ErrorType.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요."),
    INTERNAL_ERROR(ErrorType.INTERNAL, "서버 오류가 발생했습니다."),
    UPSTREAM_UNAVAILABLE(ErrorType.UPSTREAM, "외부 서비스에 연결할 수 없습니다.");

    private final ErrorType type;
    private final String defaultMessage;

    CommonErrorCode(ErrorType type, String defaultMessage) {
        this.type = type;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public ErrorType type() {
        return type;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }
}
