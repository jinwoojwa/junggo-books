package com.junggobooks.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.env.MockEnvironment;

/**
 * 설정 파일이 문법적으로 로딩되는지 확인한다. 컨텍스트나 DB 없이 돌아서 빠르고, 기동 전에 설정 오류를 잡는다.
 * (docs/troubleshooting/001-yaml-colon-in-default-value.md)
 */
class ApplicationConfigTest {

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @ParameterizedTest
    @ValueSource(strings = {"application.yml", "application-local.yml", "application-prod.yml"})
    void 설정_파일이_YAML로_로딩된다(String file) throws IOException {
        List<PropertySource<?>> sources = loader.load(file, new ClassPathResource(file));

        assertThat(sources).isNotEmpty();
    }

    @Test
    void 콜론이_들어간_기본값도_문자열로_로딩된다() throws IOException {
        Object value = property("application.yml", "app.crawler.user-agent");

        // 로더는 값을 OriginTrackedValue로 감싸 반환하므로 문자열로 바꿔 비교한다
        assertThat(value).isNotNull();
        assertThat(value.toString()).contains("contact: ");
    }

    @Test
    void prod는_필수_환경_변수가_없으면_설정_해석에_실패한다() throws IOException {
        MockEnvironment env = prodEnvironment();

        assertThatThrownBy(() -> env.getProperty("app.cors.allowed-origins"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> env.getProperty("app.crawler.user-agent"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> env.getProperty("spring.datasource.password"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void prod는_환경_변수가_있으면_그_값을_쓴다() throws IOException {
        MockEnvironment env =
                prodEnvironment()
                        .withProperty("CORS_ALLOWED_ORIGINS", "https://a.example.com")
                        .withProperty(
                                "CRAWLER_USER_AGENT", "junggo-books (contact: me@example.com)")
                        .withProperty("DB_PASSWORD", "secret");

        assertThat(env.getProperty("app.cors.allowed-origins")).isEqualTo("https://a.example.com");
        assertThat(env.getProperty("app.crawler.user-agent")).contains("contact: me@example.com");
        assertThat(env.getProperty("spring.datasource.password")).isEqualTo("secret");
    }

    /** 시스템 환경 변수가 섞이지 않도록 MockEnvironment에 prod 우선으로 YAML을 올린다. */
    private MockEnvironment prodEnvironment() throws IOException {
        MockEnvironment env = new MockEnvironment();
        for (String file : List.of("application-prod.yml", "application.yml")) {
            for (PropertySource<?> source : loader.load(file, new ClassPathResource(file))) {
                env.getPropertySources().addLast(source);
            }
        }
        return env;
    }

    private Object property(String file, String key) throws IOException {
        PropertySource<?> source = loader.load(file, new ClassPathResource(file)).get(0);
        return ((Map<?, ?>) source.getSource()).get(key);
    }
}
