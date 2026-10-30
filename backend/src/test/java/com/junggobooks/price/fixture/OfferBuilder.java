package com.junggobooks.price.fixture;

import com.junggobooks.price.domain.enums.Condition;
import com.junggobooks.price.domain.vo.Offer;

/** 테스트용 {@link Offer} 빌더. 기본값은 유효한 매물이라 필요한 필드만 바꿔 쓴다. */
public class OfferBuilder {

    private int price = 5000;
    private int shippingFee = 2500;
    private Condition condition = Condition.GOOD;
    private String rawCondition = "상";
    private String url = "https://example.com/offers/1";

    private OfferBuilder() {}

    public static OfferBuilder anOffer() {
        return new OfferBuilder();
    }

    public OfferBuilder price(int price) {
        this.price = price;
        return this;
    }

    public OfferBuilder shippingFee(int shippingFee) {
        this.shippingFee = shippingFee;
        return this;
    }

    public OfferBuilder condition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public OfferBuilder rawCondition(String rawCondition) {
        this.rawCondition = rawCondition;
        return this;
    }

    public OfferBuilder url(String url) {
        this.url = url;
        return this;
    }

    public Offer build() {
        return new Offer(price, shippingFee, condition, rawCondition, url);
    }
}
