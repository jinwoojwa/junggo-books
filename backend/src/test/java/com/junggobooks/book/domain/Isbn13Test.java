package com.junggobooks.book.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.junggobooks.book.domain.exception.InvalidIsbnException;
import com.junggobooks.book.domain.vo.Isbn13;
import com.junggobooks.common.domain.DomainException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class Isbn13Test {

    @Nested
    class ISBN13_입력 {

        @Test
        void 유효한_ISBN13이면_생성된다() {
            Isbn13 isbn13 = Isbn13.of("9780306406157");

            assertThat(isbn13.value()).isEqualTo("9780306406157");
        }

        @Test
        void 접두어가_979인_ISBN13도_생성된다() {
            Isbn13 isbn13 = Isbn13.of("9791162540640");

            assertThat(isbn13.value()).isEqualTo("9791162540640");
        }

        @Test
        void 체크_디지트가_0인_ISBN13도_생성된다() {
            Isbn13 isbn13 = Isbn13.of("9788968481260");

            assertThat(isbn13.value()).isEqualTo("9788968481260");
        }
    }

    @Nested
    class ISBN10_입력 {

        @Test
        void 유효한_ISBN10이면_ISBN13으로_변환된다() {
            Isbn13 isbn13 = Isbn13.of("0306406152");

            assertThat(isbn13.value()).isEqualTo("9780306406157");
        }

        @Test
        void 체크_디지트가_X인_ISBN10도_변환된다() {
            Isbn13 isbn13 = Isbn13.of("043942089X");

            assertThat(isbn13.value()).isEqualTo("9780439420891");
        }

        @Test
        void 변환된_ISBN13의_체크_디지트를_다시_계산한다() {
            // ISBN10의 체크 디지트(7)를 그대로 쓰지 않고 ISBN13 방식(9)으로 다시 계산한다
            Isbn13 isbn13 = Isbn13.of("8968481237");

            assertThat(isbn13.value()).isEqualTo("9788968481239");
        }
    }

    @Nested
    class 정규화 {

        @Test
        void 하이픈이_섞인_입력의_하이픈을_제거한다() {
            Isbn13 isbn13 = Isbn13.of("978-0-306-40615-7");

            assertThat(isbn13.value()).isEqualTo("9780306406157");
        }

        @Test
        void 공백이_섞인_입력의_공백을_제거한다() {
            Isbn13 isbn13 = Isbn13.of(" 978 0306 406157 ");

            assertThat(isbn13.value()).isEqualTo("9780306406157");
        }

        @Test
        void 소문자_x로_끝나는_ISBN10도_허용한다() {
            Isbn13 isbn13 = Isbn13.of("043942089x");

            assertThat(isbn13.value()).isEqualTo("9780439420891");
        }

        @Test
        void 하이픈이_있는_ISBN10도_ISBN13으로_변환된다() {
            Isbn13 isbn13 = Isbn13.of("0-306-40615-2");

            assertThat(isbn13.value()).isEqualTo("9780306406157");
        }
    }

    @Nested
    class 잘못된_입력 {

        @Test
        void 던지는_예외는_도메인_예외이다() {
            assertThatThrownBy(() -> Isbn13.of("1234567890123"))
                    .isInstanceOf(DomainException.class)
                    .isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void null이면_예외가_발생한다() {
            assertThatThrownBy(() -> Isbn13.of(null)).isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void 빈_문자열이면_예외가_발생한다() {
            assertThatThrownBy(() -> Isbn13.of("")).isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void 공백만_있으면_예외가_발생한다() {
            assertThatThrownBy(() -> Isbn13.of("   ")).isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void 길이가_10도_13도_아니면_예외가_발생한다() {
            assertThatThrownBy(() -> Isbn13.of("97803064"))
                    .isInstanceOf(InvalidIsbnException.class);
            assertThatThrownBy(() -> Isbn13.of("97803064061570"))
                    .isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void 숫자가_아닌_문자가_섞여_있으면_예외가_발생한다() {
            assertThatThrownBy(() -> Isbn13.of("97803064061a7"))
                    .isInstanceOf(InvalidIsbnException.class);
            assertThatThrownBy(() -> Isbn13.of("03064a6152"))
                    .isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void ISBN13의_체크_디지트가_틀리면_예외가_발생한다() {
            assertThatThrownBy(() -> Isbn13.of("9780306406158"))
                    .isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void ISBN10의_체크_디지트가_틀리면_예외가_발생한다() {
            assertThatThrownBy(() -> Isbn13.of("0306406159"))
                    .isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void ISBN13의_접두어가_978이나_979가_아니면_예외가_발생한다() {
            // 체크 디지트는 맞지만 접두어만 977인 값
            assertThatThrownBy(() -> Isbn13.of("9771234567898"))
                    .isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void ISBN13의_마지막_자리가_X이면_예외가_발생한다() {
            assertThatThrownBy(() -> Isbn13.of("978030640615X"))
                    .isInstanceOf(InvalidIsbnException.class);
        }

        @Test
        void ISBN10의_X가_마지막_자리가_아닌_곳에_있으면_예외가_발생한다() {
            assertThatThrownBy(() -> Isbn13.of("0306X06152"))
                    .isInstanceOf(InvalidIsbnException.class);
        }
    }

    @Nested
    class 값_객체 {

        @Test
        void 같은_값이면_동등하다() {
            assertThat(Isbn13.of("9780306406157")).isEqualTo(Isbn13.of("9780306406157"));
        }

        @Test
        void ISBN10과_변환된_ISBN13은_동등하다() {
            assertThat(Isbn13.of("0306406152")).isEqualTo(Isbn13.of("9780306406157"));
        }

        @Test
        void 문자열_값은_하이픈_없는_13자리다() {
            Isbn13 isbn13 = Isbn13.of("978-0-306-40615-7");

            assertThat(isbn13.value()).hasSize(13).containsOnlyDigits();
        }
    }
}
