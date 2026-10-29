-- 사용자가 가격 비교 화면에서 연 책만 저장한다 (검색 결과 전체는 저장하지 않음).
-- updated_at이 30일 지나면 조회 시 도서 API로 갱신한다.
CREATE TABLE book (
    isbn13          CHAR(13)     PRIMARY KEY,
    title           VARCHAR(300) NOT NULL,
    authors         VARCHAR(300),
    publisher       VARCHAR(100),
    published_date  DATE,
    cover_url       VARCHAR(500),
    list_price      INTEGER,
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL
);
