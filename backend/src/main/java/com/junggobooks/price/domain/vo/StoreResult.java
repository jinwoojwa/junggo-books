package com.junggobooks.price.domain.vo;

import com.junggobooks.price.domain.enums.FetchStatus;
import com.junggobooks.price.domain.enums.Store;
import com.junggobooks.price.domain.exception.InvalidStoreResultException;
import java.time.Duration;
import java.util.List;

/**
 * 서점 한 곳을 한 번 조회한 결과. 매물은 조회가 성공했을 때({@link Found})만 존재하고, 매물이 없거나 실패한 경우는 {@link NotFound}가 상태와 함께
 * 담는다.
 */
public sealed interface StoreResult {

    Store store();

    FetchStatus status();

    Duration latency();

    /** 매물을 하나 이상 찾았다. 상태는 항상 {@link FetchStatus#OK}다. */
    record Found(Store store, List<Offer> offers, Duration latency) implements StoreResult {

        public Found {
            requireCommon(store, latency);
            if (offers == null || offers.isEmpty()) {
                throw new InvalidStoreResultException("매물은 하나 이상이어야 합니다.");
            }
            offers = List.copyOf(offers);
        }

        @Override
        public FetchStatus status() {
            return FetchStatus.OK;
        }
    }

    /** 매물이 없다. 정상 응답이지만 비어 있거나({@code EMPTY}) 조회에 실패한 경우다. */
    record NotFound(Store store, FetchStatus status, Duration latency) implements StoreResult {

        public NotFound {
            requireCommon(store, latency);
            if (status == null) {
                throw new InvalidStoreResultException("조회 상태는 비어 있을 수 없습니다.");
            }
            if (status == FetchStatus.OK) {
                throw new InvalidStoreResultException("OK 상태는 매물이 있는 Found만 가질 수 있습니다.");
            }
        }
    }

    private static void requireCommon(Store store, Duration latency) {
        if (store == null) {
            throw new InvalidStoreResultException("서점은 비어 있을 수 없습니다.");
        }
        if (latency == null) {
            throw new InvalidStoreResultException("지연 시간은 비어 있을 수 없습니다.");
        }
        if (latency.isNegative()) {
            throw new InvalidStoreResultException("지연 시간은 0 이상이어야 합니다: " + latency);
        }
    }
}
