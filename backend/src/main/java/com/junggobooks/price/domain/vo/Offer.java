package com.junggobooks.price.domain.vo;

import com.junggobooks.price.domain.enums.Condition;
import com.junggobooks.price.domain.exception.InvalidOfferException;

public record Offer(
        int price, int shippingFee, Condition condition, String rawCondition, String url) {

    public Offer {
        requireNonNegative("가격", price);
        requireNonNegative("배송비", shippingFee);
        if (condition == null) {
            throw new InvalidOfferException("상태 등급은 비어 있을 수 없습니다.");
        }
        if (rawCondition == null || rawCondition.isBlank()) {
            throw new InvalidOfferException("원문 상태 라벨은 비어 있을 수 없습니다.");
        }
        if (url == null || url.isBlank()) {
            throw new InvalidOfferException("링크는 비어 있을 수 없습니다.");
        }
    }

    public int totalPrice() {
        return price + shippingFee;
    }

    private static void requireNonNegative(String field, int value) {
        if (value < 0) {
            throw new InvalidOfferException(field + "은(는) 0 이상이어야 합니다: " + value);
        }
    }
}
