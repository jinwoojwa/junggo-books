package com.junggobooks.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** 빈 PostgreSQL에 Flyway 마이그레이션을 처음부터 끝까지 적용해 본다. 운영과 같은 메이저 버전(compose.yaml)을 쓰며, Docker가 필요하다. */
@Testcontainers
class MigrationTest {

    @Container static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @BeforeAll
    static void migrate() {
        Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

    @Test
    void 마이그레이션이_모두_적용되고_남은_것이_없다() {
        Flyway flyway =
                Flyway.configure()
                        .dataSource(
                                postgres.getJdbcUrl(),
                                postgres.getUsername(),
                                postgres.getPassword())
                        .locations("classpath:db/migration")
                        .load();

        assertThat(flyway.info().pending()).isEmpty();
        assertThat(flyway.info().applied()).isNotEmpty();
    }

    @Test
    void 설계된_테이블이_생성된다() throws SQLException {
        List<String> tables = new ArrayList<>();
        try (Connection c = connection();
                Statement s = c.createStatement();
                var rs =
                        s.executeQuery(
                                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'")) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }

        assertThat(tables).contains("book", "search_log", "store_fetch_log", "daily_lowest_price");
    }

    @Test
    void 허용되지_않는_서점_값은_CHECK_제약이_막는다() throws SQLException {
        try (Connection c = connection();
                Statement s = c.createStatement()) {
            s.executeUpdate(
                    "INSERT INTO book (isbn13, title, created_at, updated_at) "
                            + "VALUES ('9780000000001', '테스트', now(), now()) ON CONFLICT DO NOTHING");

            assertThatThrownBy(
                            () ->
                                    s.executeUpdate(
                                            "INSERT INTO daily_lowest_price (isbn13, price_date, store, lowest_total_price, updated_at) "
                                                    + "VALUES ('9780000000001', current_date, 'UNKNOWN_STORE', 1000, now())"))
                    .isInstanceOf(SQLException.class)
                    .hasMessageContaining("chk_daily_lowest_price_store");
        }
    }

    private static Connection connection() throws SQLException {
        return DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }
}
