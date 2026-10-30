package com.junggobooks.price.fixture;

import static com.junggobooks.price.fixture.OfferBuilder.anOffer;

import com.junggobooks.price.domain.enums.FetchStatus;
import com.junggobooks.price.domain.enums.Store;
import com.junggobooks.price.domain.vo.Offer;
import com.junggobooks.price.domain.vo.StoreResult;
import java.time.Duration;
import java.util.List;

/**
 * 테스트용 {@link StoreResult} 빌더. {@link #aFound()}와 {@link #aNotFound()}로 각 경우의 빌더를 얻는다. 기본값은 유효한 결과라
 * 필요한 필드만 바꿔 쓴다.
 */
public final class StoreResultBuilder {

    private static final Store DEFAULT_STORE = Store.ALADIN;
    private static final Duration DEFAULT_LATENCY = Duration.ofMillis(300);

    private StoreResultBuilder() {}

    public static FoundBuilder aFound() {
        return new FoundBuilder();
    }

    public static NotFoundBuilder aNotFound() {
        return new NotFoundBuilder();
    }

    public static final class FoundBuilder {

        private Store store = DEFAULT_STORE;
        private List<Offer> offers = List.of(anOffer().build());
        private Duration latency = DEFAULT_LATENCY;

        private FoundBuilder() {}

        public FoundBuilder store(Store store) {
            this.store = store;
            return this;
        }

        public FoundBuilder offers(List<Offer> offers) {
            this.offers = offers;
            return this;
        }

        public FoundBuilder latency(Duration latency) {
            this.latency = latency;
            return this;
        }

        public StoreResult.Found build() {
            return new StoreResult.Found(store, offers, latency);
        }
    }

    public static final class NotFoundBuilder {

        private Store store = DEFAULT_STORE;
        private FetchStatus status = FetchStatus.EMPTY;
        private Duration latency = DEFAULT_LATENCY;

        private NotFoundBuilder() {}

        public NotFoundBuilder store(Store store) {
            this.store = store;
            return this;
        }

        public NotFoundBuilder status(FetchStatus status) {
            this.status = status;
            return this;
        }

        public NotFoundBuilder latency(Duration latency) {
            this.latency = latency;
            return this;
        }

        public StoreResult.NotFound build() {
            return new StoreResult.NotFound(store, status, latency);
        }
    }
}
