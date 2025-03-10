# E-Commerce 프로젝트

## 프로젝트 개요
E-Commerce는 Spring Boot 기반의 상품 판매 및 공연 예매 시스템으로,  
대규모 트래픽에서도 안정적인 성능을 보장하는 구조를 목표로 합니다.  
주요 기능으로는 **Redis를 활용한 동시성 제어**, **Resilience4j 기반 장애 복구 로직**,  
**JWT 인증 및 Redis 블랙리스트 관리** 등을 포함합니다.

## 프로젝트 구조

```plaintext
📂 E-Commerce/
├── 📂 common/ - 공통 모듈 (보안, 예외 처리, DTO 등)
├── 📂 user-service/ - 사용자 인증 및 관리 (JWT + Redis + 이메일 인증)
├── 📂 product-service/ - 상품 및 공연 예매 서비스 (Redis 기반 좌석 예약, 캐싱)
├── 📂 order-service/ - 주문 및 결제 서비스 (Resilience4j 기반 장애 복구)
├── 📂 gateway-service/ - API Gateway (Spring Cloud Gateway, 인증/라우팅)
├── 📂 eureka-server/ - Eureka Service Discovery 서버 (MSA 환경 구성)
├── 📄 docker-compose.yml - 전체 서비스 컨테이너 관리
├── 📄 settings.gradle - Gradle 프로젝트 설정 파일
```

## 기술 스택

### 백엔드
- Java 17 - 최신 Java 기능 활용 (Record, Stream API 등)
- Spring Boot 3 - 백엔드 서비스 개발 및 REST API 설계
- Spring Security & JWT - 사용자 인증 및 Redis 기반 토큰 블랙리스트 관리
- Spring Cloud (Eureka, OpenFeign, Gateway) - 마이크로서비스 환경 구성 및 서비스 디스커버리
- Spring Data JPA (PostgreSQL) - 데이터 정합성을 유지하는 트랜잭션 처리
- Redis - 좌석 예약 시스템 구현 (동시성 제어 및 캐싱 최적화)
- Resilience4j - 서킷 브레이커 및 장애 복구 로직 적용 (결제 서비스 안정화)

### 데이터베이스
- PostgreSQL
- Redis

### DevOps
- Docker & Docker Compose
- Spring Cloud Gateway
- Eureka Server

## 주요 기능

### 사용자 인증 및 관리
- 문제: JWT 토큰 탈취 및 재사용 가능성
- 해결: Redis 기반의 블랙리스트 관리하여 보안 강화

### 공연 좌석 예약 시스템 (동시성 문제 해결)
- 문제: 다수의 사용자가 같은 좌석을 동시에 예약할 가능성
- 해결: Redis의 putIfAbsent() 연산을 활용하여 중복 예약 방지

### 주문 및 결제 시스템 (장애 복구)
- 문제: 결제 API 호출 실패 시 서비스 중단 가능성
- 해결: Resilience4j를 활용하여 자동 재시도 및 서킷 브레이커 적용

### API Gateway 및 마이크로서비스 구성
- 문제: 각 서비스별 엔드포인트 관리가 복잡해짐
- 해결: Spring Cloud Gateway를 이용해 통합 API 엔드포인트 제공

### 마이크로서비스 아키텍처
- `user-service`: 사용자 관리
- `product-service`: 공연 및 상품 관리
- `order-service`: 주문 및 결제 관리
- `gateway-service`: API Gateway
- `eureka-server`: 서비스 등록 및 디스커버리

## 추가 예정 기능

### MSA 아키텍처 강화
- 각 서비스의 독립성을 더욱 강화하고, 서비스 간 의존성을 최소화하여 유지보수성을 개선  
- 서비스 간 데이터 동기화를 위한 이벤트 기반 메시징 시스템(Kafka) 도입 고려  

### Redis 활용 고도화
- '인기 공연' 개념을 Redis에 캐싱하여 트래픽 분산 및 조회 성능 최적화  
- 사용 빈도가 높은 데이터는 TTL을 조정하여 불필요한 재요청 감소  

### CI/CD 적용
- Docker 배포 완료 후 Jenkins 또는 GitLab CI를 이용한 자동 빌드/배포 파이프라인 구축  
- 무중단 배포를 위한 블루-그린 배포 또는 롤링 업데이트 전략 적용 고려  

### 트래픽 처리 및 성능 최적화
- 실제 배포 환경에서 부하 테스트를 진행하여 성능 병목 구간 분석 및 최적화  
- API 요청 수에 따라 Auto Scaling 적용 검토  
