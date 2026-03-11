# 선착순 쿠폰 발급 시스템

> 대규모 트래픽 환경에서 안정적인 선착순 쿠폰 발급을 처리하는 이벤트 기반 시스템

## 목차

- [프로젝트 소개](#프로젝트-소개)
- [아키텍처](#아키텍처)
- [기술 스택](#기술-스택)
- [주요 기능](#주요-기능)
- [프로젝트 구조](#프로젝트-구조)
- [시작하기](#시작하기)
- [API 명세](#api-명세)
- [팀원](#팀원)

---

### 아키텍쳐
<img width="3151" height="1651" alt="도봉라이트_msa구조 drawio" src="https://github.com/user-attachments/assets/ad17278d-fff6-44ca-946d-c7f5b9b2ca98" />

## 프로젝트 소개

프로모션 이벤트에서 선착순으로 쿠폰을 발급하는 시스템입니다. Redis 기반 대기열과 Spring Batch를 활용한 청크 단위 처리, Kafka를 통한 이벤트 기반 아키텍처로 높은 동시성 환경에서도 안정적인 쿠폰 발급을 보장합니다.

**핵심 목표:**
- 동시 요청 제한(최대 100건) 및 중복 요청 방지
- Transactional Outbox 패턴을 통한 이벤트 정합성 보장
- 실시간 알림(Email, FCM) 및 통계 제공

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.3 |
| **Database** | MySQL |
| **Cache / Queue** | Redis (Lettuce, Lua Script) |
| **Messaging** | Apache Kafka 3.3.7 (KRaft 3-Broker Cluster) |
| **Batch** | Spring Batch |
| **Notification** | Firebase Cloud Messaging (FCM), Gmail SMTP |
| **Monitoring** | Prometheus, Grafana, Spring Actuator |
| **Serialization** | Jackson, Confluent Kafka Avro Serializer |
| **Infra** | Docker, Docker Compose |
| **Build** | Gradle |
| **Test** | JUnit 5, Spring Kafka Test, Spring Batch Test |

---

## 주요 기능

### 1. 선착순 쿠폰 발급
- Redis Lua Script를 활용한 원자적 대기열 관리
- 중복 요청 방지 (SETNX) 및 최대 인원 제한 (100명)
- Spring Batch 청크 단위(10건) 처리

### 2. Transactional Outbox 패턴
- `BEFORE_COMMIT`: 이벤트를 Outbox 테이블에 기록
- `AFTER_COMMIT`: 비동기로 Kafka에 발행
- Outbox Relay: 미발행 이벤트 주기적 재시도 (1분 이상 경과)

### 3. 이벤트 기반 멀티 컨슈머
| Consumer Group | 역할 |
|---------------|------|
| Notification | Email + FCM 푸시 알림 발송 |
| History | 쿠폰 발급 이력 저장 |
| Stats | 통계 업데이트 + SSE 실시간 전송 |

### 4. Dead Letter Queue (DLQ)
- 실패한 메시지를 `{topic}.DLT`로 자동 전송
- FixedBackOff(1000ms, 3회 재시도) 후 DLQ 적재

### 5. 실시간 모니터링
- Prometheus + Grafana 대시보드
- Spring Actuator 엔드포인트
- SSE를 통한 실시간 통계 스트리밍

---

## 프로젝트 구조

```
src/main/java/com/company/demo/
├── common/
│   ├── client/                  # Kafka Producer/Consumer
│   ├── config/
│   │   ├── async/               # 비동기 설정
│   │   ├── batch/               # Batch Job 설정
│   │   ├── firebase/            # Firebase 초기화
│   │   ├── kafka/               # Kafka Producer/Consumer 설정
│   │   └── redis/               # Redis 연결 설정
│   ├── constant/                # Enum (EventType, KafkaTopic, RedisKey)
│   ├── response/                # 공통 응답 포맷
│   └── util/                    # 유틸리티
│
└── giftcoupon/
    ├── controller/              # REST API 엔드포인트
    ├── service/                 # 비즈니스 로직
    ├── domain/
    │   ├── entity/              # Coupon, User, History, CouponMetadata
    │   └── repository/          # Spring Data JPA Repository
    ├── batch/                   # Spring Batch (Reader, Processor, Writer)
    ├── handler/                 # Kafka 이벤트 핸들러
    │   └── service/             # 알림, 통계 서비스
    ├── outbox/
    │   ├── domain/              # Outbox Entity & Event
    │   ├── listener/            # @TransactionalEventListener
    │   ├── recorder/            # Outbox 기록기
    │   └── relay/               # 미발행 이벤트 재시도
    ├── config/queue/            # Redis 대기열 (Lua Script)
    ├── mapper/dto/              # DTO
    └── exception/               # 커스텀 예외
```

---

## 시작하기

### 사전 요구사항

- Java 21
- Docker & Docker Compose
- MySQL
- Redis

### 실행 방법

```bash
# 1. 인프라 실행 (Kafka, Prometheus, Grafana)
docker-compose up -d

# 2. 애플리케이션 빌드 및 실행
./gradlew bootRun
```

### 환경 설정

`src/main/resources/application.yml`에서 아래 항목을 환경에 맞게 수정하세요:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/CouponSystem
  data:
    redis:
      host: localhost
      port: 6379
  kafka:
    bootstrap-servers: 127.0.0.1:10000,127.0.0.1:10001,127.0.0.1:10002
```

---

## 팀원

| 프로필 | 이름 | 역할 | GitHub |
|--------|------|------|--------|
| <img src="https://github.com/gongPyeon.png" width="100"> | 편강 | Backend | [@gongPyeon](https://github.com/gongPyeon) |
| <img src="https://github.com/Shinjongyun.png" width="100"> | 신종윤 | Backend | [@Shinjongyun](https://github.com/Shinjongyun) |

---

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
