package com.junggobooks.price.application.port.out;

import com.junggobooks.price.domain.enums.FetchStatus;

/**
 * 서점 조회에 실패했을 때 {@link StoreAdapter}가 던지는 예외. 실패의 종류를 {@link FetchStatus}로 담고, 가격 비교 서비스가 이를 조회 결과로
 * 바꾼다.
 *
 * <p>매물이 없는 것은 실패가 아니라 정상 응답이므로 {@code OK}와 {@code EMPTY}는 담을 수 없다. 매물이 없으면 어댑터는 예외 대신 빈 목록을 돌려준다.
 */
public class StoreFetchException extends RuntimeException {

    private final FetchStatus status;

    public StoreFetchException(FetchStatus status, String message) {
        this(status, message, null);
    }

    public StoreFetchException(FetchStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = requireFailure(status);
    }

    public FetchStatus status() {
        return status;
    }

    private static FetchStatus requireFailure(FetchStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("조회 상태는 비어 있을 수 없습니다.");
        }
        if (status == FetchStatus.OK || status == FetchStatus.EMPTY) {
            throw new IllegalArgumentException("실패 상태만 담을 수 있습니다: " + status);
        }
        return status;
    }
}
