package com.junggobooks.book.domain.exception;

import com.junggobooks.common.domain.DomainException;

/** ISBN이 비어 있거나 형식·체크 디지트가 올바르지 않다. */
public class InvalidIsbnException extends DomainException {

    public InvalidIsbnException(String message) {
        super(message);
    }
}
