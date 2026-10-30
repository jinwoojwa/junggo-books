package com.junggobooks.price.domain.exception;

import com.junggobooks.common.domain.DomainException;

/** 서점 조회 결과(StoreResult)의 구성이 규칙에 맞지 않는다. 매물이 비어 있는 Found나 OK 상태의 NotFound 같은 경우다. */
public class InvalidStoreResultException extends DomainException {

    public InvalidStoreResultException(String message) {
        super(message);
    }
}
