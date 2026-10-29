package com.junggobooks.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringJUnitWebConfig(CorsConfigTest.TestConfig.class)
@TestPropertySource(
        properties = "app.cors.allowed-origins=https://a.example.com,https://b.example.com")
class CorsConfigTest {

    private static final String PREFLIGHT_METHOD = "Access-Control-Request-Method";

    private final MockMvc mockMvc;

    @Autowired
    CorsConfigTest(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void 허용한_출처의_요청에는_CORS_헤더를_붙인다() throws Exception {
        mockMvc.perform(get("/api/ping").header("Origin", "https://b.example.com"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://b.example.com"));
    }

    @Test
    void 허용하지_않은_출처는_거부한다() throws Exception {
        mockMvc.perform(get("/api/ping").header("Origin", "https://evil.example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void GET_preflight는_허용한다() throws Exception {
        mockMvc.perform(
                        options("/api/ping")
                                .header("Origin", "https://a.example.com")
                                .header(PREFLIGHT_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://a.example.com"));
    }

    @Test
    void 쓰기_메서드_preflight는_거부한다() throws Exception {
        mockMvc.perform(
                        options("/api/ping")
                                .header("Origin", "https://a.example.com")
                                .header(PREFLIGHT_METHOD, "DELETE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void api_밖의_경로에는_CORS를_적용하지_않는다() throws Exception {
        mockMvc.perform(get("/other").header("Origin", "https://a.example.com"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Configuration
    @EnableWebMvc
    @Import({CorsConfig.class, PingController.class})
    static class TestConfig {}

    @RestController
    static class PingController {

        @GetMapping("/api/ping")
        String ping() {
            return "pong";
        }

        @GetMapping("/other")
        String other() {
            return "other";
        }
    }
}
