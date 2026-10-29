-- 운영 지표용 로그. IP나 사용자 식별자는 저장하지 않는다. 90일 지나면 삭제한다.
CREATE TABLE search_log (
    id               BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    query            VARCHAR(100) NOT NULL,
    selected_isbn13  CHAR(13)     REFERENCES book (isbn13),
    result_count     INTEGER,
    searched_at      TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_search_log_searched_at ON search_log (searched_at);

CREATE TABLE store_fetch_log (
    id           BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn13       CHAR(13)    NOT NULL REFERENCES book (isbn13),
    store        VARCHAR(20) NOT NULL,
    status       VARCHAR(20) NOT NULL,
    offer_count  INTEGER,
    latency_ms   INTEGER,
    from_cache   BOOLEAN     NOT NULL,
    fetched_at   TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_store_fetch_log_store
        CHECK (store IN ('ALADIN', 'YES24')),
    CONSTRAINT chk_store_fetch_log_status
        CHECK (status IN ('OK', 'EMPTY', 'TIMEOUT', 'FAILED', 'PARSE_ERROR', 'BLOCKED'))
);

CREATE INDEX idx_store_fetch_log_store_fetched_at ON store_fetch_log (store, fetched_at);
CREATE INDEX idx_store_fetch_log_fetched_at ON store_fetch_log (fetched_at);
