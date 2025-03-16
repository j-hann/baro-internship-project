## 💻 프로젝트 소개

### _JWT & Spring Security & AWS EC2를 사용한 사용자 인증 시스템_


### 🌏 배포 서버 정보
#### http://ec2-54-180-95-107.ap-northeast-2.compute.amazonaws.com:8080/
### 📑 Swagger API 명세서
#### http://ec2-54-180-95-107.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html

## 🛠️기술 스택 & 🌱 개발 환경

### ☀️ Backend
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSONWebTokens&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-CA0C0C?style=for-the-badge&logo=Lombok&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white)
### ☀️ Database & Infra
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=AmazonEC2&logoColor=white)

### ☀️ Tool
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=black)


## 🪐구현 기능
### ✨ 회원가입 API
- DB 사용 불가 조건으로 사용자 정보를 HashMap으로 관리
### ✨ 로그인 API
- JWT 토큰을 발급하여 인증 처리

### ✨ 관리자 권한 부여 API
- Spring Security 설정을 통해 ADMIN Role을 가진 사용자만 API 요청 가능
### ✨ Junit5 Test Code 
- JUnit5 기반 API 테스트 코드
### ✨ AWS EC2 수동 배포

