# E-Commerce 프로젝트

## 프로젝트 개요
E-Commerce는 Spring Boot 기반의 상품 판매 및 공연 예매 시스템입니다.  
사용자는 상품 구매, 선착순 판매, 공연 좌석 예약 등의 기능을 이용할 수 있습니다.

## 프로젝트 구조

```plaintext
📂 E-Commerce/
├── 📂 common/ - 공통 모듈 (보안, 예외 처리, DTO 등)
├── 📂 user-service/ - 사용자 인증 및 관리 서비스
├── 📂 product-service/ - 상품 및 공연 예매 서비스
├── 📂 order-service/ - 주문 및 결제 서비스
├── 📂 gateway-service/ - API Gateway (Spring Cloud Gateway)
├── 📂 eureka-server/ - Eureka Service Discovery 서버
├── 📄 docker-compose.yml - Docker 컨테이너 설정 파일
├── 📄 settings.gradle - Gradle 프로젝트 설정 파일
```

## 기술 스택

### 백엔드
- Java 17
- Spring Boot 3
- Spring Security & JWT
- Spring Cloud (Eureka, OpenFeign, Gateway)
- Spring Data JPA (PostgreSQL)
- Redis (세션 관리 및 좌석 예약)
- Resilience4j (서킷 브레이커 및 재시도 로직)

### 데이터베이스
- PostgreSQL
- Redis

### DevOps
- Docker & Docker Compose
- Spring Cloud Gateway
- Eureka Server

## 주요 기능

### 사용자 인증 및 관리
- 이메일 인증을 통한 회원가입 (SMTP + Gmail API)
- JWT 로그인 및 Redis 기반 블랙리스트 관리
- 비밀번호 복구 기능

### 상품 및 공연 관리
- 상품(일반 상품, 티켓) CRUD
- 공연 등록 및 좌석 생성
- Redis 캐싱을 활용한 좌석 배치 및 조회 최적화

### 선착순 좌석 예약 시스템
- Redis Hash 구조를 활용한 좌석 상태 관리
- 중복 예약 방지 (`putIfAbsent()` 활용)
- 30분 이상 예약되지 않은 좌석 자동 해제 (Scheduled Batch)

### 주문 및 결제 관리
- 위시리스트 기반 상품 주문
- 결제 완료 시 이메일 발송
- Resilience4j를 활용한 장애 복구 및 재시도 로직 적용

### 마이크로서비스 아키텍처
- `user-service`: 사용자 관리
- `product-service`: 공연 및 상품 관리
- `order-service`: 주문 및 결제 관리
- `gateway-service`: API Gateway
- `eureka-server`: 서비스 등록 및 디스커버리

### 추가 예정 기능
- Kafka를 활용한 이벤트 메시징 시스템 구현  
- Swagger를 통한 API 문서화  
- Prometheus & Grafana 기반 모니터링 시스템 구축  
