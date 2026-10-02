# Spring MVC File Upload 19

Spring MVC에서 파일 업로드와 다운로드를 처리하는 방법을 학습하고 예제 코드와 문서로 정리한 저장소입니다.

서블릿의 `Part`를 활용한 파일 업로드부터 Spring MVC의 `MultipartFile`을 활용한 파일 처리, 이미지 조회와 첨부파일 다운로드까지 학습했습니다.

## 학습 목적

HTML Form에서 파일이 `multipart/form-data` 형식으로 서버에 전달되고 처리되는 흐름을 이해하기 위해 정리했습니다.

서블릿의 `Part`를 직접 사용하는 방식과 Spring MVC의 `MultipartFile`을 사용하는 방식을 예제 코드로 확인했습니다.

## 학습 내용

- `multipart/form-data`를 활용한 파일 전송
- Servlet `Part`를 활용한 파일 업로드
- Spring MVC `MultipartFile`을 활용한 파일 업로드
- 단일 파일과 다중 파일 처리
- `UUID`를 활용한 서버 저장 파일명 생성
- 업로드 파일 정보와 실제 파일 저장
- 이미지 조회와 첨부파일 다운로드
- [개념 정리 파일 보기](./src/main/docs)

## 디렉터리 구조

```text
upload
├── gradle
│   └── wrapper
├── src
│   ├── main
│   │   ├── docs
│   │   │   └── file-upload.md
│   │   ├── java
│   │   │   └── hello
│   │   │       └── upload
│   │   │           ├── controller
│   │   │           │   ├── ItemController.java
│   │   │           │   ├── ItemForm.java
│   │   │           │   ├── ServletUploadControllerV1.java
│   │   │           │   ├── ServletUploadControllerV2.java
│   │   │           │   └── SpringUploadController.java
│   │   │           ├── domain
│   │   │           │   ├── Item.java
│   │   │           │   ├── ItemRepository.java
│   │   │           │   └── UploadFile.java
│   │   │           ├── file
│   │   │           │   └── FileStore.java
│   │   │           └── UploadApplication.java
│   │   └── resources
│   │       ├── static
│   │       │   └── index.html
│   │       ├── templates
│   │       │   ├── item-form.html
│   │       │   ├── item-view.html
│   │       │   └── upload-form.html
│   │       └── application.properties
│   └── test
│       └── java
│           └── hello
│               └── upload
│                   └── UploadApplicationTests.java
├── build.gradle
├── gradlew
├── gradlew.bat
└── settings.gradle
```

## 학습 포인트

- `multipart/form-data` 요청이 여러 `Part`로 나뉘어 전달되는 구조를 확인했습니다.
- Servlet의 `Part`를 사용해 요청 정보와 파일을 직접 처리하는 방법을 학습했습니다.
- Spring MVC의 `MultipartFile`을 사용해 업로드 파일을 받고 저장하는 방법을 확인했습니다.
- 사용자가 업로드한 파일명과 서버에서 저장하는 파일명을 분리하고 `UUID`를 사용해 서버 파일명을 생성했습니다.
- `MultipartFile`과 `List<MultipartFile>`을 사용해 첨부파일 하나와 이미지 파일 여러 개를 처리했습니다.
- `UrlResource`를 사용한 이미지 조회와 `Content-Disposition` 헤더를 사용한 첨부파일 다운로드 방식을 학습했습니다.

## 실행 환경

- Java 17
- Spring Boot 4.1.1
- Spring MVC
- Servlet
- Thymeleaf
- Gradle
- Lombok
- JUnit 5
- IntelliJ IDEA

## 참고

- 코드 출처 : 스프링 MVC 2편 - 백엔드 웹 개발 활용 기술