package com.junggobooks.price.application.port.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.junggobooks.price.domain.enums.FetchStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StoreFetchExceptionTest {

    @Nested
    class 생성 {

        @Test
        void TIMEOUT_상태로_생성된다() {
            StoreFetchException exception = new StoreFetchException(FetchStatus.TIMEOUT, "응답 지연");

            assertThat(exception.status()).isEqualTo(FetchStatus.TIMEOUT);
        }

        @Test
        void FAILED_상태로_생성된다() {
            StoreFetchException exception = new StoreFetchException(FetchStatus.FAILED, "요청 실패");

            assertThat(exception.status()).isEqualTo(FetchStatus.FAILED);
        }

        @Test
        void PARSE_ERROR_상태로_생성된다() {
            StoreFetchException exception =
                    new StoreFetchException(FetchStatus.PARSE_ERROR, "매물 영역 없음");

            assertThat(exception.status()).isEqualTo(FetchStatus.PARSE_ERROR);
        }

        @Test
        void BLOCKED_상태로_생성된다() {
            StoreFetchException exception = new StoreFetchException(FetchStatus.BLOCKED, "차단됨");

            assertThat(exception.status()).isEqualTo(FetchStatus.BLOCKED);
        }

        @Test
        void 생성할_때_넘긴_상태를_그대로_돌려준다() {
            StoreFetchException exception = new StoreFetchException(FetchStatus.FAILED, "요청 실패");

            assertThat(exception.status()).isEqualTo(FetchStatus.FAILED);
            assertThat(exception).hasMessage("요청 실패");
        }
    }

    @Nested
    class 원인_예외 {

        @Test
        void 원인_예외를_함께_담을_수_있다() {
            IllegalStateException cause = new IllegalStateException("연결 끊김");

            StoreFetchException exception =
                    new StoreFetchException(FetchStatus.FAILED, "요청 실패", cause);

            assertThat(exception).hasCause(cause);
        }

        @Test
        void 원인_예외가_없어도_생성된다() {
            StoreFetchException exception = new StoreFetchException(FetchStatus.FAILED, "요청 실패");

            assertThat(exception).hasNoCause();
        }
    }

    @Nested
    class 입력_검증 {

        @Test
        void 상태가_OK이면_예외가_발생한다() {
            assertThatThrownBy(() -> new StoreFetchException(FetchStatus.OK, "요청 실패"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 상태가_EMPTY이면_예외가_발생한다() {
            assertThatThrownBy(() -> new StoreFetchException(FetchStatus.EMPTY, "요청 실패"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 상태가_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> new StoreFetchException(null, "요청 실패"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
