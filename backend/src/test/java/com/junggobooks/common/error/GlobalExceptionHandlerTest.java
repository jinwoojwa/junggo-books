package com.junggobooks.common.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 스프링 컨텍스트 없이 컨트롤러와 어드바이스만 묶어 에러 응답 규약을 확인한다. */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new ThrowingController())
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
    }

    @Test
    void 업무_예외는_에러_코드와_상태로_변환된다() throws Exception {
        mockMvc.perform(get("/business"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("책을 찾을 수 없습니다."));
    }

    @Test
    void 업무_예외에_메시지가_없으면_기본_메시지를_쓴다() throws Exception {
        mockMvc.perform(get("/business-default"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"))
                .andExpect(jsonPath("$.detail").value("테스트용 기본 메시지"));
    }

    @Test
    void 본문_검증_실패는_필드별_오류를_담아_400을_반환한다() throws Exception {
        mockMvc.perform(
                        post("/body")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.errors[0].field").value("title"));
    }

    @Test
    void 필수_파라미터_누락은_400_INVALID_REQUEST다() throws Exception {
        mockMvc.perform(get("/param"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void 지원하지_않는_메서드는_405_METHOD_NOT_ALLOWED다() throws Exception {
        mockMvc.perform(post("/business"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    void 없는_경로는_404_NOT_FOUND다() throws Exception {
        mockMvc.perform(get("/nowhere"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void 예상하지_못한_예외는_내부_정보_없이_500을_반환한다() throws Exception {
        mockMvc.perform(get("/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.detail").value("서버 오류가 발생했습니다."));
    }

    @ParameterizedTest
    @CsvSource({
        "VALIDATION, 400",
        "NOT_FOUND, 404",
        "METHOD_NOT_ALLOWED, 405",
        "UNSUPPORTED_MEDIA_TYPE, 415",
        "TOO_MANY_REQUESTS, 429",
        "UPSTREAM, 502",
        "INTERNAL, 500"
    })
    void 에러_종류는_HTTP_상태로_변환된다(ErrorType type, int status) {
        assertThat(GlobalExceptionHandler.toStatus(type).value()).isEqualTo(status);
    }

    enum TestErrorCode implements ErrorCode {
        BOOK_NOT_FOUND;

        @Override
        public String code() {
            return name();
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }

        @Override
        public String defaultMessage() {
            return "테스트용 기본 메시지";
        }
    }

    record BookRequest(@NotBlank String title) {}

    @RestController
    static class ThrowingController {

        @GetMapping("/business")
        void business() {
            throw new BusinessException(TestErrorCode.BOOK_NOT_FOUND, "책을 찾을 수 없습니다.");
        }

        @GetMapping("/business-default")
        void businessDefault() {
            throw new BusinessException(TestErrorCode.BOOK_NOT_FOUND);
        }

        @PostMapping("/body")
        void body(@Valid @RequestBody BookRequest request) {}

        @GetMapping("/param")
        void param(@RequestParam String isbn) {}

        @GetMapping("/unexpected")
        void unexpected() {
            throw new IllegalStateException("내부 상세 정보");
        }
    }
}
