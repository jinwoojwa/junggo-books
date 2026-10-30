package com.junggobooks.book.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.junggobooks.common.error.ErrorType;
import org.junit.jupiter.api.Test;

/** 코드 문자열과 종류는 클라이언트와의 계약이라, 실수로 바뀌면 빌드에서 잡는다. */
class BookErrorCodeTest {

    @Test
    void INVALID_ISBN은_검증_오류다() {
        assertThat(BookErrorCode.INVALID_ISBN.code()).isEqualTo("INVALID_ISBN");
        assertThat(BookErrorCode.INVALID_ISBN.type()).isEqualTo(ErrorType.VALIDATION);
        assertThat(BookErrorCode.INVALID_ISBN.defaultMessage()).isEqualTo("유효하지 않은 ISBN입니다.");
    }

    @Test
    void BOOK_NOT_FOUND는_없음_오류다() {
        assertThat(BookErrorCode.BOOK_NOT_FOUND.code()).isEqualTo("BOOK_NOT_FOUND");
        assertThat(BookErrorCode.BOOK_NOT_FOUND.type()).isEqualTo(ErrorType.NOT_FOUND);
        assertThat(BookErrorCode.BOOK_NOT_FOUND.defaultMessage()).isEqualTo("책을 찾을 수 없습니다.");
    }
}
