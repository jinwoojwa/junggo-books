# TS-001 application.yml 기본값의 콜론 때문에 부트런이 실패한다

- 날짜: 2026-10-29
- 영역: backend

## 증상

`./gradlew bootRun`이 설정 로딩 단계에서 종료된다. DB나 Docker까지 가지 못한다.

```
mapping values are not allowed here
 in 'reader', line 41, column 67:
     ... NT:junggo-books-crawler (contact: set CRAWLER_USER_AGENT)}
```

## 원인

`application.yml`의 `app.crawler.user-agent` 기본값 안에 `contact: `(콜론+공백)가 있었다. 따옴표 없는 값에서 YAML이 이를 새 key-value 구분자로 해석한다.

```yaml
user-agent: ${CRAWLER_USER_AGENT:junggo-books-crawler (contact: set CRAWLER_USER_AGENT)}
```

## 해결

값 전체를 큰따옴표로 감쌌다.

```yaml
user-agent: "${CRAWLER_USER_AGENT:junggo-books-crawler (contact: set CRAWLER_USER_AGENT)}"
```

## 재발 방지

`AGENTS.md`의 설정 규칙에 "`: `가 들어가는 값은 따옴표로 감싼다"를 적었다. 설정 로딩을 검증하는 테스트는 아직 없다.
