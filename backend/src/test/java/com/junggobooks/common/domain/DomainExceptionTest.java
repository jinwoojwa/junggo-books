package com.junggobooks.common.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomainExceptionTest {

    @Test
    void 메시지를_그대로_돌려준다() {
        DomainException exception = new TestDomainException("규칙 위반");

        assertThat(exception).hasMessage("규칙 위반");
    }

    @Test
    void 원인_예외를_함께_담을_수_있다() {
        IllegalStateException cause = new IllegalStateException("원인");

        DomainException exception = new TestDomainException("규칙 위반", cause);

        assertThat(exception).hasMessage("규칙 위반").hasCause(cause);
    }

    @Test
    void 비검사_예외이다() {
        assertThat(new TestDomainException("규칙 위반")).isInstanceOf(RuntimeException.class);
    }

    static class TestDomainException extends DomainException {

        TestDomainException(String message) {
            super(message);
        }

        TestDomainException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
