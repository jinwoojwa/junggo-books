package com.junggobooks.common.error;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 모든 예외를 RFC 9457 {@link ProblemDetail}로 바꾼다. 응답에는 항상 {@code code} 확장 필드가 있다.
 *
 * <p>스프링 MVC가 던지는 표준 예외는 부모 클래스가 처리하고, 여기서는 {@code code}만 채운다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    ProblemDetail handleBusiness(BusinessException e) {
        ErrorCode errorCode = e.errorCode();
        if (toStatus(errorCode.type()).is5xxServerError()) {
            log.error("업무 예외: {}", errorCode.code(), e);
        }
        return problem(errorCode, e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException e) {
        List<Map<String, String>> errors =
                e.getConstraintViolations().stream()
                        .map(
                                v ->
                                        Map.of(
                                                "field", v.getPropertyPath().toString(),
                                                "message", v.getMessage()))
                        .toList();
        ProblemDetail problem = problem(CommonErrorCode.INVALID_REQUEST, null);
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception e) {
        log.error("처리되지 않은 예외", e);
        return problem(CommonErrorCode.INTERNAL_ERROR, null);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<Map<String, String>> errors =
                e.getBindingResult().getFieldErrors().stream()
                        .map(GlobalExceptionHandler::toError)
                        .toList();
        ProblemDetail problem = problem(CommonErrorCode.INVALID_REQUEST, null);
        problem.setProperty("errors", errors);
        return ResponseEntity.status(status).headers(headers).body(problem);
    }

    /** 부모가 만든 표준 예외 응답(파라미터 누락, 본문 파싱 실패 등)에 code를 채운다. */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        ResponseEntity<Object> response =
                super.handleExceptionInternal(e, body, headers, statusCode, request);
        if (response != null && response.getBody() instanceof ProblemDetail problem) {
            problem.setProperty("code", codeFor(statusCode).code());
        }
        return response;
    }

    private static ProblemDetail problem(ErrorCode errorCode, String detail) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        toStatus(errorCode.type()),
                        detail != null ? detail : errorCode.defaultMessage());
        problem.setProperty("code", errorCode.code());
        return problem;
    }

    /** 에러 종류를 HTTP 상태로 바꾸는 유일한 곳. */
    static HttpStatus toStatus(ErrorType type) {
        return switch (type) {
            case VALIDATION -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED;
            case UNSUPPORTED_MEDIA_TYPE -> HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            case TOO_MANY_REQUESTS -> HttpStatus.TOO_MANY_REQUESTS;
            case UPSTREAM -> HttpStatus.BAD_GATEWAY;
            case INTERNAL -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private static Map<String, String> toError(FieldError error) {
        return Map.of(
                "field",
                error.getField(),
                "message",
                error.getDefaultMessage() != null ? error.getDefaultMessage() : "");
    }

    private static ErrorCode codeFor(HttpStatusCode status) {
        if (status.value() == HttpStatus.NOT_FOUND.value()) {
            return CommonErrorCode.NOT_FOUND;
        }
        if (status.value() == HttpStatus.METHOD_NOT_ALLOWED.value()) {
            return CommonErrorCode.METHOD_NOT_ALLOWED;
        }
        if (status.value() == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
            return CommonErrorCode.UNSUPPORTED_MEDIA_TYPE;
        }
        return status.is4xxClientError()
                ? CommonErrorCode.INVALID_REQUEST
                : CommonErrorCode.INTERNAL_ERROR;
    }
}
