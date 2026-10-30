package com.junggobooks.price.application.port.out;

import com.junggobooks.book.domain.vo.Isbn13;
import com.junggobooks.price.domain.enums.Store;
import com.junggobooks.price.domain.vo.Offer;
import java.util.List;

/**
 * 서점 한 곳의 중고 매물을 조회하는 포트. 서점마다 하나씩 구현한다(ADR-005).
 *
 * <p>어댑터는 조회 결과 상태를 만들지 않는다. 성공하면 매물 목록을, 실패하면 {@link StoreFetchException}을 던지고, 이를 조회 결과로 바꾸는 일은
 * 가격 비교 서비스가 맡는다. 시간 제한은 어댑터가 HTTP 클라이언트 타임아웃으로 걸고, 전체 상한은 서비스가 건다.
 */
public interface StoreAdapter {

    /** 이 어댑터가 조회하는 서점. 캐시 키와 결과 구분에 쓴다. */
    Store store();

    /**
     * ISBN13으로 이 서점의 중고 매물을 조회한다.
     *
     * @return 찾은 매물. 매물이 없으면 예외 대신 빈 목록이다.
     * @throws StoreFetchException 조회에 실패했을 때. 실패 종류는 {@link StoreFetchException#status()}에 담는다.
     */
    List<Offer> fetch(Isbn13 isbn13);
}
