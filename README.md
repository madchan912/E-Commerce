# 🎟️ 대규모 트래픽 처리를 위한 MSA 기반 공연 예매 시스템

> **"기본에 충실한 설계와 AI 도구(ChatGPT, Gemini)를 활용한 효율적인 개발을 지향합니다."**

## 1. 프로젝트 개요 (Project Overview)
**E-Commerce**는 대규모 사용자가 몰리는 공연 예매 환경을 가정하여 안정성과 데이터 정합성을 목표로 개발한 **MSA(Microservices Architecture)** 프로젝트입니다.
화려한 기술보다는 **"왜 이 기술이 필요한가?"**에 집중하여, 대용량 데이터 처리와 동시성 제어 문제를 해결하는 데 주력했습니다.

* **기간:** 2024.10 ~ 2024.12
* **핵심 목표:** 동시성 이슈 제어, 대용량 데이터의 안정적 처리, 개발 효율화
* **Tech Stack:** Java 17, Spring Boot 3, Spring Batch, Redis(Redisson), Resilience4j, Docker

## 2. 프로젝트 구조 (Project Structure)

📂 E-Commerce/
├── 📂 common/           # 공통 모듈 (DTO, Utils, Error Handling)
├── 📂 user-service/     # 사용자 인증 (JWT + Redis Blacklist)
├── 📂 product-service/  # 공연/좌석 관리 (Redis Caching, Spring Batch)
├── 📂 order-service/    # 주문/결제 (Redisson Lock, Resilience4j)
├── 📂 gateway-service/  # API Gateway
├── 📂 eureka-server/    # Service Discovery
└── 📄 combine_files.sh  # (Custom) LLM 분석용 소스 병합 스크립트

## 3. 핵심 문제 해결 (Key Troubleshooting)

### 🚀 1. Spring Batch를 활용한 조회 성능 최적화
* **Problem:** 공연 종료 여부를 매번 실시간으로 조회(`end_date < now`)할 경우, 데이터 증가에 따른 DB 부하 발생.
* **Solution:** **Spring Batch**를 도입하여 매일 자정 종료된 공연 상태를 일괄 업데이트(`CLOSED`)하는 방식으로 변경.
* **Impact:** 조회 쿼리를 단순화하고, **Chunk 지향 처리(Size: 10)**를 통해 대량의 데이터를 메모리 이슈 없이 안정적으로 처리.

### 🔒 2. Redis 분산 락(Redisson)을 통한 동시성 제어
* **Problem:** 인기 공연 예매 시, 다수의 요청이 동시에 들어올 경우 **좌석 중복 예약(Race Condition)** 발생.
* **Solution:** Pub/Sub 방식의 **Redisson 분산 락**을 적용하여, DB 락보다 가볍고 확실하게 동시성 이슈를 제어.
* **Impact:** JMeter 부하 테스트 환경에서도 데이터 무결성(Data Integrity) 보장.

### 🤖 3. LLM 분석 효율화를 위한 컨텍스트 추출 자동화 (Automation)
* **Problem:** 프로젝트 규모가 커지면서, AI(ChatGPT)에게 전체 구조와 코드를 이해시키기 위해 파일을 일일이 복사/붙여넣기 하는 비효율 발생.
* **Solution:** 프로젝트 내 모든 소스 코드(.java, .yml, .gradle)를 하나의 텍스트 파일로 병합하는 **쉘 스크립트(`combine_java_files.sh`)** 자체 제작.
* **Impact:**
    * AI에게 프로젝트의 **전체 문맥(Context)**을 한 번에 주입하여, 코드 분석 및 리팩토링 제안의 정확도 향상.
    * 반복적인 프롬프트 작성 시간을 단축하고 개발 생산성 확보.

## 4. 기술 스택 (Tech Stack)

### Backend
- **Java 17 & Spring Boot 3:** 최신 LTS 버전 활용
- **Spring Cloud:** Eureka, Gateway, OpenFeign (MSA 환경 구성)
- **Database:** Spring Data JPA (PostgreSQL), QueryDSL
- **Batch:** Spring Batch (대용량 데이터 처리)

### Infrastructure & Tools
- **Cache & Lock:** Redis (Caching, Redisson)
- **Resiliency:** Resilience4j (Circuit Breaker)
- **DevOps:** Docker
- **AI Tools:** ChatGPT, Gemini (코드 리뷰 및 아키텍처 검증용)

## 5. 향후 개선 계획
* **Event-Driven:** Kafka 도입을 통한 서비스 간 결합도 완화.
* **CI/CD:** GitHub Actions를 활용한 배포 자동화.
* **Monitoring:** Prometheus & Grafana 연동.