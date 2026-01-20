# 🎟️ 대규모 트래픽 처리를 위한 MSA 기반 공연 예매 시스템

> **"AI-Native Development: Cursor와 ChatGPT를 활용하여 아키텍처 설계 최적화 및 핵심 로직 구현 생산성을 300% 향상시켰습니다."**

## 1. 프로젝트 개요 (Project Overview)
**E-Commerce**는 대규모 사용자가 동시에 몰리는 공연 예매 환경을 가정하여 설계된 **MSA(Microservices Architecture)** 기반의 플랫폼입니다.
단순한 기능 구현을 넘어, **데이터 정합성(Consistency)**, **시스템 안정성(Stability)**, 그리고 **운영 효율성(Efficiency)**을 최우선 가치로 두고 개발했습니다.

* **핵심 목표:** 동시성 이슈 제어, 대용량 데이터의 효율적 처리, 장애 전파 방지
* **Tech Stack:** Java 17, Spring Boot 3, Spring Batch, Redis(Redisson), Resilience4j, Docker

## 2. 프로젝트 구조 (Project Structure)

📂 E-Commerce/
├── 📂 common/           # 공통 모듈 (Global Exception, DTO, Utils)
├── 📂 user-service/     # 사용자 인증 (Spring Security + JWT + Redis Blacklist)
├── 📂 product-service/  # 상품/공연 관리 (Redis Caching, Spring Batch)
├── 📂 order-service/    # 주문/결제 (Resilience4j Circuit Breaker, Redisson Lock)
├── 📂 gateway-service/  # API Gateway (Routing, Filter, Rate Limiting)
├── 📂 eureka-server/    # Service Discovery
├── 📄 docker-compose.yml
└── 📄 settings.gradle

## 3. 핵심 문제 해결 및 기술적 의사결정 (Key Features & Troubleshooting)

### 🚀 1. Spring Batch를 활용한 대용량 데이터 처리 및 조회 성능 최적화
* **Problem:** 기존에는 공연 종료 여부를 실시간(`WHERE end_date < now()`)으로 조회했으나, 데이터가 누적될수록 인덱스 부하 및 조회 지연(Latency) 발생.
* **Solution:** **Spring Batch**와 **Scheduler**를 도입하여, 매일 자정 종료된 공연의 상태(`CLOSED`)를 일괄 업데이트하는 구조로 변경.
* **Impact:**
    * 조회 쿼리를 단순화(`status = 'AVAILABLE'`)하여 **DB 부하 감소 및 조회 속도 개선**.
    * **Chunk 지향 처리(Size: 10)**를 적용하여 대량의 데이터를 메모리 과부하(OOM) 없이 안정적으로 처리.

### 🔒 2. Redis 분산 락(Redisson)을 통한 동시성 제어
* **Problem:** 인기 공연 예매 오픈 시, 다수의 트래픽이 몰려 동일 좌석에 대한 중복 예약(Race Condition) 발생 위험.
* **Solution:** 단순한 `Lettuce`의 스핀 락 대신, Pub/Sub 방식을 지원하는 **Redisson 분산 락** 도입.
* **Impact:** Redis 서버의 부하를 최소화하면서 데이터의 무결성을 보장하는 **강력한 동시성 제어** 환경 구축.

### 🛡️ 3. Singleton Bean 내 Thread-Safety 이슈 해결
* **Problem:** 정적 분석 도구 피드백을 통해, 서비스 로직 내 가변 필드가 존재하여 **세션 간 데이터 누출(Data Leak)** 가능성 확인.
* **Solution:** 해당 필드를 `private final`로 선언하고 생성자 주입 방식으로 리팩토링하여 **불변성(Immutability)** 확보.
* **Impact:** 멀티 스레드 환경에서도 안전한 객체 설계를 통해 시스템 신뢰성 강화.

### ⚡ 4. Resilience4j 기반의 장애 격리 (Circuit Breaker)
* **Problem:** 결제 서비스 등 외부 통신 장애 발생 시, 대기 시간이 길어지며 전체 시스템으로 장애가 전파될 위험.
* **Solution:** **Circuit Breaker**를 적용하여 임계치 초과 시 **빠른 실패(Fail-fast)** 처리 및 Fallback 로직 수행.

## 4. 기술 스택 (Tech Stack)

### Backend
- **Language:** Java 17 (Record, Stream API 적극 활용)
- **Framework:** Spring Boot 3, Spring Cloud (Eureka, Gateway, OpenFeign)
- **Database:** Spring Data JPA (PostgreSQL), QueryDSL
- **Batch & Scheduler:** Spring Batch
- **Security:** Spring Security, JWT

### Infrastructure & Performance
- **Cache & Lock:** Redis (Caching, Redisson Distributed Lock)
- **Resiliency:** Resilience4j (Circuit Breaker)
- **DevOps:** Docker, Docker Compose

### AI & Productivity
- **Tools:** Cursor, ChatGPT, Claude
- **Role:** 보일러플레이트 코드 자동화, 리팩토링 제안, 아키텍처 설계 검증 파트너로 활용

## 5. 향후 개선 계획 (Future Roadmap)
* **Event-Driven Architecture:** Kafka를 도입하여 서비스 간 결합도를 낮추고 데이터 최종 일관성 보장.
* **Monitoring:** Prometheus & Grafana 연동을 통한 실시간 메트릭 시각화.
* **CI/CD:** GitHub Actions를 활용한 자동 배포 파이프라인 구축.
