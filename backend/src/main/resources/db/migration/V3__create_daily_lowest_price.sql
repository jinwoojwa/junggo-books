-- 책·날짜(KST)·서점당 그날 관측된 최저 총액 한 줄.
-- 서점 조회가 OK일 때 upsert하며, 기존 값보다 낮을 때만 갱신한다.
CREATE TABLE daily_lowest_price (
    isbn13              CHAR(13)    NOT NULL REFERENCES book (isbn13),
    price_date          DATE        NOT NULL,
    store               VARCHAR(20) NOT NULL,
    lowest_total_price  INTEGER     NOT NULL,
    channel             VARCHAR(30),
    condition           VARCHAR(10),
    updated_at          TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (isbn13, price_date, store),
    CONSTRAINT chk_daily_lowest_price_store
        CHECK (store IN ('ALADIN', 'YES24')),
    CONSTRAINT chk_daily_lowest_price_condition
        CHECK (condition IN ('BEST', 'GOOD', 'FAIR', 'POOR', 'UNKNOWN'))
);
