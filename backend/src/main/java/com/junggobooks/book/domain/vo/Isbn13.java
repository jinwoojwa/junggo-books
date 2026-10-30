package com.junggobooks.book.domain.vo;

import java.util.Set;
import java.util.regex.Pattern;

public record Isbn13(String value) {

    private static final String ISBN13_PREFIX = "978";
    private static final Set<String> VALID_PREFIXES = Set.of("978", "979");
    private static final Pattern ISBN10_PATTERN = Pattern.compile("\\d{9}[\\dX]");
    private static final Pattern ISBN13_PATTERN = Pattern.compile("\\d{13}");

    public Isbn13 {
        value = normalize(value);
        if (value.length() == 10) {
            validateIsbn10(value);
            value = fromIsbn10(value);
        } else {
            validateIsbn13(value);
        }
    }

    public static Isbn13 of(String value) {
        return new Isbn13(value);
    }

    private static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("ISBN은 비어 있을 수 없습니다.");
        }
        return raw.replaceAll("[-\\s]", "").toUpperCase();
    }

    private static void validateIsbn10(String isbn10) {
        if (!ISBN10_PATTERN.matcher(isbn10).matches()) {
            throw invalid(isbn10);
        }
        int sum = 0;
        for (int i = 0; i < isbn10.length(); ++i) {
            char c = isbn10.charAt(i);
            int digit = c == 'X' ? 10 : c - '0';
            sum += (10 - i) * digit;
        }
        if (sum % 11 != 0) {
            throw invalid(isbn10);
        }
    }

    private static void validateIsbn13(String isbn13) {
        if (!ISBN13_PATTERN.matcher(isbn13).matches()
                || !VALID_PREFIXES.contains(isbn13.substring(0, 3))
                || checkDigit(isbn13.substring(0, 12)) != isbn13.charAt(12) - '0') {
            throw invalid(isbn13);
        }
    }

    private static String fromIsbn10(String isbn10) {
        String body = ISBN13_PREFIX + isbn10.substring(0, 9);
        return body + checkDigit(body);
    }

    private static int checkDigit(String body) {
        int sum = 0;
        for (int i = 0; i < body.length(); ++i) {
            int weight = (i % 2 == 0) ? 1 : 3;
            sum += (body.charAt(i) - '0') * weight;
        }
        return (10 - sum % 10) % 10;
    }

    private static IllegalArgumentException invalid(String isbn) {
        return new IllegalArgumentException("유효하지 않은 ISBN입니다: " + isbn);
    }
}
