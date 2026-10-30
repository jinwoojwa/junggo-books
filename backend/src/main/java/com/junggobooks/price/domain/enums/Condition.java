package com.junggobooks.price.domain.enums;

/** 중고 매물의 상태 등급. 서점별 표기는 각 서점 파서가 이 등급으로 바꾼다. 상수 이름은 DB CHECK 제약(`condition`)과 같다. */
public enum Condition {
    /** 최상. 새 상품에 가까움. */
    BEST,
    /** 상. 사용 흔적이 약간 있으나 깨끗함. */
    GOOD,
    /** 중. 사용 흔적이 많음. */
    FAIR,
    /** 하. 사용 흔적이 많고 손상이 있음. YES24에만 있다. */
    POOR,
    /** 서점 표기를 등급으로 바꾸지 못함. */
    UNKNOWN
}
