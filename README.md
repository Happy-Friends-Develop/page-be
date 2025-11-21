# [Happy-Friends] - 위치 기반 라이프스타일 플랫폼

> **"내 주변의 모든 즐거움을 찾다"**  <br> 사용자의 위치를 기반으로 반경 10km 내(설정 가능)의 먹거리, 놀거리, 숙소를 추천하고,  
> 실시간 채팅과 예약 결제까지 한 번에 제공하는 통합 플랫폼입니다.

<br>

##  Tech Stack

### Backend
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Spring Batch](https://img.shields.io/badge/Spring_Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![JPA](https://img.shields.io/badge/JPA_(Hibernate)-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

### Database & Cache
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)

### Communication & Utils
![WebSocket](https://img.shields.io/badge/WebSocket-000000?style=for-the-badge&logo=socket.io&logoColor=white)
![FFmpeg](https://img.shields.io/badge/FFmpeg-007808?style=for-the-badge&logo=ffmpeg&logoColor=white)

### API Integration
![Kakao Map](https://img.shields.io/badge/Kakao_Map_API-FFCD00?style=for-the-badge&logo=kakao&logoColor=black)
![Toss Payment](https://img.shields.io/badge/Toss_Payments-0064FF?style=for-the-badge&logo=toss&logoColor=white)
![OAuth 2.0](https://img.shields.io/badge/OAuth_2.0-EB5424?style=for-the-badge&logo=auth0&logoColor=white)

<br>

##  Project Overview

### 1. 위치 기반 서비스 (LBS)
- 사용자가 검색한 위치를 기준으로 반경 **10km 내외(거리 설정 가능)** 의 컨텐츠를 필터링하여 제공합니다.
- Kakao Map API를 활용하여 위치 확인이 가능합니다.

### 2. 실시간 커뮤니케이션
- **WebSocket**을 활용한 1:1 실시간 채팅 시스템을 구축하여 판매자와 구매자 간의 즉각적인 상담이 가능합니다.

### 3. 대용량 처리 및 성능 최적화
- **Spring Batch**를 활용하여 정산, 통계 데이터 등 대량의 백그라운드 작업을 처리합니다.
- **Redis**를 도입하여 조회 성능을 개선하고, 채팅 메시지 큐 등으로 활용합니다.

<br>

##  Key Features

###  구매자 (User)
- **위치 기반 탐색**: 현재 위치 또는 지정 위치 기준 반경 설정 검색
- **예약 및 결제**: Toss Payments 연동을 통한 간편 결제 및 예약 시스템
- **실시간 상담**: WebSocket 기반 채팅으로 판매자와 즉시 문의 가능
- **커뮤니티 기능**:
    - 게시글 댓글 및 평점 등록
    - 관심 게시글 '좋아요' 및 장바구니 담기
    - 불량 게시글 신고 기능
- **이벤트**: 플랫폼 내 진행 중인 이벤트 참가

###  판매자 (Seller)
- **컨텐츠 관리**: 먹거리, 놀거리, 숙소 등 판매 게시글 등록 및 관리 (FFmpeg 활용 미디어 처리)
- **비즈니스 상담**: 구매자와의 1:1 실시간 채팅 응대
- **매출 관리**: 자신의 컨텐츠 판매 내역 및 정산 확인
- **데이터 분석**: 게시물 별 '좋아요' 수, 조회수, 방문 횟수 등 통계 대시보드 제공

### 🛡관리자 (Admin)
- **사이트 운영**: 회원 관리 및 전체 컨텐츠 관리
- **이벤트 관리**: 시즌 별 이벤트 생성 및 관리
- **신고 처리**: 접수된 신고 내역 검토 및 제재 처리

<br>
<br>