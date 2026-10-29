# PROGRESS

## 현재 상태

- 기반 구축 단계. 백엔드 뼈대(Spring Boot 4.1 / Java 25), Flyway 마이그레이션 V1~V3, ADR 11개, 검증 스크립트와 Claude Code 하네스(Hook, Skill, reviewer)가 있다.
- 도메인·API 코드는 아직 없다(`JunggoBooksApplication`만 존재). 구현은 사용자가 직접 수행한다.
- 배포 방식은 ADR-010(VM + Docker Compose)으로 확정되어 있다. ADR-008·009는 이미 "대체됨"이다.
- 프론트엔드는 아직 없다.
- CI(GitHub Actions)가 동작하고 main은 PR + squash merge + `verify` 통과로 보호된다. 포맷은 Spotless(google-java-format AOSP)로 검사한다.

## 세션 로그

### 2026-10-29

- 프로젝트 진행 상황 점검.
- F02: CI 워크플로 추가, main 브랜치 보호와 squash merge 전용 설정 (PR #1).
- F03: Spotless(google-java-format AOSP) 도입, 기존 코드 재포맷, `.editorconfig` 추가.
- F04: ADR-012(컨텍스트별 헥사고널 구조)와 ArchUnit `ArchitectureTest` 추가. JPA 엔티티는 `adapter.out.persistence`에 분리한다.
- 기반 구축에서 추가할 항목을 정리하고 `feature_list.json`(기반 F, 백엔드 B, 프론트 FE, 배포 D)과 `PROGRESS.md`를 작성했다 (F01).

## 다음 세션에서 할 일

- 기반 구축 항목을 하나씩 확정하고 진행한다: F05 → F05 → F06 → F07 → F08 → F09 → F10 → F11.
