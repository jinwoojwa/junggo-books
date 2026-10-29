package com.junggobooks.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** {@code app.cors.*} 설정. 허용 출처는 쉼표로 구분한 환경 변수(CORS_ALLOWED_ORIGINS)로 받는다. */
@ConfigurationProperties("app.cors")
public record CorsProperties(List<String> allowedOrigins) {

    public CorsProperties {
        allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
    }
}
