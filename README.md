# Room Reservation Application

Spring Boot 기반 간단한 예약 애플리케이션입니다.  
로컬 환경에서 `docker compose up` 명령으로 바로 실행할 수 있도록 구성되었습니다.

---

## 실행 방법

### 1. JAR 빌드
```bash
./gradlew clean bootJar
```

### 2. Docker Compose 실행
```bash
docker compose up --build
```

- 애플리케이션: http://localhost:8080
- MySQL: localhost:3306 (사용자: `rruser`, 비밀번호: `rrpass`, DB: `rrdb`)

> 내부 네트워크에서는 애플리케이션이 `db:3306` 으로 DB에 접속합니다.

---

## 구성

- `src/` : 애플리케이션 소스코드
- `db/init/schema.sql` : 초기 테이블 스키마 및 시드 데이터
- `Dockerfile` : Spring Boot 애플리케이션 빌드/실행 컨테이너 정의
- `docker-compose.yml` : MySQL + App 컨테이너 동시 실행 정의
- `build.gradle`, `settings.gradle` : 빌드 설정

---

## LLM 사용 정책

본 프로젝트는 과제 수행 중 LLM(ChatGPT)을 **도우미로 활용**하였습니다.  
- **사용한 구간**:
  - Dockerfile / docker-compose.yml 작성 가이드
  - Spring Boot 컨트롤러/서비스/리포지토리 구조 예시
  - README.md 초안 생성
- **프롬프트 예시**:
  - "Spring Boot에서 예약 취소 로직을 어떻게 권한 기반으로 나눠 구현할 수 있을까?"
  - "Dockerfile과 docker-compose.yml을 작성해서 로컬에서 바로 구동되도록 하려면 어떻게 해야 하나?"
  - "과제 요구사항에 맞는 README.md 템플릿을 작성해줘"
---
