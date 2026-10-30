package com.junggobooks.price.domain;

import static com.junggobooks.price.fixture.OfferBuilder.anOffer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.junggobooks.price.domain.enums.Condition;
import com.junggobooks.price.domain.exception.InvalidOfferException;
import com.junggobooks.price.domain.vo.Offer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class OfferTest {

    @Nested
    class 생성 {

        @Test
        void 유효한_값이면_생성된다() {
            Offer offer =
                    anOffer()
                            .price(5000)
                            .shippingFee(2500)
                            .condition(Condition.GOOD)
                            .rawCondition("상")
                            .url("https://example.com/offers/1")
                            .build();

            assertThat(offer.price()).isEqualTo(5000);
            assertThat(offer.shippingFee()).isEqualTo(2500);
            assertThat(offer.condition()).isEqualTo(Condition.GOOD);
            assertThat(offer.rawCondition()).isEqualTo("상");
            assertThat(offer.url()).isEqualTo("https://example.com/offers/1");
        }

        @Test
        void 배송비가_0이어도_생성된다() {
            Offer offer = anOffer().shippingFee(0).build();

            assertThat(offer.shippingFee()).isZero();
        }

        @Test
        void 가격이_0이어도_생성된다() {
            Offer offer = anOffer().price(0).build();

            assertThat(offer.price()).isZero();
        }

        @Test
        void 상태가_UNKNOWN이어도_생성된다() {
            Offer offer = anOffer().condition(Condition.UNKNOWN).rawCondition("A급").build();

            assertThat(offer.condition()).isEqualTo(Condition.UNKNOWN);
            assertThat(offer.rawCondition()).isEqualTo("A급");
        }
    }

    @Nested
    class 총액 {

        @Test
        void 가격과_배송비의_합이다() {
            Offer offer = anOffer().price(3000).shippingFee(2500).build();

            assertThat(offer.totalPrice()).isEqualTo(5500);
        }

        @Test
        void 배송비가_0이면_가격과_같다() {
            Offer offer = anOffer().price(3000).shippingFee(0).build();

            assertThat(offer.totalPrice()).isEqualTo(3000);
        }
    }

    @Nested
    class 입력_검증 {

        @ParameterizedTest
        @ValueSource(ints = {-1, -150000, -2500})
        void 가격이_음수이면_예외가_발생한다(int invalidPrice) {
            assertThatThrownBy(() -> anOffer().price(invalidPrice).build())
                    .isInstanceOf(InvalidOfferException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -2500, -5000})
        void 배송비가_음수이면_예외가_발생한다(int invalidShippingFee) {
            assertThatThrownBy(() -> anOffer().shippingFee(invalidShippingFee).build())
                    .isInstanceOf(InvalidOfferException.class);
        }

        @Test
        void 상태가_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> anOffer().condition(null).build())
                    .isInstanceOf(InvalidOfferException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t"})
        void 원문_상태_라벨이_비어_있으면_예외가_발생한다(String invalidValue) {
            assertThatThrownBy(() -> anOffer().rawCondition(invalidValue).build())
                    .isInstanceOf(InvalidOfferException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t"})
        void 링크가_비어_있으면_예외가_발생한다(String invalidUrl) {
            assertThatThrownBy(() -> anOffer().url(invalidUrl).build())
                    .isInstanceOf(InvalidOfferException.class);
        }
    }
}
