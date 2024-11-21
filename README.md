## 1\. 프로젝트 소개

## 🎟️ 너굴티켓

티켓 예매 및 양도, 경매 플랫폼

![1](https://github.com/user-attachments/assets/cf6fea2d-811f-4bc6-81e2-59db9a3bd5f2)

**“부당한 티켓 거래 🕵️‍♂️, 이젠 안녕!”**

너굴티켓은 티켓을 정가로만 양도할 수 있도록 지원해,

고가 되팔이로 인한 불공정한 거래를 방지하는 티켓 예매 플랫폼입니다.

<br>

## 2\. 프로젝트 핵심 목표

**1️⃣ 대규모 트래픽 대응**

-   Auto Scaling 정책을 적용하여 트래픽 급증 시에도 안정적인 요청 처리

-   Redis 분산 락을 활용한 분산 서버 환경의 동시성 제어

<br>

**2️⃣ 성능 최적화**

-   gRPC 도입으로 REST 대비 평균 응답 시간 3배 단축해 통신 성능 향상

-   OpenSearch 인덱스 구조 최적화로 검색 속도 96% 개선 및 사용자 검색 경험 강화

<br>

**3️⃣ 운영 및 배포 효율화**

-   AWS ECS Fargate를 이용해 3개의 서버를 안전하게 배포하고, 무중단 배포 방식 적용

-   Github Actions를 이용한 CI/CD 파이프라인 구축으로 배포 자동화

<br>

**4️⃣ 결제 서버 구축 및 트랜잭션 관리**

-   SQS, SNS를 이용한 분산서버 간의 비동기 메시징 및 보상 트랜잭션 처리

-   결제 서버 다운될 시 Queue에 메시지가 보관되어 서비스 내결함성 향상

<br>

## 3\. KEY SUMMARY

### 💳 분산 아키텍처, 메시징 시스템 도입 : 서비스 안정성, 확장성, 트래픽 처리 능력

**\[ 한 줄 요약 \]**

-   분산 아키텍처와 메시징 시스템 도입 : 서비스 안정성, 확장성, 트래픽 처리 능력 향상 **\]**

<br>

**\[ 도입 배경 \]**

-   기존 모놀리식 아키텍처에서 **결제 및 검색 기능 간의 종속성**으로 인해 하나의 기능 장애가 전체 서비스 중단으로 이어짐

-   민감한 결제 정보를 처리하는 별도 서버 분리가 필요했으며, **Auto Scaling** 환경에서도 안정적인 통신이 필요

<br>

\[ **기술적 선택지 \]**

1.  **모놀리식 아키텍처 유지**
    
    -   결제와 예매 기능 간의 의존성을 유지하며 하나의 서버에서 처리
    
    -   트래픽 증가 시 단일 서버의 부하가 증가하여 장애 발생 가능성 큼
    
    -   Auto Scaling 환경에서 서버 주소 동기화가 어려워 통신 오류 발생

<br>

2.  **분산 아키텍처로 전환**
    
    -   예매 서버(너굴티켓)와 결제 서버(너굴페이)를 분리
    
    -   SNS/SQS 기반 비동기 메시징 도입으로 트랜잭션 복원력 강화
    
    -   Toss API와의 통신 문제 시 **보상 트랜잭션 처리**로 안정성 확보

<br>

**\[ 결론 \]** **분산 아키텍처와 메시징 시스템 도입을 통해 서비스 안정성, 확장성, 트래픽 처리 능력 모두 향상**

<br>

\[ **성과 \]**

-   **Auto Scaling 환경 지원:** 트래픽 증가 시 동적 확장에도 결제 시스템 정상 작동

-   **비동기 처리:** Toss API 응답 지연 시에도 서비스 중단 없이 트랜잭션 복구 가능

-   **보상 트랜잭션:** 예매 서버와 결제 서버 간 통신 문제 발생 시에도 트랜잭션 보장

![2](https://github.com/user-attachments/assets/99f0fecb-cda0-40ae-b4cb-148779f70341)

---

### 🔍 OpenSearch 도입 : 공연 검색 TPS 19배 향상

-   **기존 쿼리 → MySQL 인덱싱 → OpenSearch**

-   Opensearch 도입을 통해 기존 MySQL 기반 검색에서 발생한 성능 저하 문제를 해결하고, 검색 속도와 사용자 경험을 동시에 개선

<br>

📈 **성능테스트 결과**

![3](https://github.com/user-attachments/assets/665bff0d-3561-4c9b-abd4-53965c347900)

<br>

| **테스트 종류** | **총 소요시간** | **평균 TPS** | **쓰레드 평균 소요시간** | **에러율** |
| --- | --- | --- | --- | --- |
| **일반 쿼리** | 1분 34초 | 531.0 | 9.27/sec | 0% |
| **MySQL Indexing** | 1분 31초 | 548.1 | 9.00/sec | 0% |
| OpenSearch | **5초** | **10600.2** | **0.35/sec** | 0% |

<br>

-   **TPS 개선** <br> Elasticsearch 도입 후 <ins>TPS(초당 처리량)가 기존 방식 대비 약 19배 증가</ins>하여 더 많은 요청을 동시에 처리할 수 있는 환경을 구축

-   **응답 시간 단축** <br> <ins>쓰레드 평균 소요 시간이 96% 이상 감소함</ins>으로써 사용자 검색 요청에 대한 응답 속도가 비약적으로 단축

-   **전체 성능 향상** <br> <ins>기존 방식 대비 90% 이상의 성능 개선</ins>을 이루었으며, 처리 속도와 응답 시간을 획기적으로 개선하여 사용자 경험을 크게 향상

-   **상세 결과**

    <details>
      <summary><b>MySQL 인덱싱</b></summary>
          <div markdown="1">
            <br> -   <b>ElasticSearch</b>
            <img src="https://github.com/user-attachments/assets/82ed412e-4ae4-427a-9c72-0a38eb8d851d">
            <img src="https://github.com/user-attachments/assets/7d0d74a0-1111-4225-a1dd-268a6dce13e1">
            <img src="https://github.com/user-attachments/assets/86af545b-fdcb-403a-8a99-d513aa655ffe">
            <img src="https://github.com/user-attachments/assets/9457a0f6-5e98-4497-9591-a899df072876">
          </div>
    </details>

    <details>
      <summary><b>OpenSearch</b></summary>
          <div markdown="1">
            <img src="https://github.com/user-attachments/assets/82ed412e-4ae4-427a-9c72-0a38eb8d851d">
            <img src="https://github.com/user-attachments/assets/7d0d74a0-1111-4225-a1dd-268a6dce13e1">
          </div>
    </details>

<br>
   

## 4\. 인프라 아키텍처 & 적용 기술

### 🏰 인프라 아키텍처

![10](https://github.com/user-attachments/assets/b6e1858c-755f-48df-82ec-28fa702b29b4)

-   Springboot 기반의 각 모듈은 SQS, SNS, gRPC로 통신

-   ECR에 저장되는 Docker 이미지로 ECS Fargate에 배포

-   모두 프라이빗 서브넷에 배치되며, Github Actions CI/CD를 통해 자동 배포

<br>

### 💎 적용 기술

<table>
  <thead>
    <tr>
      <th align="left">💻 Programming and Frameworks</th>
      <th align="left">🗂️ Database and Storage</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <strong>프로그래밍 언어</strong><br>
        - Java<br><br>
        <strong>프레임워크</strong><br>
        - Spring Boot<br><br>
        <strong>ORM</strong><br>
        - Hibernate<br><br>
        <strong>빌드 도구</strong><br>
        - Gradle
      </td>
      <td>
        <strong>데이터베이스</strong><br>
        - RDS (MySQL)<br>
        - ElastiCache (Redis)<br>
        - S3<br><br>
        <strong>검색 엔진</strong><br>
        - OpenSearch
      </td>
    </tr>
    <tr>
      <th align="left">🔒 Security and Authentication</th>
      <th align="left">🌏 Infrastructure and Deployment</th>
    </tr>
    <tr>
      <td>
        <strong>인증/보안</strong><br>
        - Spring Security<br>
        - JWT<br>
        - Google OTP<br>
        - Naver Email SMTP
      </td>
      <td>
        <strong>배포 환경</strong><br>
        - AWS: ECR, ECS Fargate, CloudFront<br>
        - ALB, NAT Gateway, Route53<br>
        - Docker<br>
        - GitHub Actions<br>
        - Auto Scaling
      </td>
    </tr>
    <tr>
      <th align="left">💬 Communication and Messaging</th>
      <th align="left">🔍 Monitoring and Collaboration</th>
    </tr>
    <tr>
      <td>
        <strong>메시지 브로커</strong><br>
        - AWS SQS<br>
        - AWS SNS<br>
        - STOMP<br><br>
        <strong>실시간 이벤트</strong><br>
        - SSE<br>
        - WebSocket
      </td>
      <td>
        <strong>모니터링 도구</strong><br>
        - JMeter<br>
        - Prometheus<br>
        - Grafana<br><br>
        <strong>협업 툴</strong><br>
        - Notion<br>
        - Slack<br><br>
        <strong>버전 관리</strong><br>
        - Git & GitHub
      </td>
    </tr>
  </tbody>
</table>

<br>

## 5\. 주요 기능

**⛱️ 티켓 예매 & 양도 : SNS / SQS를 활용한 비동기 처리**

-   주요 흐름 : 티켓 상태를 결제 대기로 변경 → 결제 정보 작성 → 결제 승인 요청 → 결제 결과 저장 & 티켓 상태를 예매 완료로 변경

-   오류 발생 시, 롤백 진행을 통해 <ins>데이터 일관성 유지</ins>

<br>

**⛱️ 좌석 예매 시 실시간 좌석 정보 변경 알림 : SSE, SQS 활용**

-   주요 흐름 : 사용자가 좌석 선택 API 접속(서버 연결) → 특정 좌석이 다른 사용자에 의해 예매됨 → 해당 좌석의 정보가 “예매됨”으로 변경 → 서버가 이를 감지하여 바뀐 좌석정보를 사용자에게 알림 → UI에 적용

-   <ins>새로고침 없이 실시간으로 좌석 정보 변경사항을 사용자에게 제공</ins>하여 사용자 경험 개선

<br>

**⛱️ 공연 키워드 검색 : OpenSearch를 활용해 사용자 검색 경험 향상**

-   <ins>검색어 자동완성, 초성 검색, 동의어 검색 기능 제공</ins>

-   <ins>검색어와 가장 유사한 공연을 우선적으로 표시</ins>하여 편리한 검색 지원

<br>

**⛱️ 공연&키워드 실시간 랭킹 조회 : Redis 캐싱 활용**

-   빠른 응답 속도로 최신 <ins>트렌드 반영</ins>

-   실시간 데이터 처리로 검색 효율성 및 서비스 신뢰도 향상

<br>

**⛱️ 보안 E-mail , OTP 인증**

-   회원가입시 <ins>E-mail 인증</ins> → 판매자는 <ins>2차 OTP 인증</ins>을 완료해야 공연 생성 가능

-   이메일과 OTP 인증의 이중 방어로 <ins>민감한 정보 보호 강화</ins>

<br>

**⛱️ 사용자 맞춤 추천 시스템 : GPT API 활용**

-   사용자의 관심 공연 데이터를 기반으로 한 GPT의 맞춤 추천 시스템

-   <ins>사용자의 관심 키워드를 반영한 맞춤 공연 추천</ins>

<br>

⏳ **서비스 전체 흐름도**

![11](https://github.com/user-attachments/assets/a9b0105e-6469-46a9-8796-5da214b0b077)

<details>
      <summary><b>기능별 흐름도</b></summary>
            <ul>
                    <details> 
                          <summary><b>회원가입 및 로그인</b></summary>
                          <div markdown="1">
                          <img src="https://github.com/user-attachments/assets/91cf75ae-cd90-4b00-9ad2-e97e81af8364">
                            </div>
                       </details>
                <details> 
                          <summary><b>공연 관리</b></summary>
                          <div markdown="1">
                          <img src="https://github.com/user-attachments/assets/09706fc4-a48c-400f-b8fb-20276cc54f0a">
                            </div>
                       </details>
                <details> 
                          <summary><b>티켓 관리</b></summary>
                          <div markdown="1">
                          <img src="https://github.com/user-attachments/assets/832a16ae-ecea-4c97-a829-596294ffb806">
                            </div>
                       </details>
                <details> 
                          <summary><b>티켓 양도</b></summary>
                          <div markdown="1">
                          <img src="https://github.com/user-attachments/assets/6d7de60a-667e-4b5d-ad00-b64448b96d84">
                            </div>
                       </details>
                                <details> 
                          <summary><b>티켓 경매</b></summary>
                          <div markdown="1">
                          <img src="https://github.com/user-attachments/assets/3e76e7a3-59d8-44ef-9c0c-7c95c2bdc551">
                            </div>
                       </details>
                <details> 
                          <summary><b>검색</b></summary>
                          <div markdown="1">
                          <img src="https://github.com/user-attachments/assets/2a0cc575-2eca-4def-a7b3-b2b124c22b34">
                            </div>
                       </details>
                <details> 
                          <summary><b>관리자</b></summary>
                          <div markdown="1">
                          <img src="https://github.com/user-attachments/assets/dc801f6d-c0b9-4c6a-8353-5fad50e5210b">
                            </div>
                       </details>
                <details> 
                          <summary><b>판매자</b></summary>
                          <div markdown="1">
                          <img src="https://github.com/user-attachments/assets/c4ac1e5c-33c7-477a-84e8-a657cc1950ef">
                            </div>
                       </details>
                <details> 
                          <summary><b>유저 정보 수정</b></summary>
                          <div markdown="1">
                          <img src="https://github.com/user-attachments/assets/c3092e73-435a-4512-8c48-c1331cae25c5">
                            </div>
                       </details>
              </ul>
          </div>
</details>

<br>

## 6\. 기술적 고도화

<br>

### 🌟 기술적 의사결정
<details>
  <summary><b>[ AWS ] 인프라 구조 설계</b></summary>
  <div markdown="1">
    <h3>인프라 구조 설계</h3>
    <hr>
    <b>[📋 구현한 기능]</b>
    <ul>
      <li>예매 서버와 결제 서버, 실시간 서버를 <ins>AWS ECS fargate 환경</ins>ins>에 안전하게 배포했다.</li>
      <li>모든 서버와 데이터베이스를 프라이빗 서브넷에 배치하고, ALB(Application Load Balancer)를 활용해 외부에서 예매 서버에 접근하도록 구성했다.</li>
      <li>결제 서버는 외부 요청이 직접 필요하지 않아 서비스 검색 엔드포인트를 사용해 접근할 수 있도록 설정했고, 토스페이먼츠 API를 호출하는 경우가 있어 NAT gateway를 연결하여 외부 인터넷에 접근할 수 있도록 설정했다.</li>
    </ul>
    <b>[전체 로직]</b>
    <div>
      <img src="https://github.com/user-attachments/assets/156dc11d-fce7-4099-96b2-9eb45767ff05" alt="전체 로직">
    </div>
    <b>[⛰️ 배경]</b>
    <ul>
      <li>결제 서버와 예매 서버를 개별적으로 배포하면서, 결제 서버가 더 높은 보안이 요구되는 서비스임을 고려했다.</li>
      <li>이전까지 쉽게 접근했던 퍼블릭 서버 배포 방식에서 벗어나 프라이빗 서브넷을 사용해 외부 접근을 차단하고, 네트워크 내부에서만 서버 간 통신이 가능하도록 하는 것이 적절하다고 판단했다. 이 과정에서 ALB와 프라이빗 서브넷을 활용하는 방법을 새로 학습하게 되었다.</li>
    </ul>
    <b>[🏁 요구사항]</b>
    <ol>
      <li>예매 서버, 실시간 서버는 외부 사용자 요청을 수신해야 한다.
        <ul>
          <li>예매 서버는 결제 서버를 호출할 수 있어야 한다.</li>
          <li>외부에서 예매 서버에 접근하기 위한 방법이 필요하다.</li>
        </ul>
      </li>
      <li>결제 서버는 구조와 보안상 외부에서 접근이 허용되지 않고 오로지 예매 서버에서만 호출된다.
        <ul>
          <li>외부에서 들어오는 연결이 없어야 한다.</li>
          <li>외부 API를 호출할 수 있어야 한다.</li>
        </ul>
      </li>
      <li>DB와 Cache는 보안을 위해 프라이빗 서브넷에 위치시켜야 한다.</li>
    </ol>
    <b>[✅ 선택지]</b>
    <ol>
      <li><b>ALB + 프라이빗 서브넷</b>
        <ul>
          <li>ALB를 통해 외부 트래픽을 프라이빗 서브넷 내 예매 서버로 라우팅한다. → 보안성과 접근성을 모두 보장.</li>
          <li>트래픽 증가 시 Auto scaling을 추가할 가능성이 있는 만큼 확장성을 위해서 또한 적절하다.</li>
        </ul>
      </li>
      <li><b>프라이빗 서버 + 퍼블릭 서버</b>
        <ul>
          <li>결제 서버만 프라이빗 서브넷에 배치하고 예매 서버는 퍼블릭 서브넷에 배치한다. → 외부 접근이 직접 가능하도록 하여 간소화하는 방법.</li>
          <li>외부에서 예매 서버에 직접 접근할 수는 있지만, 보안 측면에서 프라이빗 서버에 비해 취약해질 가능성이 큼.</li>
        </ul>
      </li>
      <li><b>NAT 게이트웨이 + 보안 그룹</b>
        <ul>
          <li>모든 서버를 프라이빗 서브넷에 배치하되, NAT 게이트웨이와 보안 그룹 설정을 통해 예매 서버에 대한 특정 외부 IP 접근을 허용한다.</li>
          <li>→ 초반 배포방법 학습 전 선택지에 넣었으나, NAT 게이트웨이는 외부에서 프라이빗 서버로의 접근을 허용하지 않으므로 이 방식은 적절하지 않음을 알게 됨.</li>
        </ul>
      </li>
      <li><b>Bastion Host</b>
        <ul>
          <li>Bastion Host를 퍼블릭 서브넷에 배치하여 외부에서 관리자가 SSH 또는 RDP를 통해 Bastion Host로 접근한다.</li>
          <li>이를 통해 프라이빗 서브넷 내의 예매 서버와 결제 서버에 간접적으로 접근할 수 있도록 한다.</li>
          <li>→ 초반 배포방법 학습 전 선택지에 넣었으나, Bastion Host는 관리 및 유지보수 목적으로 주로 사용되며 일반 사용자 트래픽 처리는 지원하지 않음을 알게 됨.</li>
        </ul>
      </li>
    </ol>
    <b>[💡 의사결정 및 사유]</b>
    <ul>
      <li><b>선택한 방법:</b> ALB + 프라이빗 서브넷</li>
      <li><b>선택한 이유:</b>
        <ul>
          <li>ALB는 프라이빗 서브넷 내의 예매 서버와 실시간 서버만 접근 가능하도록 트래픽을 제한한다.</li>
          <li>이를 통해 민감한 데이터가 외부에 노출되지 않고 안전하게 전송된다. 또한, ALB는 외부 요청을 하나의 진입점으로 관리하며 서버 간 로드 밸런싱을 통해 서비스를 보다 쉽게 확장할 수 있도록 한다.</li>
          <li>결제 서버는 외부 요청 없이 오직 예매 서버를 통해서만 접근 가능하도록 설정했다.</li>
          <li>이를 통해 결제 서버와 예매 서버 간 통신 경로가 단순화되고, 외부 트래픽이나 잘못된 요청으로 인한 네트워크 리소스 낭비를 방지할 수 있다.</li>
        </ul>
      </li>
    </ul>
  </div>
</details>


<details>
  <summary><b>[ Fargate ] 배포 컨테이너 선택</b></summary>
  <div markdown="1">
    <h3>배포 컨테이너 선택</h3>
    <hr>
    <b>[📋 구현한 기능]</b>
    <p>서버 인프라를 AWS ECS Fargate로 배포하여 자동 확장 기능과 무중단 배포를 유연하게 지원할 수 있도록 구성했다.</p>
    <b>[주요 로직]</b>
    <ul>
      <li>서버 애플리케이션을 AWS ECS Fargate에서 컨테이너로 배포하고, 트래픽 상황에 따라 Auto Scaling을 통해 유동적으로 확장 가능하도록 설정했다.</li>
      <li>무중단 배포 방식을 적용하여 배포 중에도 서비스 중단 없이 지속적으로 운영될 수 있도록 구성했다.</li>
      <li>Auto Scaling 정책을 사용해 트래픽 급증 시에도 안정적으로 요청을 처리할 수 있도록 준비했다.</li>
    </ul>
    <b>[⛰️ 배경]</b>
    <ul>
      <li>티켓 예매 서비스의 특성상 특정 이벤트(티켓 예매 시점 등)에는 대량의 트래픽이 몰릴 가능성이 크다. 이를 대비해 자동 확장이 가능하고 대규모 트래픽을 안정적으로 처리할 수 있는 아키텍처가 필요했다.</li>
      <li>스프링부트 애플리케이션이 환경에 상관없이 동일하게 실행되도록 컨테이너 형식으로 배포하기로 결정했으며, 이에 적합한 인스턴스를 선택해야 했다.</li>
    </ul>
    <b>[🏁 요구사항]</b>
    <ol>
      <li>컨테이너 형식으로 배포하기 적합한 환경이어야 한다.</li>
      <li>대규모 트래픽 발생 시 서버가 자동으로 확장되어 성능이 유지되어야 한다.</li>
      <li>서버 인프라의 관리 부담이 적어야 하며, 운영 및 배포 과정이 간편해야 한다.</li>
      <li>무중단 배포를 지원하여 서비스 중단 없이 안정적으로 배포할 수 있어야 한다.</li>
      <li>서비스 특성상 예기치 않은 트래픽 급증에 신속히 대응할 수 있어야 한다.</li>
    </ol>
    <b>[✅ 선택지]</b>
    <ol>
      <li><b>ECS Fargate 사용</b>
        <ul>
          <li><b>장점:</b> ECS Fargate는 컨테이너 중심으로 설계된 서비스로, 컨테이너의 배포와 관리를 간소화한다. 서버리스 아키텍처로 컨테이너 실행 환경을 자동으로 설정해주며, 트래픽 증가에 따라 컨테이너 수를 자동으로 조정하고 무중단 배포를 지원하여 운영 부담을 줄일 수 있다.</li>
          <li><b>단점:</b> 다른 옵션에 비해 비용이 상대적으로 높을 수 있다.</li>
        </ul>
      </li>
      <li><b>EC2 인스턴스 사용</b>
        <ul>
          <li><b>장점:</b> 비용 효율적이며, EC2를 직접 관리함으로써 다양한 설정이 가능하다.</li>
          <li><b>단점:</b> 트래픽 증가 시 수동 확장 또는 Auto Scaling 설정이 필요하고, 무중단 배포 구현 시 추가 설정 및 관리 부담이 발생한다.</li>
        </ul>
      </li>
      <li><b>ECS EC2 모드 사용</b>
        <ul>
          <li><b>장점:</b> EC2 기반으로 ECS 클러스터를 구성해 컨테이너를 관리하며, Auto Scaling과 무중단 배포 구현이 가능하다.</li>
          <li><b>단점:</b> EC2 인스턴스를 관리해야 하며, 확장성과 관리 측면에서 부담이 크다.</li>
        </ul>
      </li>
      <li><b>Elastic Beanstalk 사용</b>
        <ul>
          <li><b>장점:</b> 간편한 배포 및 확장 기능을 제공하며, 컨테이너나 EC2 인스턴스를 관리하는 부담을 줄인다.</li>
          <li><b>단점:</b> 프라이빗 서브넷 구성 같은 세부적인 네트워크 설정이 제한적이고, 복잡한 트래픽 관리나 배포 전략 설정에 유연성이 부족하다.</li>
        </ul>
      </li>
    </ol>
    <b>[💡 의사결정 및 사유]</b>
    <ul>
      <li><b>선택한 방법:</b> ECS Fargate</li>
      <li><b>선택 이유:</b>
        <ul>
          <li>유연한 자동 확장 → ECS Fargate는 컨테이너 수요에 따라 자원을 자동으로 할당하여 대규모 트래픽에도 유연하게 대응할 수 있다. 즉, 트래픽 변동성이 큰 티켓 예매 서비스에 적합하다.</li>
          <li>운영 부담 감소 → 서버리스 환경으로 EC2 인스턴스 관리를 완전히 제거하고, 자동 설정되는 컨테이너 실행 환경으로 운영 부담을 줄일 수 있다.</li>
          <li>무중단 배포 지원 → Fargate는 롤링 업데이트와 같이 서비스 중단 없이 배포가 가능하며, 안정적인 운영을 보장한다.</li>
          <li>트래픽 변동성 대응 → 트래픽 변동성이 큰 티켓 예매 서비스에 적합하다.</li>
        </ul>
      </li>
    </ul>
  </div>
</details>


<details>
  <summary><b>[ 메시징 구조 선택 ] 좌석정보 변경 감지를 위한 서버간 이벤트 동기화 방법</b></summary>
  <div markdown="1">
    <h3>좌석정보 변경 감지를 위한 서버간 이벤트 동기화 방법</h3>
    <hr>
    <b>[📋 구현한 기능]</b>
    <p>좌석 상태가 변경되면 일반 서버에서 메시지를 생성하고, 이를 실시간 서버가 구독하여 알림을 처리할 수 있도록 큐 구조를 사용하여 메시지를 전달하는 시스템을 구현했습니다.</p>
    <b>[주요 로직]</b>
    <ol>
      <li><b>일반 서버</b>
        <ul>
          <li>좌석 상태가 변경될 때 메시지(좌석 ID, 변경 상태, 변경 시간)를 생성.</li>
          <li>메시지를 큐에 푸시.</li>
        </ul>
      </li>
      <li><b>실시간 서버</b>
        <ul>
          <li>큐를 구독하여 메시지를 수신.</li>
          <li>수신된 메시지를 기반으로 SSE 알림을 생성하고 클라이언트에 전송.</li>
        </ul>
      </li>
    </ol>
    <b>[⛰️ 배경]</b>
    <p>좌석 예매 시스템에서는 일반 서버와 실시간 서버가 분리되어 동작합니다. 좌석 상태가 변경될 때 실시간 서버가 이를 인지하지 못하면 클라이언트에 즉시 알림을 전송할 수 없습니다.</p>
    <p>따라서, 두 서버 간 데이터를 전달하기 위해 <b>메시지 브로커 역할</b>을 하는 시스템이 필요했습니다. 이를 통해 서버 간 결합도를 낮추고, 실시간 서버가 일시적으로 다운되더라도 데이터를 안전하게 전달할 수 있는 방법이 필요하여 큐를 선택했습니다.</p>
    <b>[🏁 요구사항]</b>
    <ol>
      <li>일반 서버에서 발생한 좌석 상태 변경 사항을 실시간 서버가 빠르고 안정적으로 인지해야 한다.</li>
      <li>서버 간 느슨한 결합을 유지하여 확장성을 확보한다.</li>
      <li>실시간 서버가 다운되더라도 메시지를 잃지 않도록 해야 한다.</li>
    </ol>
    <b>[✅ 선택지]</b>
    <ol>
      <li><b>큐 구조</b>
        <ul>
          <li><b>장점:</b>
            <ul>
              <li>메시지를 임시 저장하여 수신자가 준비되었을 때 전달 가능하다.</li>
              <li>서버 간 독립성을 유지하며, 서버 다운 시에도 데이터 손실을 방지한다.</li>
              <li>높은 트래픽에도 안정적으로 메시지를 처리하고 소비 속도를 제어할 수 있다.</li>
            </ul>
          </li>
          <li><b>단점:</b>
            <ul>
              <li>메시지 전달 과정에서 약간의 지연이 발생할 수 있다.</li>
              <li>메시지 브로커 설정 및 관리를 추가적으로 요구한다.</li>
            </ul>
          </li>
        </ul>
      </li>
      <li><b>Pub/Sub 구조</b>
        <ul>
          <li><b>장점:</b>
            <ul>
              <li>실시간 전송에 적합하며, 여러 수신자에게 동시에 메시지를 전송 가능하다.</li>
              <li>소비자가 여러 개일 경우 메시지 복제를 자동 처리해 병렬 처리에 유리하다.</li>
            </ul>
          </li>
          <li><b>단점:</b>
            <ul>
              <li>메시지 보존 기간이 제한적이며, 수신자가 메시지를 놓치면 복구가 어렵다.</li>
              <li>이벤트 브로드캐스트에는 적합하지만, 1:1 메시징에는 비효율적이다.</li>
            </ul>
          </li>
        </ul>
      </li>
      <li><b>HTTP 통신</b>
        <ul>
          <li><b>장점:</b>
            <ul>
              <li>메시지 브로커 없이 간단하게 구현 가능하다.</li>
            </ul>
          </li>
          <li><b>단점:</b>
            <ul>
              <li>서버 간 결합도가 높아 확장성과 유지보수성이 떨어진다.</li>
              <li>실시간 서버가 다운되면 메시지가 손실될 가능성이 크다.</li>
            </ul>
          </li>
        </ul>
      </li>
    </ol>
    <b>[💡 의사결정/사유]</b>
    <ul>
      <li><b>선택한 방법:</b> 큐 구조</li>
      <li><b>선택 사유:</b>
        <ul>
          <li>일반 서버와 실시간 서버 간 결합도를 낮추어 두 서버가 독립적으로 동작할 수 있습니다. 이를 통해 발신자 및 수신자를 각각 확장할 수 있고 서버간 의존도가 낮아져 서버 확장성과 유지보수성을 확보할 수 있습니다.</li>
          <li>메시지를 임시 저장해 수신자가 준비되지 않아도 데이터를 안전하게 보존하고, 실패한 메시지를 다시 처리할 수 있습니다.</li>
          <li>Pub/Sub 구조는 다수의 수신자에게 실시간으로 메시지를 전달할 때 적합하지만, 이번 시스템처럼 1:1 메시징에는 과도한 방법입니다.</li>
          <li>HTTP 통신은 간단하지만 데이터 손실 가능성이 높고 확장성도 부족해 요구사항을 충족하지 못했습니다.</li>
        </ul>
      </li>
    </ul>
  </div>
</details>

<details>
  <summary><b>[ SQS ] 큐 구조에서 AWS SQS 선택 이유</b></summary>
  <div markdown="1">
    <h3>큐 구조에서 AWS SQS 선택 이유</h3>
    <hr>
    <b>[📋 구현한 기능]</b>
    <p>좌석 상태 변경 시 일반 서버에서 <b>AWS SQS</b>로 메시지를 전송하고, 실시간 서버가 이를 구독하여 처리하는 <b>큐 기반 메시징 시스템</b>을 구현했습니다. 일반 서버는 AWS SQS에 메시지를 푸시하고, 실시간 서버는 큐에서 메시지를 받아 클라이언트에 실시간 알림을 전송합니다.</p>
    <b>[주요 로직]</b>
    <ul>
      <li><b>일반 서버:</b> AWS SQS API를 사용해 좌석 상태 변경 메시지를 큐에 푸시.</li>
      <li><b>실시간 서버:</b>
        <ul>
          <li>AWS SQS Queue를 구독하여 메시지를 수신.</li>
          <li>수신한 메시지를 기반으로 SSE 알림을 생성하고 클라이언트에 전송.</li>
        </ul>
      </li>
    </ul>
    <b>[⛰️ 배경]</b>
    <p>좌석 상태 변경 정보를 안정적으로 전달하기 위해 메시지 큐 시스템을 도입했습니다. Kafka, RabbitMQ, AWS SQS 등 여러 옵션 중에서 AWS SQS를 선택했습니다. AWS SQS는 <b>AWS 인프라와의 통합</b>이 용이하고, <b>완전 관리형 서비스</b>로 운영 부담이 적으며, 안정적인 메시지 전달과 낮은 비용을 보장합니다.</p>
    <p>Kafka나 RabbitMQ와 같은 고급 메시징 시스템은 과도한 선택이며, 좌석 상태 변경 이벤트 중심의 간단한 메시지 처리에는 SQS가 적합하다고 판단했습니다.</p>
    <b>[🏁 요구사항]</b>
    <ul>
      <li>메시지 전송 및 소비 과정에서 데이터 손실을 방지해야 한다.</li>
      <li>높은 가용성과 확장성을 제공해야 한다.</li>
      <li>AWS 인프라와의 통합이 용이해야 한다.</li>
      <li>유지보수와 운영 비용을 최소화 해야 한다.</li>
    </ul>
    <b>[✅ 선택지]</b>
    <ul>
      <li><b>AWS SQS</b>
        <ul>
          <li><b>장점:</b>
            <ul>
              <li>완전 관리형 서비스로 별도의 설치나 운영 필요 없음.</li>
              <li>메시지 보존 기간 설정 가능(최대 14일)으로 안정적인 메시지 전달 보장.</li>
              <li>AWS IAM 통합으로 보안 관리가 용이하며, AWS 기반 인프라와 완벽히 연계.</li>
              <li>높은 트래픽 처리 능력을 제공하며 확장성이 뛰어남.</li>
            </ul>
          </li>
          <li><b>단점:</b>
            <ul>
              <li>Kafka나 RabbitMQ에 비해 메시징 기능(예: 메시지 스트리밍, 고급 패턴 지원)이 제한적.</li>
            </ul>
          </li>
        </ul>
      </li>
      <li><b>Kafka</b>
        <ul>
          <li><b>장점:</b>
            <ul>
              <li>대규모 데이터 처리와 메시지 스트리밍에 최적화된 설계.</li>
              <li>높은 처리량과 성능 제공.</li>
            </ul>
          </li>
          <li><b>단점:</b>
            <ul>
              <li>직접 클러스터를 구성하고 운영해야 하므로 복잡도가 높음.</li>
              <li>AWS 관리형 서비스가 아닌 경우, 유지보수와 운영 비용이 크게 증가.</li>
            </ul>
          </li>
        </ul>
      </li>
      <li><b>RabbitMQ</b>
        <ul>
          <li><b>장점:</b>
            <ul>
              <li>복잡한 메시징 패턴(예: 라우팅 키, 토픽)을 지원하며 유연성이 높음.</li>
              <li>상대적으로 낮은 지연 시간 제공.</li>
            </ul>
          </li>
          <li><b>단점:</b>
            <ul>
              <li>클러스터를 직접 구성하고 유지보수해야 하며, 운영 부담이 큼.</li>
              <li>대규모 트래픽 처리에는 상대적으로 부적합.</li>
            </ul>
          </li>
        </ul>
      </li>
    </ul>
    <b>[💡 의사결정/사유]</b>
    <ul>
      <li><b>선택한 방법:</b> AWS SQS</li>
      <li><b>선택 이유:</b>
        <ul>
          <li>AWS 기반 인프라와 완벽히 통합되며, IAM과 연계된 보안 관리가 용이하다.</li>
          <li>완전 관리형 서비스로 별도의 클러스터 구성이나 운영이 필요하지 않아 유지보수 부담이 적다.</li>
          <li>좌석 상태 변경 메시지는 대규모 데이터 스트리밍이 아닌 이벤트 기반 메시지 처리에 해당하므로 Kafka는 과도한 솔루션이다.</li>
          <li>RabbitMQ는 고급 메시징 패턴이 요구되지 않으며, 별도의 클러스터 관리 부담 때문에 부적합하다.</li>
          <li>SQS는 <b>운영 효율성과 비용 절감</b>을 동시에 만족시키며, 요구사항을 충족하는 가장 적합한 선택이었다.</li>
        </ul>
      </li>
    </ul>
  </div>
</details>

<details>
  <summary><b>[ STOMP ] Socket 통신을 활용한 1:1 채팅</b></summary>
  <div markdown="1">
    <h3>Socket 통신을 활용한 1:1 채팅</h3>
    <hr>
    <b>[📋 구현한 기능]</b>
    <ul>
      <li><b>1:1 채팅</b>
        <ul>
          <li>STOMP를 활용하여 WebSocket 채팅 환경을 구성.</li>
          <li>MySQL을 활용하여 실시간으로 채팅 내용을 저장하고, 1:1 채팅방 입장 시 이전 채팅 내용을 표시.</li>
        </ul>
      </li>
    </ul>
    <b>[⛰️ 배경]</b>
    <ul>
      <li><b>UX 강화</b>
        <ul>
          <li>사용자가 시스템 장애나 문제를 겪을 경우 전화를 통해 문의를 남기는 방식을 보완.</li>
          <li>전화 사용이 어려운 경우 메시지를 통해 상담사에게 문의할 수 있도록 기능 제공.</li>
        </ul>
      </li>
    </ul>
    <b>[주요 로직]</b>
    <ul>
      <li><b>STOMP를 활용한 Pub/Sub 구조</b>
        <ul>
          <li>EndPoint를 설정하고, Controller에서 Publish한 내용을 Subscribe하고 있는 채팅방에 게시.</li>
        </ul>
      </li>
      <li><b>채팅 내용 저장</b>
        <ul>
          <li>MySQL을 통해 유저가 작성한 내용을 DB에 저장.</li>
          <li>유저가 채팅방에 입장하면 이전에 작성된 내용을 불러와 표시.</li>
        </ul>
      </li>
    </ul>
  </div>
</details>

    

<details>
  <summary><b>[ 분산서버 & TOSS Payment ] 결제 시스템 아키텍쳐 설계 및 개선</b></summary>
  <div markdown="1">
    <h3>초기 아키텍쳐 설계 및 문제점</h3>
    <hr>
    <b>🚧 기존</b>
    <ul>
      <li>모놀리식 아키텍처: 예매 서버에서 티켓팅과 결제를 진행하는 구조로 작성.</li>
    </ul>
    <b>🏁 문제점</b>
    <ul>
      <li>예매 서버에서 티켓 선택 ~ 결제 진행 시 발생한 문제로 인해 모든 기능이 마비되는 현상이 발생.</li>
      <li>세부적인 문제:
        <ul>
          <li>결제 기능에서 문제가 발생했을 때 검색 기능까지 마비되는 현상.</li>
          <li>검색/티켓 선택 기능 문제로 인해 결제 기능이 중단되는 현상.</li>
          <li>결제와 같은 민감한 정보에 대해 별도의 보안 처리가 필요.</li>
        </ul>
      </li>
    </ul>
    <b>💡 의사결정</b>
    <ul>
      <li>예매 서버(너굴티켓)와 결제 서버(너굴페이)를 분리.</li>
      <li>동기식 API 호출 방식을 채택:
        <ul>
          <li>예매 서버(너굴티켓) → 결제 서버(너굴페이) → TOSS API의 흐름으로 설계.</li>
          <li>결제 승인 요청을 결제 서버를 통해 TOSS API로 전달하고 반환.</li>
        </ul>
      </li>
    </ul>
    <b>흐름도 (1차)</b>
    <p>
      <img src="https://github.com/user-attachments/assets/4985a839-9178-4daf-868d-5dbde51a9ae9" alt="Flowchart of initial architecture design">
    </p>
  </div>
</details>


<details>
  <summary><b>[ 보상 트랜잭션 ] 결제 승인 결과에 따른 보상 트랜잭션 설계</b></summary>
  <div markdown="1">
    <h3>보상 트랜잭션 설계</h3>
    <hr>
    <b>보상 트랜잭션 처리를 진행할 범위</b>
    <ul>
      <li>Toss API에 결제 승인 요청을 보냈을 경우, 각 응답에 따른 트랜잭션 처리를 진행.</li>
    </ul>
    <b>📕 시나리오 리스트</b>
    <ul>
        <details>
           <summary><b>결제 승인 실패</b></summary>
        <p>
          <img src="https://github.com/user-attachments/assets/98c70935-8c22-4d19-ac5d-7213749695c2" alt="결제 승인 실패 시나리오">
        </p>
        </details>
        <details>
           <summary><b>결제 승인에 성공했지만, Toss로부터 결과가 반환되지 않은 경우</b></summary>
        <p>
          <img src="https://github.com/user-attachments/assets/d97fb5b7-51eb-4073-92a7-32a3a38936da" alt="결제 승인 성공, Toss 결과 미반환 시나리오">
        </p>
            </details>
      <details>
           <summary><b>결제 승인에 실패했지만, Toss로부터 결과가 반환되지 않은 경우</b></summary>
        <p>
          <img src="https://github.com/user-attachments/assets/fa73201f-d965-40d4-a0fd-7217a5ba360b" alt="결제 승인 실패, Toss 결과 미반환 시나리오">
        </p>
          </details>
<details>
           <summary><b>결제 성공 여부도 모르고, Toss로부터 결과 또한 반환되지 않은 경우</b></summary>
        <p>
        <p>
          <img src="https://github.com/user-attachments/assets/b9404fb5-21d3-4c27-966b-985d14601a9e" alt="결제 성공 여부 불명, Toss 결과 미반환 시나리오">
        </p>
    </details>
    </ul>
  </div>
</details>


### 🌟 트러블슈팅

<details>
  <summary><b>[ SNS / SQS ] 결제 시스템 구조 비동기화 - 동기 방식의 문제점 개선</b></summary>
  <div markdown="1">
    <h3>결제 시스템 구조 비동기화 - 동기 방식의 문제점 개선</h3>
    <hr>
    <b>🚧 기존</b>
    <ul>
        <details>
           <summary><b>동기 방식의 흐름도</b></summary>
        <p>
          <img src="https://github.com/user-attachments/assets/1573a698-5ea0-4c7a-beb4-2752f4a4e95b" alt="동기 방식 흐름도">
        </p>
        </details>
      </li>
        <details>
           <summary><b>상세 흐름 설명</b></summary>
        <ul>
          <li>사용자가 티켓 예매를 할 경우, ‘결제 대기’ 상태의 티켓 Entity를 생성.</li>
          <li>추후 검증을 위해 orderId (주문 번호), amount (가격) 정보를 너굴페이 서버에 전송 & 저장.</li>
          <li>Toss HTML을 통해 사용자로부터 결제 정보를 입력 받고 paymentKey를 생성.</li>
          <li>orderId, amount, paymentKey를 결제 승인을 위해 너굴페이 서버에 전송.</li>
          <li>orderId와 amount를 통해 1차 위변조 검증을 진행.</li>
          <li>너굴페이 서버에서 Toss API를 호출해 결제 승인 요청 진행.</li>
          <li>Toss 결제 승인 결과 정보를 너굴티켓에게 반환.</li>
          <li>너굴티켓에서 반환 받은 값을 토대로 후 처리 작업을 진행:
            <ul>
              <li>결제 성공시: 티켓 상태를 결제 대기 → 예약 완료 상태로 변경.</li>
              <li>결제 실패시: 티켓 상태를 결제 대기 → 취소 상태로 변경.</li>
            </ul>
          </li>
        </ul>
        </details>
      </li>
    </ul>
    <b>🏁 문제사항</b>
    <ul>
      <li>너굴티켓(예매서버) → 너굴페이(결제서버) API 호출 구조로 통신하여, Auto Scaling이 적용될 경우 서버 주소를 다시 가져와야 하는 문제가 발생.</li>
      <li>Toss API에 보낸 결제 승인 요청 및 결제 정보 확인 요청 시 응답이 없을 경우, 보상 처리에 어려움이 있었음.
        <ul>
          <li>응답이 없을 경우, 즉시 Toss API에 확인 요청 & 재요청을 보내야 했음.</li>
          <li>서버 문제가 일시적으로 발생했을 경우, 일정 시간 뒤 Toss API 재호출 필요.</li>
        </ul>
      </li>
    </ul>
    <b>💡 의사결정</b>
    <ul>
      <li>SNS / SQS와 같은 메시징 서비스를 사용하여 비동기 구조로 변경:
        <ul>
          <li>현재 상황에 맞게 SNS에 메시지를 게시.</li>
          <li>Ticket / Payment라는 Topic을 각각 구독하고 있는 SQS로부터 메시지를 Polling한 뒤 처리.</li>
        </ul>
      </li>
    </ul>
      <details>
      <summary>흐름도(비동기)</summary>
        <p>
          <img src="https://github.com/user-attachments/assets/99f0fecb-cda0-40ae-b4cb-148779f70341" alt="비동기 방식 흐름도">
        </p>
        </details>
  </div>
</details>


<details>
  <summary><b>[ DB 반정규화 ] ERD 구조 변경</b></summary>
  <div markdown="1">
    <h3>ERD 구조 변경</h3>
    <hr>
    <b>🏁 문제사항</b>
    <ul>
      <li>좌석과 티켓 데이터를 조회할 때 잦은 JOIN이 발생할 것이 예상됨. 특히 이벤트별로 티켓을 조회할 때 무수히 많은 JOIN이 발생.</li>
      <li>잦은 연산: 성능 저하와 응답시간 증가가 발생할 우려 있음.</li>
      <li>ERD 수정 필요성</li>
    </ul>
    <p>
      <img width="1263" alt="ERD 문제" src="https://github.com/user-attachments/assets/320dcc9b-3f46-4627-9028-50b2c30a0fa6">
    </p>
    <b>해결 방안</b>
    <ul>
      <li>현재 구조에서는 저장공간의 비용보다 조회 시 성능 문제(속도 및 부하)를 줄이는 것이 더 중요하다는 의견이 모임.</li>
      <li>정규화가 아닌 반정규화 진행</li>
      <ul>
        <li>티켓 테이블에서 직접 이벤트 ID를 가지도록 FK 추가.</li>
        <li>좌석 테이블에서 좌석 카테고리 데이터를 가지도록 병합.</li>
      </ul>
    </ul>
    <b>의견 조율</b>
    <ul>
      <li>현재 티켓 조회가 가장 잦을 것이라고 예상되고, 그에 따른 부하도 높을 것이 명확하므로 티켓 테이블에 이벤트 ID 추가가 필요.</li>
      <li>티켓 예약 시 결제가 필수이며, 결제 테이블에 작성할 가격 정보를 가져오기 위해 티켓 -> 좌석 -> 티켓 카테고리 순으로 데이터를 가져와야 하므로, 티켓 카테고리 데이터를 좌석 테이블에 병합해야 함.</li>
    </ul>
    <b>💡 의사결정</b>
    <ul>
      <li>티켓 테이블에 이벤트 ID 추가.</li>
      <li>티켓 카테고리 테이블을 좌석 테이블에 병합.</li>
    </ul>
    <p>
      <img width="866" alt="반정규화된 ERD" src="https://github.com/user-attachments/assets/0a4d5eab-0373-46ec-8cb0-64ffb562fc85">
    </p>
  </div>
</details>



<details>
  <summary><b>[ OpenSearch 도입 ] 공연 키워드 검색 최적화</b></summary>
  <div markdown="1">
    <h3>공연 키워드 검색 최적화</h3>
    <hr>
    <b>🏁 문제 사항</b>
    <ul>
      <li>초기에는 일반 쿼리를 통해 공연 키워드 검색을 구현했으나, 데이터량 증가로 인해 조회 속도가 저하되는 문제가 발생.</li>
      <li>특정 키워드 검색 시 응답 지연이 발생하며 사용자 경험에 부정적인 영향을 미쳤음.</li>
    </ul>
    <b>✅ 선택지</b>
    <ul>
      <li><b>MySQL 인덱싱</b>
        <ul>
          <li>장점: 기존 RDBMS를 활용해 추가 기술 도입 없이 개선 가능.</li>
          <li>한계: 키워드 검색에 최적화되지 않아 데이터량 증가 시 성능이 급격히 저하되고, 복잡한 검색 기능 구현이 어려움.</li>
        </ul>
      </li>
      <li><b>Elasticsearch</b>
        <ul>
          <li>대규모 데이터에서 빠른 검색 속도 제공.</li>
          <li>자동완성(N-gram), 동의어 검색, 유사도 기반 검색 등 고급 검색 기능 제공.</li>
          <li>확장성과 안정성을 갖춰 데이터 증가에도 일관된 성능 유지 가능.</li>
        </ul>
      </li>
    </ul>
    <b>💡 ElasticSearch 도입 이유</b>
    <ul>
      <li><b>검색 속도 개선</b>
        <ul>
          <li>MySQL 인덱싱으로 해결되지 않는 검색 속도 저하 문제를 근본적으로 해결하기 위해 Elasticsearch 도입.</li>
          <li>대규모 데이터를 효율적으로 처리하며, 빠른 검색 속도로 대량 트래픽에서도 안정적인 성능 제공.</li>
        </ul>
      </li>
      <li><b>사용자 검색 경험 향상</b>
        <ul>
          <li>Nori 토크나이저와 N-gram 설정을 통해 한글 특성을 반영한 정교한 검색 가능.</li>
          <li>동의어 검색(예: ‘축제’ → ‘페스티벌’)과 자동완성 기능으로 사용자 편의성을 극대화.</li>
          <li>검색 키워드와 데이터 간 연관성을 평가해 정확도와 유사도를 기반으로 검색 품질 향상.</li>
        </ul>
      </li>
      <li><b>확장성과 유지보수 편의성</b>
        <ul>
          <li>Elasticsearch는 데이터를 인덱싱하고 분산 처리하는 데 최적화되어 데이터량 증가에도 안정적인 검색 성능 유지.</li>
          <li>기존 시스템과의 호환성도 뛰어나고, 다양한 검색 기능 추가가 용이해 미래 확장성 측면에서도 유리.</li>
        </ul>
      </li>
    </ul>
    <b>📊 성능 테스트 결과 비교</b>
    <p>
      <img width="500" alt="성능 테스트" src="https://github.com/user-attachments/assets/4cfd2f6d-189b-42d2-b36c-b3970ca0ead2">
    </p>
    <ul>
      <li><b>일반 쿼리</b>: 총 소요 시간 1분 34초, 평균 TPS 531.0, 쓰레드 평균 소요 시간 9.27초</li>
      <li><b>MySQL 인덱싱 적용</b>: 총 소요 시간 1분 31초, 평균 TPS 548.1, 쓰레드 평균 소요 시간 9.00초</li>
      <li><b>Elasticsearch 적용</b>: 총 소요 시간 5초, 평균 TPS 10,620.2, 쓰레드 평균 소요 시간 0.35초</li>
        <li><b>Elasticsearch 적용 후, 평균 TPS는 약 19배 이상, 쓰레드 평균 소요시간은 약 25배 이상 성능 대폭 향상됨.</b></li>
    </ul>
    <p></p>
    <b>[📋 결론]</b>
    <b>ElasticSearch 인덱스 색인 설계</b>
    <h4>기본 정보</h4>
    <table>
      <thead>
        <tr>
          <th>항목</th>
          <th>값</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>인덱스 이름</td>
          <td>events_v1</td>
        </tr>
        <tr>
          <td>인덱스 설정</td>
          <td>max_ngram_diff: 8</td>
        </tr>
      </tbody>
    </table>
    <h4>분석기 구성</h4>
    <table>
      <thead>
        <tr>
          <th>구성 요소</th>
          <th>이름</th>
          <th>세부 설정</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>토크나이저</td>
          <td>my_nori_tokenizer</td>
          <td>`type: nori_tokenizer`, `decompound_mode: mixed`, `user_dictionary: event_dictionary.txt`</td>
        </tr>
        <tr>
          <td></td>
          <td>ngram_tokenizer</td>
          <td>`type: ngram`, `min_gram: 2`, `max_gram: 10`, `token_chars: [letter, digit]`</td>
        </tr>
        <tr>
          <td>필터</td>
          <td>my_stopwords</td>
          <td>`type: stop`, `stopwords_path: event_stopword.txt`</td>
        </tr>
        <tr>
          <td></td>
          <td>my_synonym_filter</td>
          <td>`type: synonym_graph`, `synonyms_path: event_synonym.txt`</td>
        </tr>
        <tr>
          <td>분석기</td>
          <td>my_nori_analyzer</td>
          <td>`tokenizer: my_nori_tokenizer`, `filter: [lowercase, my_stopwords]`</td>
        </tr>
        <tr>
          <td></td>
          <td>autocomplete_analyzer</td>
          <td>`type: custom`, `tokenizer: ngram_tokenizer`, `filter: [lowercase, my_synonym_filter]`</td>
        </tr>
      </tbody>
    </table>
    <h4>매핑 구조</h4>
    <table>
      <thead>
        <tr>
          <th>필드 이름</th>
          <th>데이터 타입</th>
          <th>분석기 설정</th>
          <th>기타 설정</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>eventId</td>
          <td>long</td>
          <td>없음</td>
          <td>없음</td>
        </tr>
        <tr>
          <td>title</td>
          <td>text</td>
          <td>`analyzer: autocomplete_analyzer`, `search_analyzer: my_nori_analyzer`</td>
          <td>없음</td>
        </tr>
        <tr>
          <td>title_initials</td>
          <td>keyword</td>
          <td>없음</td>
          <td>없음</td>
        </tr>
        <tr>
          <td>category</td>
          <td>text</td>
          <td>`analyzer: autocomplete_analyzer`, `search_analyzer: my_nori_analyzer`</td>
          <td>없음</td>
        </tr>
        <tr>
          <td>description</td>
          <td>text</td>
          <td>`analyzer: autocomplete_analyzer`, `search_analyzer: my_nori_analyzer`</td>
          <td>없음</td>
        </tr>
        <tr>
          <td>place</td>
          <td>text</td>
          <td>`analyzer: autocomplete_analyzer`, `search_analyzer: my_nori_analyzer`</td>
          <td>없음</td>
        </tr>
        <tr>
          <td>startDate</td>
          <td>date</td>
          <td>없음</td>
          <td>`format: yyyy-MM-dd`</td>
        </tr>
        <tr>
          <td>endDate</td>
          <td>date</td>
          <td>없음</td>
          <td>`format: yyyy-MM-dd`</td>
        </tr>
        <tr>
          <td>rating</td>
          <td>float</td>
          <td>없음</td>
          <td>없음</td>
        </tr>
        <tr>
          <td>bookAble</td>
          <td>boolean</td>
          <td>없음</td>
          <td>없음</td>
        </tr>
      </tbody>
    </table>
    <h4>인덱스 버전 관리 및 운영</h4>
    <table>
      <thead>
        <tr>
          <th>관리 항목</th>
          <th>설명</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>인덱스 버전 관리</td>
          <td>- 인덱스를 버전별(`events_v1`, `events_v2`)로 생성<br>- 새 버전 생성 시, 기존 데이터를 안전하게 이전 후 교체</td>
        </tr>
        <tr>
          <td>알리아스 설정</td>
          <td>- `events_current` 라는 알리아스를 사용해 항상 최신 버전의 인덱스를 참조<br>- `is_write_index: true` 를 활용해 쓰기 작업 관리</td>
        </tr>
        <tr>
          <td>재색인(Reindexing)</td>
          <td>- 새로운 색인 생성 후 데이터를 기존 인덱스에서 새 인덱스로 복사<br>- `POST /_reindex` 사용</td>
        </tr>
        <tr>
          <td>교체 프로세스</td>
          <td>1. 새 인덱스 생성<br>2. 데이터 재색인<br>3. 알리아스를 새 인덱스로 전환<br>4. 기존 인덱스 삭제</td>
        </tr>
      </tbody>
    </table>
    <p>
      Elasticsearch 도입으로 기존 MySQL 기반 검색에서 발생한 성능 저하 문제를 해결하고, 검색 속도와 사용자 경험을 대폭 개선했다.
    </p>
    <p>
      성능 테스트 결과, Elasticsearch는 <b>TPS 약 19배 증가</b>, <b>응답 시간 96% 단축</b>을 기록하며, 데이터 증가에도 안정적인 검색 성능을 유지했다.
    </p>
    <p>
      Elasticsearch는 속도 개선뿐 아니라 자동완성, 동의어 검색, 유사도 기반 검색 등 고급 검색 기능을 통해 사용자 만족도를 높이고, 데이터 증가에도 안정적인 검색 성능을 유지한다.
    </p>
    <b>✔️ 최종 배포 변경</b>
    <ul>
      <li>Elasticsearch를 활용한 테스트 단계 이후, 실제 배포 환경에서는 AWS OpenSearch로 마이그레이션하여 운영했다.</li>
      <li>OpenSearch는 Elasticsearch와의 호환성을 유지하며, 관리형 서비스로 운영 편의성과 안정성을 제공한다. 이를 통해 성능 테스트와 동일한 검색 속도 및 기능을 유지하면서, 운영 비용 절감과 관리 부담을 줄이는 효과를 얻었다.</li>
    </ul>
  </div>
</details>

    
<details>
  <summary><b>[ gRPC 도입 ] 결제 정보 반환 API의 통신 성능 개선</b></summary>
  <div markdown="1">
    <h3>결제 정보 반환 API의 통신 성능 개선</h3>
    <b>[🏁문제 상황]</b>
    <p>
      기존에는 REST API를 통해 티켓 서버가 결제 서버에서 결제 정보를 반환받는 기능을 구현했다. 하지만 REST API는 HTTP/1.1을 기반으로 요청-응답 사이에 별도의 연결을 반복적으로 생성해야 하며, 이는 다수의 요청이 몰릴 경우 처리 성능 저하로 이어졌다. 또한, 결제 정보에는 다양한 세부 데이터를 포함해야 하는데 REST API의 JSON 직렬화 방식은 응답 속도가 저하되는 문제점이 발생했다.
    </p>
    <b>[✅ 선택지]</b>
    <ol>
      <li>
        <b>메시지 큐</b>
        <ul>
          <li>비동기 처리와 서버 부하 완화에는 적합하지만, 실시간 요청-응답이 필요한 작업에는 한계가 있다.</li>
          <li>메시지 큐는 데이터를 가져오는 과정이 여러 단계를 거치기 때문에, 단순히 결제 정보를 즉시 반환하는 요청에는 적합하지 않다.</li>
        </ul>
      </li>
      <li>
        <b>gRPC</b>
        <ul>
          <li>Protobuf를 사용한 효율적인 데이터 직렬화로 데이터 크기를 줄여 전송 속도를 개선할 수 있다.</li>
          <li>HTTP/2 기반으로 실시간 요청-응답 성능이 뛰어나고, 대규모 요청 처리에도 안정적이다.</li>
          <li>동기적 요청-응답이 필요한 상황에서 REST API 대비 효율적이며 메시지큐보다 응답 속도가 빠르다.</li>
          <li>한계: 브라우저에서 직접 호출하기 어렵고, 사람이 읽기 어려운 바이너리 형식이므로 디버깅할 때 다소 어렵다.</li>
        </ul>
      </li>
    </ol>
    <b>[💡 gRPC 도입이유]</b>
    <ul>
      <li>현재 결제 서버와 티켓 서버는 물리적으로 구분되어 있지만, 이 API는 다른 서버와 통신하면서도 단일 서버급의 빠른 처리를 통해 유저 경험을 향상시키고 싶었다.</li>
      <li>gRPC는 Protobuf 직렬화로 방대한 결제 데이터를 빠르게 전송할 수 있으며, HTTP/2 기반의 멀티플렉싱으로 동기적 요청-응답 속도를 극대화한다.</li>
      <li>성능과 사용성을 모두 고려했을 때, gRPC는 단순히 대안 중 하나가 아니라 이 상황에서 가장 적합한 상위 기술이라고 생각했다.</li>
      <li>
        <b>한계를 극복한 방안</b>
        <ul>
          <li>Spring MVC 컨트롤러를 통해 HTTP 요청을 받고 내부적으로 gRPC 클라이언트를 호출하는 방식으로, 기존 REST API 기반 클라이언트와의 호환성을 유지하며 gRPC 도입의 장점을 극대화했다.</li>
        </ul>
      </li>
    </ul>
    <b>[📊 성능테스트 결과 비교]</b>
    <img src="https://github.com/user-attachments/assets/15f23e77-8d1c-44e8-8d43-498ff3a6de5f" alt="성능 테스트 결과">
    <table>
      <thead>
        <tr>
          <th>프로토콜</th>
          <th>요청 수</th>
          <th>총 소요 시간(s)</th>
          <th>평균응답시간(ms)</th>
          <th>평균 TPS(/sec)</th>
          <th>에러율(%)</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>REST</td>
          <td>10000</td>
          <td>393</td>
          <td>3843</td>
          <td>25.5</td>
          <td>0</td>
        </tr>
        <tr>
          <td>gRPC</td>
          <td>10000</td>
          <td>180</td>
          <td>1770</td>
          <td>55.5</td>
          <td>0</td>
        </tr>
        <tr>
          <td>REST</td>
          <td>100000</td>
          <td>3044</td>
          <td>15044</td>
          <td>32.8</td>
          <td>0</td>
        </tr>
        <tr>
          <td>gRPC</td>
          <td>100000</td>
          <td>990</td>
          <td>4902</td>
          <td>101.1</td>
          <td>0</td>
        </tr>
      </tbody>
    </table>
    <p>
      - 요청 수 1만건 → gRPC가 REST 대비 <b>평균 응답시간 약 2.2배, 평균 TPS 약 2배 높은 성능</b>
    </p>
    <p>
      - 요청 수 10만건 → gRPC가 REST 대비 <b>평균 응답시간 약 3배, 평균 TPS 약 3배 높은 성능</b>
    </p>
    <b>[📋 결론]</b>
    <ul>
      <li>gRPC를 통해 방대한 데이터를 더 빠르게 제공하면서도, 동기적 요청-응답 처리로 사용자 경험을 대폭 개선할 수 있었다.</li>
      <li>요청 수가 증가할수록 REST와 gRPC의 성능 차이가 더 커졌다.</li>
      <li>대규모 요청 처리 시 REST는 병목 현상이 발생하지만, gRPC는 높은 Throughput을 유지하며 안정적인 성능을 제공한다.</li>
      <li>대규모 트래픽 처리나 실시간 성능이 중요한 시스템에서는 gRPC를 사용하는 것이 더 적합하다.</li>
    </ul>
  </div>
</details>

<details>
<summary><b>[ SSE 알림 도입 ] 실시간 좌석 상태 알림 기능 설계 및 구현</b></summary>
<h3>실시간 좌석 상태 알림 기능 설계 및 구현</h3>
<hr>
<h4>🏁 문제 상황</h4>
<p>
기존 공연 티켓 예매 서비스에서는 사용자가 새로고침을 해야만 좌석 예매 상태가 업데이트되어 표시되었다. 하지만 여러 사용자가 동시에 좌석을 선택하거나 취소하는 상황에서 변경 사항이 즉시 반영되지 않으면, 이미 예매된 좌석을 다시 선택하는 문제가 빈번히 발생할 가능성이 높다. 이는 서비스의 신뢰도와 사용자 경험을 크게 저하시킬 수 있다.
</p>
<p>
이러한 문제를 해결하기 위해, 좌석 상태 변경 이벤트를 서버에서 감지하여 실시간으로 클라이언트에 전달하는 기능이 필요했다.
</p>
<img src="https://github.com/user-attachments/assets/95c79cc3-5748-4815-9c73-5c1a410d7541" alt="사용자 관점 예시 사진">
<h4>🏁 요구사항</h4>
<ul>
    <li>좌석 상태 변경 사항이 사용자 화면에 실시간으로 반영되어야 한다.</li>
    <li>서버 리소스를 효율적으로 사용해야 한다.</li>
    <li>다수의 사용자가 동시에 좌석 상태를 조회해도 안정적으로 작동해야 한다.</li>
    <li>브라우저 환경에서 구현이 간단하고 추가 설정이 최소화되어야 한다.</li>
</ul>
<h4>✅ 선택지</h4>
    <details>
<summary><b>1. Server-Sent Events</b></summary>
<ul>
    <li><strong>장점:</strong>
        <ul>
            <li>단방향 데이터 전송에 최적화되어 있다 → 클라이언트에 실시간으로 데이터를 전달하기 적합하다.</li>
            <li>브라우저에서 기본적으로 지원한다 → 추가 라이브러리 설치 없이 구현이 간단하다.</li>
            <li>HTTP 기반으로 작동한다 → 대부분의 네트워크 환경(방화벽, 프록시 등)에서도 안정적으로 작동한다.</li>
            <li>서버 리소스 소모가 낮다 → 다수의 사용자 연결을 효율적으로 처리할 수 있다.</li>
        </ul>
    </li>
    <li><strong>단점:</strong>
        <ul>
            <li>단방향 통신만 가능하므로 클라이언트가 서버로 데이터를 보낼 때는 별도의 API 호출이 필요하다.</li>
            <li>클라이언트 연결이 끊어졌을 때 재연결 로직을 추가로 구현해야 한다.</li>
        </ul>
    </li>
</ul>
</details>
<details>
<summary><b>2. WebSocket</b></summary>
<ul>
    <li><strong>장점:</strong>
        <ul>
            <li>양방향 통신을 지원하므로 클라이언트와 서버 간 데이터를 실시간으로 주고받을 수 있다.</li>
            <li>상태 변경 이벤트가 잦거나 상호작용이 많은 채팅이나 게임 등의 애플리케이션에 적합하다.</li>
            <li>연결이 유지되는 동안 요청 및 응답의 오버헤드가 없다.</li>
        </ul>
    </li>
    <li><strong>단점:</strong>
        <ul>
            <li>SSE에 비해 초기 구현과 유지보수가 복잡하다.</li>
            <li>방화벽이나 네트워크 설정에 따라 연결이 차단될 가능성이 있으며, 이를 처리하기 위한 추가 로직이 필요하다.</li>
            <li>클라이언트 수가 많아질수록 서버 리소스 소모가 증가하며, 연결 상태를 지속적으로 관리해야 한다.</li>
        </ul>
    </li>
</ul>
</details>
<details>
<summary><b>3. HTTP Polling</b></summary>
<ul>
    <li><strong>장점:</strong>
        <ul>
            <li>구현이 매우 간단하며 모든 브라우저에서 지원된다.</li>
            <li>HTTP 요청만으로 동작하므로 추가 프로토콜이나 설정 없이 빠르게 적용할 수 있다.</li>
        </ul>
    </li>
    <li><strong>단점:</strong>
        <ul>
            <li>실시간성이 낮고, 주기적으로 서버에 요청을 보내야 하므로 서버 부하와 네트워크 트래픽이 증가한다.</li>
            <li>사용자가 변경 사항을 즉시 확인하기 어려워 실시간 경험을 제공하기에는 부적합하다.</li>
        </ul>
    </li>
</ul>
</details>
<details>
<summary><b>4. Long Polling</b></summary>
<ul>
    <li><strong>장점:</strong>
        <ul>
            <li>HTTP Polling보다 실시간성이 높으며, 서버가 데이터가 준비되었을 때 클라이언트에 응답할 수 있다.</li>
            <li>기존 HTTP 기반이므로 대부분의 네트워크 환경에서 호환성이 높다.</li>
        </ul>
    </li>
    <li><strong>단점:</strong>
        <ul>
            <li>연결이 장시간 유지되므로 서버와 클라이언트 간 리소스 소모가 크다.</li>
            <li>요청이 완료될 때마다 새 연결을 생성하므로 SSE보다 리소스 효율성이 떨어진다.</li>
            <li>SSE나 WebSocket보다 구현 복잡도가 높다.</li>
        </ul>
    </li>
</ul>
</details>
    <details>
<summary><b>5. Push Notification</b></summary>
<ul>
    <li><strong>장점:</strong>
        <ul>
            <li>브라우저가 비활성화 상태에서도 알림을 전송할 수 있어, 사용자 참여율을 높이는 데 유용하다.</li>
            <li>서버가 클라이언트에 직접 데이터를 전달하므로 사용자가 특정 이벤트를 놓치지 않게 할 수 있다.</li>
        </ul>
    </li>
    <li><strong>단점:</strong>
        <ul>
            <li>브라우저 기반 실시간 UI 업데이트에는 적합하지 않으며, 화면 즉시 반영보다는 알림 목적에 적합하다.</li>
            <li>사용자 동의가 필요하며, 초기 설정과 인증 과정이 복잡하다.</li>
            <li>클라이언트 환경(예: 브라우저, 기기)에 따라 지원 여부가 달라질 수 있다.</li>
        </ul>
    </li>
</ul>
    </details>
<h4>💡 의사결정/사유</h4>
<ul>
    <li><strong>선택한 방법:</strong> Server-Sent Events (SSE)</li>
    <li><strong>선택 이유:</strong>
        <ol>
            <li>좌석 상태 변경 정보는 단방향 데이터 전송으로 충분하다 → 양방향 통신이 필요 없는 SSE가 적합하다.</li>
            <li>브라우저 기본 지원으로 클라이언트 구현이 간단하며, 추가 라이브러리 없이 빠르게 적용할 수 있다.</li>
            <li>HTTP 기반으로 작동하므로 대부분의 네트워크 환경에서 안정적으로 동작한다.</li>
            <li>구현 난이도가 낮으면서도 리소스를 효율적으로 사용해 다수의 사용자가 접속해도 서버 부하가 적다.</li>
            <li>Polling과 Long Polling은 실시간성이 낮거나 리소스 소모가 크며, WebSocket은 필요 이상의 복잡성을 요구한다. Push Notification은 요구사항과 맞지 않는다.</li>
        </ol>
    </li>
</ul>
<h4>📋 구현 결과</h4>
<p>
공연 예매 서비스의 백엔드에서 <strong>SSE(Server-Sent Events)</strong>를 활용해 좌석 상태를 실시간으로 업데이트하는 기능을 구현했다. 좌석 상태가 변경되면 서버는 클라이언트로 좌석 ID, 상태, 변경 시간을 전송하며, 클라이언트는 이를 즉시 화면에 반영한다.
</p>
</details>
<details>
<summary><b>[ Redis 분산 락 ] 분산 서버 환경에서 Redis 분산 락으로 동시성 제어</b></summary>
<h3>분산 서버 환경에서 Redis 분산 락으로 동시성 제어</h3>
<hr>
<h4>🏁 문제 상황 및 해결 방안</h4>
<ul>
    <li>티켓 예매, 경매 입찰, 티켓 양도에서 많은 트래픽이 몰렸을 때, 데이터 무결성이 깨지는 상황이었다. (초당 900건 이상의 요청이 몰릴 경우, 동일 티켓 중복 예매 또는 동일 가격 중복 입찰)</li>
    <li>동시성 제어를 통해 이러한 문제를 해결하기로 결정했다.</li>
</ul>
<h4>💡 비교 및 의사 결정</h4>
<p><strong>결론:</strong> Redis 분산 락 중에서도 Redisson의 공정 락(Fair Lock)을 사용하여 구현했다. <strong>Redisson 공정 락은 선입선출 보장, 고성능 처리, 락 누수 방지</strong>가 필요한 시스템에 적합하며, 데이터베이스 락이나 일반 분산 락과 비교했을 때, 효율성과 공정성을 동시에 확보할 수 있는 최적의 선택으로 판단했다.</p>
<h5>데이터베이스 락과 Redis 분산 락 비교</h5>
<ul>
    <li><strong>Redis는 메모리 기반, 데이터베이스 락은 디스크 기반이므로, Redis가 훨씬 고성능을 낼 수 있다.</strong></li>
    <li><strong>TTL 지원으로 락 누수 방지:</strong>
        <ul>
            <li>데이터베이스 락은 락을 해제하지 못하거나 트랜잭션이 비정상적으로 종료되었을 때, 락이 계속 유지되는 <strong>락 누수</strong> 문제가 발생할 수 있다.</li>
            <li>Redis 락은 TTL을 설정하여 락을 자동으로 해제할 수 있으므로, 락 관리가 더 안정적이다.</li>
        </ul>
    </li>
    <li><strong>의사결정 사유:</strong> 많은 트래픽이 몰리는 상황에서도 사용자 경험을 극대화하기 위해 고성능인 Redis를 선택했다.</li>
</ul>
<h5>Redis 일반 락과 Redisson 공정 락 비교</h5>
<ul>
    <li><strong>Redis 일반 락은 단순 락 구현에 적합:</strong>
        <ul>
            <li>SETNX와 EXPIRE로 구성된 기본적인 락으로, 특정 자원에 대한 동시 접근을 막는 데 사용된다.</li>
            <li>락 요청의 순서를 보장하지 않으며, 스타베이션 문제가 발생할 가능성이 있다.</li>
        </ul>
    </li>
    <li><strong>Redisson 공정 락은 선입선출 방식으로 락 요청 순서를 보장:</strong>
        <ul>
            <li>스타베이션 문제를 방지하며, 순차 처리가 중요한 경우에 적합하다.</li>
        </ul>
    </li>
    <li><strong>의사결정 사유:</strong> 락 요청의 순서를 보장해야 하기 때문에 Redisson 공정 락을 선택했다.</li>
</ul>
<h4>📋 주요 로직</h4>
<ul>
    <li><strong>락 생성 및 관리:</strong> Redis를 활용해 고유 키를 생성하고 자원을 잠금 처리하며, 적절한 TTL을 설정해 락 누수를 방지.</li>
    <li><strong>안전한 락 해제:</strong> 락 해제 시, 고유 값을 검증하여 다른 요청이 실수로 락을 해제하지 못하도록 처리해 데이터 무결성을 보장.</li>
</ul>
</details>

<details>
<summary><b>[ Redis 비관적, 낙관적 락 ] 동시성 문제를 해결하는 두 가지 접근법</b></summary>
<h3>동시성 문제를 해결하는 두 가지 접근법</h3>
<hr>
<h4>🏁 문제 상황</h4>
<ul>
    <li><strong>티켓 환불</strong>
        <ul>
            <li>중복 환불이나 잘못된 데이터 업데이트가 발생할 가능성이 존재한다.</li>
            <li>동시 접근이 낮더라도, 데이터 충돌 방지와 무결성 유지를 위한 관리가 필요하다.</li>
        </ul>
    </li>
    <li><strong>경매 입찰</strong>
        <ul>
            <li>다수의 사용자가 동시에 입찰 요청을 보내면서 데이터 충돌이 빈번히 발생한다.</li>
            <li>분산 서버 간의 요청 경합과 데이터베이스 트랜잭션 충돌로 인해 데이터 무결성과 일관성에 문제가 생길 수 있다.</li>
            <li>단일 락만으로는 분산 환경과 데이터베이스 내부 충돌을 모두 방지하기 어렵다는 한계가 존재한다.</li>
        </ul>
    </li>
</ul>
<h4>📋 구현한 기능</h4>
<ul>
    <li><strong>티켓 환불</strong>
        <ul>
            <li>낙관적 락 사용. 충돌 발생 시에만 처리하며, 트랜잭션 범위를 최소화해 데이터베이스 자원 소모를 줄인다.</li>
        </ul>
    </li>
    <li><strong>경매 입찰</strong>
        <ul>
            <li>비관적 락 + 분산 락으로 이중 보호를 적용한다.</li>
            <li>분산 락은 서버 간 충돌 방지, 비관적 락은 데이터베이스 트랜잭션 충돌을 차단한다.</li>
        </ul>
    </li>
</ul>
<h4>⛰️ 배경</h4>
<ul>
    <li><strong>티켓 환불</strong>
        <ul>
            <li>동시성이 낮아 낙관적 락으로 충돌만 확인하며 처리. 리소스 절약이 가능하다.</li>
        </ul>
    </li>
    <li><strong>경매 입찰</strong>
        <ul>
            <li>높은 동시성 환경으로 단일 락만으로는 불충분하다.</li>
            <li>분산 락으로 서버 간 경쟁 제어, 비관적 락으로 데이터 무결성을 보장한다.</li>
        </ul>
    </li>
</ul>
<h4>💡 의사결정/사유</h4>
<ul>
    <li><strong>티켓 환불</strong>
        <ul>
            <li>낮은 동시성 환경에서 성능 최적화를 위해 낙관적 락을 채택한다.</li>
        </ul>
    </li>
    <li><strong>경매 입찰</strong>
        <ul>
            <li>이중 보호를 통해 분산 환경과 데이터베이스 레벨 모두에서 충돌을 방지한다.</li>
            <li>데이터 무결성과 안정성을 최우선으로 고려한다.</li>
        </ul>
    </li>
</ul>
</details>


<details>
<summary><b>[ Google OTP 보안 도입 ] 공연 생성 시 인증 보안 강화</b></summary>
<h3>공연 생성 시 인증 보안 강화</h3>
<hr>
<h4>🏁 문제 상황</h4>
<ul>
    <li>OTP 인증이 없으면 사용자에게 신뢰감을 주지 못할 가능성이 있었다. 사이트의 보안이 취약하다고 느낀 사용자가 플랫폼 이용을 꺼릴 수 있었다.</li>
    <li>기존 인증 방식(비밀번호 및 이메일 인증)만으로는 보안성 부족으로 인한 문제가 발생할 가능성이 있었다.</li>
</ul>
<h4>📋 구현한 기능</h4>
<ul>
    <li><strong>Google OTP 기반 이중 인증</strong>
        <ul>
            <li>QR 코드를 생성하고 OTP 앱에 등록하는 기능을 구현했다.</li>
            <li>OTP 번호를 통한 인증을 진행하며, 실패 횟수를 초과할 경우 계정을 잠금 처리했다.</li>
            <li>이메일로 잠금 해제 코드를 발송하고, 이를 통해 계정을 잠금 해제하는 기능도 구현했다.</li>
        </ul>
    </li>
    <li><strong>Redis 활용</strong>
        <ul>
            <li>인증 상태와 잠금 상태를 Redis에 저장했으며, TTL을 설정하여 데이터가 자동으로 만료되도록 했다.</li>
        </ul>
    </li>
</ul>
<h4>⛰️ 배경</h4>
<ul>
    <li><strong>보안 강화</strong>
        <ul>
            <li>기존 비밀번호와 이메일 인증만으로는 한계가 있었고, 이를 보완하기 위해 OTP를 도입했다.</li>
            <li>유효기간이 있는 일회용 비밀번호를 사용함으로써 해킹 위험을 줄였다.</li>
        </ul>
    </li>
    <li><strong>사용자 경험 개선</strong>
        <ul>
            <li>QR 코드를 사용해 간편하게 OTP 앱에 등록할 수 있도록 했다.</li>
            <li>Redis를 통해 빠르고 실시간으로 인증 상태를 관리하며 성능을 최적화했다.</li>
        </ul>
    </li>
</ul>
<h4>💡 의사결정/사유</h4>
<ul>
    <li><strong>OTP 도입</strong>
        <ul>
            <li>정기적으로 갱신되는 일회용 비밀번호를 통해 보안을 강화했다.</li>
            <li>해킹 위험을 줄이고 사용자 계정을 안전하게 보호했다.</li>
        </ul>
    </li>
    <li><strong>Redis 활용</strong>
        <ul>
            <li>빠른 인증 상태 관리를 가능하게 했으며, TTL 설정을 통해 불필요한 데이터를 자동으로 삭제하도록 했다.</li>
        </ul>
    </li>
    <li><strong>이중 인증 설계</strong>
        <ul>
            <li>보안성과 사용자 경험을 모두 고려하여 시스템을 설계했다.</li>
            <li>실패 처리와 계정 잠금 해제 시나리오까지 포함해 완성도 높은 기능을 제공했다.</li>
        </ul>
    </li>
</ul>
</details>

<details>
<summary><b>[ AWS CloudFront ] 이미지 로딩 속도 및 시스템 성능 최적화</b></summary>
<h3>AWS CloudFront - 이미지 로딩 속도 및 시스템 성능 최적화</h3>
    <hr>
<h4>🏁 문제 상황</h4>
<ul>
  <li>이미지 데이터를 직접 S3에서 불러올 경우, 네트워크 지연 및 트래픽 처리 병목 현상이 발생했다.</li>
  <li>특히 대용량 데이터 요청이 많아지면 시스템 성능이 저하되고, 사용자 경험에 부정적 영향을 미친다고 판단했다.</li>
</ul>
<h4>💡 CloudFront 도입 이유</h4>
<ol>
  <li>
    <strong>콘텐츠 전송 속도 향상</strong>
    <ul>
      <li>AWS CloudFront는 글로벌 엣지 로케이션을 통해 데이터를 캐싱하여 사용자 가까운 곳에서 데이터를 전송할 수 있도록 한다.</li>
      <li>이를 통해 네트워크 지연을 최소화하고, 빠른 응답 속도를 제공할 수 있다.</li>
    </ul>
  </li>
  <li>
    <strong>비용 효율성</strong>
    <ul>
      <li>S3로 직접 요청하는 횟수를 줄여 데이터 전송 비용을 절감할 수 있다.</li>
      <li>자주 요청되는 데이터는 캐시에서 제공되므로 S3 I/O 비용 역시 감소한다.</li>
    </ul>
  </li>
  <li>
    <strong>확장성</strong>
    <ul>
      <li>고트래픽 환경에서도 CloudFront는 자동으로 확장되어 서비스 안정성을 유지할 수 있다.</li>
      <li>대규모 트래픽이 발생하더라도 성능 저하 없이 안정적으로 대응할 수 있다.</li>
    </ul>
  </li>
</ol>
<h4>📊 성능 테스트 결과 비교</h4>
    <br>
<img src="https://github.com/user-attachments/assets/8f273529-6e82-4646-8fd9-2da22fa349ea" alt="CloudFront 성능 비교">
<table>
  <thead>
    <tr>
      <th></th>
      <th>요청 수</th>
      <th>파일크기</th>
      <th>평균응답시간(ms)</th>
      <th>평균 TPS(/sec)</th>
      <th>에러율(%)</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>S3 직접 접근</td>
      <td>100</td>
      <td>4.1MB</td>
      <td>918</td>
      <td>7.8</td>
      <td>0</td>
    </tr>
    <tr>
      <td>CloudFront</td>
      <td>100</td>
      <td>4.1MB</td>
      <td>694</td>
      <td>10.4</td>
      <td>0</td>
    </tr>
    <tr>
      <td>S3 직접 접근</td>
      <td>1000</td>
      <td>500KB</td>
      <td>801</td>
      <td>59.4</td>
      <td>0</td>
    </tr>
    <tr>
      <td>CloudFront</td>
      <td>1000</td>
      <td>500KB</td>
      <td>679</td>
      <td>107.6</td>
      <td>0</td>
    </tr>
  </tbody>
</table>
<h5>4.1MB 파일 테스트 (100건 요청)</h5>
<ul>
  <li>CloudFront</li>
  <ul>
    <li>평균 응답 시간이 S3(918ms)보다 약 24.4% 더 빠름</li>
    <li>평균 TPS도 S3(7.8 TPS)보다 약 33.3% 더 높음</li>
    <li>대용량 파일에서도 CloudFront의 성능이 우수</li>
  </ul>
</ul>
<h5>500KB 파일 테스트 (1000건 요청)</h5>
<ul>
  <li>CloudFront</li>
  <ul>
    <li>평균 응답 시간이 S3(801ms)보다 약 15.2% 더 빠름</li>
    <li>평균 TPS도 S3(59.4 TPS)보다 약 81.2% 더 높음</li>
    <li>소용량 파일에서도 CloudFront의 성능 차이가 더욱 두드러짐</li>
  </ul>
</ul>
<h4>📋 결론</h4>
<ul>
  <li>파일 크기에 관계없이 CloudFront는 S3에 직접 접근하는 것 대비 더 빠른 응답 시간과 높은 TPS를 제공하였다.</li>
  <li>이는 엣지 서버 캐싱 및 효율적인 네트워크 처리가 주된 이유라고 판단된다.</li>
  <li>CloudFront는 파일 크기와 상관없이 성능이 S3 직접 접근보다 더 뛰어나며, 대규모 트래픽에서도 확장성이 훨씬 뛰어나다.</li>
  <li>성능, 안정성, 확장성을 고려할 때 CloudFront 사용이 명확히 유리하다.</li>
</ul>
</details>

<br>
    

## 7\. 역할 분담 및 협업 방식

**\[ 역할 분담 \]**

<table>
  <thead>
    <tr>
      <th>팀원명</th>
      <th>포지션</th>
      <th>담당</th>
      <th>깃허브 링크</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>이주호</td>
      <td>❤️ <strong>Leader</strong></td>
      <td>
        ▶ 결제 시스템<br>
        &emsp;- Toss API를 활용한 결제 인증 서비스 구현<br>
        &emsp;- 결제 정보 검증 및 저장<br>
        &emsp;- SNS, SQS를 활용한 결제 트랜잭션 보장<br><br>
        ▶ 동시성 제어<br>
        &emsp;- Redis 분산 락을 활용한 분산 환경 동시성 제어
      </td>
      <td><a href="https://github.com/jhgit95">https://github.com/jhgit95</a></td>
    </tr>
    <tr>
      <td>한지은</td>
      <td>🧡 <strong>Sub-Leader</strong></td>
      <td>
        ▶ CI/CD<br>
        &emsp;- 프로젝트 인프라 설계 & 구축 & 배포 (AWS ECS fargate)<br>
        &emsp;- Github Action을 활용한 workflow 자동화<br><br>
        ▶ 인증/인가<br>
        &emsp;- Spring Security를 활용한 사용자 인가<br>
        &emsp;- Jwt를 활용한 사용자 인증<br><br>
        ▶ 좌석 예매 실시간 알림<br>
        &emsp;- SSE를 사용한 실시간 좌석 정보 변경 알림<br>
        &emsp;- SQS로 서버간 좌석정보 변경 메시징
      </td>
      <td><a href="https://github.com/jay1864">https://github.com/jay1864</a></td>
    </tr>
    <tr>
      <td>김건우</td>
      <td>💛 <strong>Member</strong></td>
      <td>
        ▶ 결제 시스템<br>
        &emsp;- Toss API를 활용한 결제 시스템 구축<br>
        &emsp;- 티켓서버 → 결제서버 통신 및 인증<br><br>
        ▶ 티켓, 결제 서버 사이의 비동기 메시지 큐 환경 구축<br>
        &emsp;- SQS / SNS를 활용한 메시지 큐 환경 구축<br>
        &emsp;- Toss API 호출 결과에 따른 트랜잭션 보장<br><br>
        ▶ 1:1 상담시스템 구축<br>
        &emsp;- STOMP을 활용한 1:1 통신 구축<br>
        &emsp;- MySQL을 활용한 채팅 내용 저장<br><br>
        ▶ 테스트<br>
        &emsp;- jmeter, prometeus, grafana 활용한 테스트<br>
        &emsp;- 테스트에 사용할 더미 데이터 추가 코드 작성
      </td>
      <td><a href="https://github.com/kkw2238">https://github.com/kkw2238</a></td>
    </tr>
    <tr>
      <td>김태준</td>
      <td>💛 <strong>Member</strong></td>
      <td>
        ▶ Redis<br>
        &emsp;- 실시간 검색어 순위<br>
        &emsp;- 인기 공연 실시간 순위<br><br>
        ▶ 인증 보안 강화<br>
        &emsp;- E-Mail 인증으로 계정 보안 강화<br>
        &emsp;- Google OTP 2차 인증 계정 보안 강화<br><br>
        ▶ OpenAI<br>
        &emsp;- AI챗봇을 통한 사용자맞춤 추천 제공
      </td>
      <td><a href="https://github.com/KTJ-1650">https://github.com/KTJ-1650</a></td>
    </tr>
    <tr>
      <td>최선</td>
      <td>💛 <strong>Member</strong></td>
      <td>
        ▶ 검색 성능 개선<br>
        &emsp;- ElasticSearch 기반 저장소 및 검색 구현<br>
        &emsp;- AWS OpenSearch로 마이그레이션하여 운영<br><br>
        ▶ 인덱싱<br>
        &emsp;- 공연 키워드 및 양도 가능 티켓 검색 최적화<br><br>
        ▶ 서버 간 통신 개선<br>
        &emsp;- REST API → gRPC 프로토콜 전환<br><br>
        ▶ 공연 이미지 저장<br>
        &emsp;- AWS S3에 이미지 저장<br>
        &emsp;- CloudFront URL 반환<br><br>
        ▶ 소셜로그인 기능<br>
        &emsp;- OAuth 기반 카카오 로그인 구현
      </td>
      <td><a href="https://github.com/sunchoiii">https://github.com/sunchoiii</a></td>
    </tr>
  </tbody>
</table>
<br>

**\[ 협업 방식 및 규칙 \]**

![34](https://github.com/user-attachments/assets/7bb718fa-ef13-471d-8d75-ce5ef30f3628)

<br>

## 8\. 성과 및 회고

### 👍🏻 **잘된 점**

**\[ 효율적인 협업 \]**

-   매일 스크럼을 통해 문제를 빠르게 공유하고, 함께 적극적으로 해결

-   저녁마다 코드 리뷰를 진행하여 개선점을 빠르게 적용하고. 서로가 어떤 것을 개발하고 있는지 파악

<br>


**\[ 꼼꼼한 일정관리 및 문서화 \]**

-   기본적으로 큰 단위의 일정을 세운 다음(CRUD, 개선, 고도화, 마일스톤 등) 데일리 스크럼을 통해 매주, 매일의 세부 계획을 세우고 달성 여부를 체크함 → 프로젝트 기한동안 일정 관리가 매우 수월했음

-   공유해할 규칙이나 환경변수 목록, 서로가 사용하지 않지만 알아야 할 기술적 내용, 개인별 트러블 슈팅 등 모든 사항을 문서화 하여 팀 노션에 공유함 → 모든 팀원이 프로젝트 진행 상황을 파악하고 있었음

<br>

**\[ 프로젝트와 관련된 의사소통 \]**

-   현재 진행중인 과정에서 발생하는 문제에 대한 질문이 오가는 과정에서 불편함이나 무례함 없이 의사소통이 이루어졌음

<br>

**\[ 진취적인 기능 구현 \]**

-   프로젝트 초기 단계에서 모든 팀원이 기능 구현에 대한 높은 열의를 가지고 적극적으로 아이디어를 제안하고 기획에 참여함

-   초기 프로젝트 설계에서 계획한 기술 및 개선방안을 모두 적용한 후, 보안처리(OTP, 이메일인증) 및 사용자 경험 개선(실시간 알림, gRPC 통신) 등을 추가로 구현함 → 목표 초과달성으로 프로젝트의 완성도를 높임

<br>

### 👀 아쉬운 점

-   시간 부족으로 일부 기능을 미완성함 : Redis Cluster, HTTPS 적용

-   ElasticSearch, gRPC, SQS, SNS, 결제API, Fargate 배포 등 대부분 사용해본 적 없는 기술이어서, 초기 시스템 설계 및 일정 계획 시 실제 구현기간을 예상하기 어려웠음. 이 때문에 초반에 일정 수정이 잦았음

<br>

### 📆 향후 계획

**\[ 기술적 고도화 \]**

-   레디스 클러스터
    
    -   현재 주요 기능에서 Redis를 사용하고 있어서, 단일 Redis 장애 발생 시 서비스 중단을 방지함
    
    -   클러스터를 통해 여러 노드에 트래픽을 분산하여 처리 성능을 향상시킴

<br>

-   SQS 병렬처리
  
    -   실시간 서버가 SQS 메시지를 처리할 때, 다중 스레드로 메시지를 처리하도록 설정하여 한 번에 여러 메시지를 동시에 처리하게 하여 대기시간을 줄이고자 함

<br>

-   HTTPS (ACM 인증서 적용)
  
    -   현재 HTTP 프로토콜을 사용하고 있어 비교적 보안에 취약함. 따라서 AWS의 ACM 인증서를 적용해 암호화된 HTTPS 프로토콜로 개선하고자 함

<br>

-   ElasticSearch + GPT 결합
  
    -   더 빠르고 정확한 검색 결과를 제공하며, GPT를 통해 자연어 질문도 처리해 사용자 경험을 극대화 하고자 함

<br>

**\[ 테스트 자동화 강화 \]**

-   기존에 시행한 단위, 시나리오, 성능 테스트 외에 통합 테스트를 추가하여 **테스트 커버리지 50% 목표**로 안정성을 더욱 강화
