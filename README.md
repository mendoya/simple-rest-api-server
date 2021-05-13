# 결제요청을 받아 카드사와 통신하는 인터페이스를 제공하는 결제시스템

## 개발 프레임워크
* Java 11
* Spring Boot 2.4.5

## 테이블 설계
* 결제 정보 및 취소 정보를 테이블에 저장
* Payment

| Column      |      Type     |  Desc             |
|-------------|---------------|-------------------|
| id          | varchar(20)   |  거래관리번호      |
| amount      | bigint        |  거래금액          |
| card_no     | varchar(16)   |  카드번호          |
| cvc         | varchar(3)    |  CVC              |
| expire      | varchar(4)    |  유효기간          |
| installment | integer       |  할부기간          |
| message     | varchar(450)  |  카드사 전문 메시지 |
| org_id      | varchar(20)   |  원거래관리번호     |
| vat         | bigint        |  부가세            |

## 문제해결 전략
* 카드결제 / 결제취소 / 결제정보 조회 REST API
  * 결제와 결제 취소는 POST Method를 통해 호출
  * 결제정보 조회는 GET Method 를 통해 호출
  * uri 는 /{version}/{resource} 의 형태로 지정
* 부분취소 API
  * 부분 결제 취소는 결제 취소 API 와 동일하게 호출하도록 설계
  * 부분 결제 가능 금액 등의 계산은 테이블의 원거래관리번호 기반으로 레코드를 조회하여 계산

## 빌드 및 실행
### 빌드
```
gradlew clean bootJar
```
### 실행
```
java -jar build\libs\simple-rest-api-server-0.0.1-SNAPSHOT.jar
```
### Request URI
 * 결제
  ```
  POST /v1/payments
  ```
 * 결제조회
  ```
  GET /v1/payments/{paymentId}
  ```
 * 결제취소
  ```
  POST /v1/payments/cancel
  ```