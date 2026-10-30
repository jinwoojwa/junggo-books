package com.junggobooks.book.application;

import com.junggobooks.common.error.ErrorCode;
import com.junggobooks.common.error.ErrorType;

/** 책 컨텍스트의 에러 코드. 코드 문자열은 클라이언트와의 계약이라 바꾸지 않는다. */
public enum BookErrorCode implements ErrorCode {
    INVALID_ISBN(ErrorType.VALIDATION, "유효하지 않은 ISBN입니다."),
    BOOK_NOT_FOUND(ErrorType.NOT_FOUND, "책을 찾을 수 없습니다.");

    private final ErrorType type;
    private final String defaultMessage;

    BookErrorCode(ErrorType type, String defaultMessage) {
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
