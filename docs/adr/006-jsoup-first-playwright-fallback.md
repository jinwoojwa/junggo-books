# ADR-006 HTML 파싱은 Jsoup 우선, Playwright는 예외적으로만

- 상태: 채택
- 날짜: 2026-10-29

## 맥락

헤드리스 브라우저는 느리고 메모리를 많이 쓴다. 반면 일부 페이지는 자바스크립트 렌더링이 필요할 수 있다.

## 결정

기본은 HTTP 요청 + Jsoup 파싱. 정적 HTML로 가격을 얻을 수 없는 서점만 Playwright for Java를 쓴다.

## 대안

- 모든 서점에 Playwright 사용

## 결과

대부분의 조회가 가볍다. Playwright가 필요해지면 Docker 이미지와 VM 사양을 다시 정한다.
