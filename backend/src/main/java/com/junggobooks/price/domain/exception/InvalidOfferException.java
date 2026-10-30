package com.junggobooks.price.domain.exception;

import com.junggobooks.common.domain.DomainException;

/** 매물(Offer)의 값이 규칙에 맞지 않는다. 가격이나 배송비가 음수이거나 필수 값이 비어 있는 경우다. */
public class InvalidOfferException extends DomainException {

    public InvalidOfferException(String message) {
        super(message);
    }
}
