package com.junggobooks.price.domain.enums;

/** 서점 조회 한 번의 결과 상태. 매물의 속성이 아니며, 상수 이름은 DB CHECK 제약(`status`)과 같다. */
public enum FetchStatus {
    /** 매물을 하나 이상 찾았다. */
    OK,
    /** 정상 응답이지만 매물이 없다. */
    EMPTY,
    TIMEOUT,
    FAILED,
    /** 페이지 구조가 바뀌어 매물 영역을 읽지 못했다. */
    PARSE_ERROR,
    /** 서점이 요청을 차단했다. */
    BLOCKED
}
