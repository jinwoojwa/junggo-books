package com.junggobooks.price.domain;

import static com.junggobooks.price.fixture.OfferBuilder.anOffer;
import static com.junggobooks.price.fixture.StoreResultBuilder.aFound;
import static com.junggobooks.price.fixture.StoreResultBuilder.aNotFound;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.junggobooks.price.domain.enums.FetchStatus;
import com.junggobooks.price.domain.enums.Store;
import com.junggobooks.price.domain.exception.InvalidStoreResultException;
import com.junggobooks.price.domain.vo.Offer;
import com.junggobooks.price.domain.vo.StoreResult;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StoreResultTest {

    @Nested
    class Found_생성 {

        @Test
        void 매물이_하나_이상이면_생성된다() {
            Offer first = anOffer().price(3000).build();
            Offer second = anOffer().price(4000).build();

            StoreResult.Found found =
                    aFound().store(Store.YES24).offers(List.of(first, second)).build();

            assertThat(found.store()).isEqualTo(Store.YES24);
            assertThat(found.offers()).containsExactly(first, second);
        }

        @Test
        void 상태는_항상_OK이다() {
            StoreResult.Found found = aFound().build();

            assertThat(found.status()).isEqualTo(FetchStatus.OK);
        }

        @Test
        void 지연_시간이_0이어도_생성된다() {
            StoreResult.Found found = aFound().latency(Duration.ZERO).build();

            assertThat(found.latency()).isEqualTo(Duration.ZERO);
        }
    }

    @Nested
    class Found_입력_검증 {

        @Test
        void 매물이_비어_있으면_예외가_발생한다() {
            assertThatThrownBy(() -> aFound().offers(List.of()).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }

        @Test
        void 매물_목록이_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> aFound().offers(null).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }

        @Test
        void 서점이_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> aFound().store(null).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }

        @Test
        void 지연_시간이_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> aFound().latency(null).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }

        @Test
        void 지연_시간이_음수이면_예외가_발생한다() {
            assertThatThrownBy(() -> aFound().latency(Duration.ofMillis(-1)).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }
    }

    @Nested
    class Found_매물_목록 {

        @Test
        void 원본_목록을_바꿔도_결과에_반영되지_않는다() {
            List<Offer> source = new ArrayList<>(List.of(anOffer().build()));
            StoreResult.Found found = aFound().offers(source).build();

            source.add(anOffer().price(9000).build());

            assertThat(found.offers()).hasSize(1);
        }

        @Test
        void 돌려받은_목록은_수정할_수_없다() {
            StoreResult.Found found = aFound().build();

            assertThatThrownBy(() -> found.offers().add(anOffer().build()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class NotFound_생성 {

        @Test
        void EMPTY_상태로_생성된다() {
            StoreResult.NotFound notFound = aNotFound().status(FetchStatus.EMPTY).build();

            assertThat(notFound.status()).isEqualTo(FetchStatus.EMPTY);
        }

        @Test
        void TIMEOUT_상태로_생성된다() {
            StoreResult.NotFound notFound = aNotFound().status(FetchStatus.TIMEOUT).build();

            assertThat(notFound.status()).isEqualTo(FetchStatus.TIMEOUT);
        }

        @Test
        void FAILED_상태로_생성된다() {
            StoreResult.NotFound notFound = aNotFound().status(FetchStatus.FAILED).build();

            assertThat(notFound.status()).isEqualTo(FetchStatus.FAILED);
        }

        @Test
        void PARSE_ERROR_상태로_생성된다() {
            StoreResult.NotFound notFound = aNotFound().status(FetchStatus.PARSE_ERROR).build();

            assertThat(notFound.status()).isEqualTo(FetchStatus.PARSE_ERROR);
        }

        @Test
        void BLOCKED_상태로_생성된다() {
            StoreResult.NotFound notFound = aNotFound().status(FetchStatus.BLOCKED).build();

            assertThat(notFound.status()).isEqualTo(FetchStatus.BLOCKED);
        }

        @Test
        void 생성할_때_넘긴_상태를_그대로_돌려준다() {
            StoreResult.NotFound notFound =
                    aNotFound()
                            .store(Store.YES24)
                            .status(FetchStatus.FAILED)
                            .latency(Duration.ofSeconds(3))
                            .build();

            assertThat(notFound.store()).isEqualTo(Store.YES24);
            assertThat(notFound.status()).isEqualTo(FetchStatus.FAILED);
            assertThat(notFound.latency()).isEqualTo(Duration.ofSeconds(3));
        }
    }

    @Nested
    class NotFound_입력_검증 {

        @Test
        void 상태가_OK이면_예외가_발생한다() {
            assertThatThrownBy(() -> aNotFound().status(FetchStatus.OK).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }

        @Test
        void 상태가_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> aNotFound().status(null).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }

        @Test
        void 서점이_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> aNotFound().store(null).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }

        @Test
        void 지연_시간이_null이면_예외가_발생한다() {
            assertThatThrownBy(() -> aNotFound().latency(null).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }

        @Test
        void 지연_시간이_음수이면_예외가_발생한다() {
            assertThatThrownBy(() -> aNotFound().latency(Duration.ofMillis(-1)).build())
                    .isInstanceOf(InvalidStoreResultException.class);
        }
    }

    @Nested
    class 공통_접근 {

        @Test
        void Found와_NotFound_모두_서점과_지연_시간을_같은_방식으로_꺼낼_수_있다() {
            List<StoreResult> results =
                    List.of(
                            aFound().store(Store.ALADIN).latency(Duration.ofMillis(100)).build(),
                            aNotFound().store(Store.YES24).latency(Duration.ofMillis(200)).build());

            assertThat(results)
                    .extracting(StoreResult::store)
                    .containsExactly(Store.ALADIN, Store.YES24);
            assertThat(results)
                    .extracting(StoreResult::latency)
                    .containsExactly(Duration.ofMillis(100), Duration.ofMillis(200));
        }

        @Test
        void Found와_NotFound를_switch로_빠짐없이_구분할_수_있다() {
            // default 없이 컴파일되려면 StoreResult가 Found와 NotFound만 허용해야 한다
            assertThat(describe(aFound().build())).isEqualTo("found");
            assertThat(describe(aNotFound().build())).isEqualTo("notFound");
        }

        private String describe(StoreResult result) {
            return switch (result) {
                case StoreResult.Found found -> "found";
                case StoreResult.NotFound notFound -> "notFound";
            };
        }
    }
}
