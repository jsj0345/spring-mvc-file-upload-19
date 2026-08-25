# HTML 폼에서 파일이 서버로 전달되는 과정 정리

> 일반 폼 데이터와 파일이 HTTP 요청 안에서 어떻게 달라지는지 확인하고, 서블릿의 `Part`를 이용해 멀티파트 요청을 읽고 파일을 저장하는 과정까지 정리했다.

## 1. 일반 폼 전송만으로 파일을 다루기 어려운 이유

HTML 폼에서 별도의 `enctype`을 지정하지 않으면 보통 `application/x-www-form-urlencoded` 형식으로 요청이 만들어진다.

예를 들어 이름과 나이를 전송하면 요청 본문은 다음과 비슷한 형태가 된다.

```text
username=kim&age=20
```

각 입력값을 문자 형태로 이어 붙여 전달하는 방식이라 일반적인 텍스트 입력을 처리하기에는 단순하다.

하지만 파일 업로드에서는 상황이 달라진다.

```text
이름 → 문자 데이터
나이 → 문자 데이터
첨부 파일 → 파일 데이터
```

하나의 폼 요청 안에서 성격이 다른 데이터를 함께 보내야 하므로 기존 방식만으로 처리하기 어렵다.

이때 사용하는 전송 형식이 `multipart/form-data`다.

---

## 2. multipart/form-data는 요청 데이터를 여러 조각으로 나눈다

파일을 함께 전송하려면 폼에 다음 설정이 필요하다.

```html
<form method="post" enctype="multipart/form-data">
```

`multipart/form-data`로 요청하면 폼 전체를 하나의 문자열로 합치는 대신 각 입력값을 구분해서 전달한다.

대략적인 요청 구조는 다음과 같다.

```text
boundary
Content-Disposition: form-data; name="username"

kim
boundary
Content-Disposition: form-data; name="file"; filename="intro.png"
Content-Type: image/png

파일 데이터
```

여기서 `boundary`가 각 데이터를 나누는 기준 역할을 한다.

그래서 서버 입장에서는 하나의 요청을 받더라도 내부에는 `username`, `file`처럼 서로 구분된 여러 데이터가 들어 있다고 볼 수 있다.

이렇게 나누어진 각각의 데이터를 서블릿에서는 `Part`로 다룬다.

---

## 3. 업로드 폼에서 multipart 요청 만들기

현재 예제의 업로드 화면은 상품명과 파일을 함께 전송한다.

```html
<form th:action method="post" enctype="multipart/form-data">
    <ul>
        <li>상품명 <input type="text" name="itemName"></li>
        <li>파일 <input type="file" name="file"></li>
    </ul>
    <input type="submit"/>
</form>
```

여기서 중요한 부분은 `enctype="multipart/form-data"`다.

이 설정 때문에 `itemName`과 `file`이 하나의 문자열로 합쳐지는 것이 아니라 서로 다른 `Part`로 구분되어 전송된다.

---

## 4. 서블릿에서 멀티파트 요청 확인하기

첫 번째 컨트롤러에서는 멀티파트 요청이 서버에서 어떻게 보이는지 확인한다.

```java
String itemName = request.getParameter("itemName");
Collection<Part> parts = request.getParts();
```

`request.getParameter("itemName")`은 일반 입력값인 상품명을 가져온다.

`request.getParts()`는 현재 요청을 구성하는 `Part`들을 가져온다.

현재 폼에는 다음 두 입력이 있으므로 요청 역시 두 부분으로 나뉜다.

```text
itemName
file
```

즉, 여기서 `parts`는 여러 요청을 의미하는 것이 아니라 **하나의 multipart 요청 안에서 구분된 각각의 데이터 묶음**을 의미한다.

---

## 5. 실제 HTTP 요청에서 Part가 어떻게 구분되는지 확인하기

HTTP 요청 로그를 보기 위해 다음 로그 레벨을 사용했다.

```properties
logging.level.org.apache.coyote.http11=trace
```

업로드 요청을 보내면 일반 입력과 파일이 서로 다른 영역으로 나뉘어 있는 것을 확인할 수 있다.

```text
Content-Disposition: form-data; name="itemName"

itemA

Content-Disposition: form-data; name="file"; filename="...png"
Content-Type: image/png
```

상품명 부분에는 문자열 값이 들어가고, 파일 부분에는 파일명과 파일의 타입 정보가 추가된다.

파일 본문은 이미지와 같은 바이너리 데이터이므로 일반 문자열 로그처럼 읽으려고 하면 사람이 확인하기 어려운 형태로 출력된다.

이 로그를 통해 `multipart`라는 이름 그대로 하나의 요청이 여러 부분으로 구성된다는 점을 확인했다.

---

## 6. Spring Boot의 멀티파트 처리 설정 확인

Spring Boot에서는 멀티파트 처리가 기본적으로 활성화되어 있다.

이를 끄고 동작 차이를 확인하려면 다음 설정을 사용할 수 있다.

```properties
spring.servlet.multipart.enabled=false
```

멀티파트 처리를 비활성화한 상태에서는 예제에서 다음 값들을 정상적으로 얻지 못했다.

```text
itemName=null
parts=[]
```

반대로 멀티파트 처리를 사용하면 상품명과 각각의 `Part`를 읽을 수 있다.

```text
itemName=itemA
parts=[..., ...]
```

따라서 브라우저가 `multipart/form-data` 형식으로 요청을 보내는 것과 별개로, 서버에서도 그 형식을 해석할 수 있도록 멀티파트 처리가 동작해야 한다는 점을 확인했다.

---

## 7. 업로드 크기 제한 설정

파일 업로드 요청은 데이터 크기가 커질 수 있기 때문에 Spring Boot에서 크기 제한을 설정할 수 있다.

```properties
spring.servlet.multipart.max-file-size=1MB
spring.servlet.multipart.max-request-size=10MB
```

두 설정은 기준이 다르다.

```text
max-file-size
→ 파일 하나에 적용되는 최대 크기

max-request-size
→ 하나의 multipart 요청 전체에 적용되는 최대 크기
```

따라서 한 요청에 여러 파일이 포함된다면 각각의 파일 크기뿐 아니라 요청 전체 크기도 따로 제한할 수 있다.

설정한 크기를 넘는 요청은 정상적인 업로드 요청으로 처리되지 않고 예외가 발생한다.

---

## 8. Part 안에 어떤 정보가 들어 있는지 확인하기

두 번째 컨트롤러에서는 `request.getParts()`로 얻은 값을 하나씩 순회한다.

```java
for (Part part : parts) {
    log.info("name={}", part.getName());
    log.info("submittedFilename={}", part.getSubmittedFileName());
    log.info("size={}", part.getSize());
}
```

상품명 `Part`와 파일 `Part`는 같은 `Part` 타입이지만 포함된 정보에는 차이가 있다.

상품명에서는 다음과 같은 값을 확인할 수 있다.

```text
name=itemName
submittedFilename=null
body=itemA
```

파일에서는 다음과 같은 정보가 나온다.

```text
name=file
submittedFilename=업로드한 파일명
content-type=image/png
```

즉, 일반 폼 데이터 역시 하나의 `Part`이며 파일인 경우에만 파일명과 `Content-Type` 같은 정보가 추가로 들어간다.

---

## 9. Part의 헤더와 본문 읽기

각 `Part`도 헤더와 실제 데이터 영역을 가지고 있다.

헤더는 다음 코드로 확인했다.

```java
Collection<String> headerNames = part.getHeaderNames();

for (String headerName : headerNames) {
    log.info("header {}: {}", headerName, part.getHeader(headerName));
}
```

그리고 `Part`의 실제 내용은 입력 스트림으로 읽을 수 있다.

```java
InputStream inputStream = part.getInputStream();
String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
```

상품명처럼 문자열로 들어온 데이터는 `itemA`처럼 확인할 수 있다.

반면 이미지 파일도 같은 방식으로 문자열 변환을 시도하면 바이너리 데이터를 문자로 해석하게 되므로 정상적인 글자로 보이지 않는다.

이 코드는 파일 내용을 문자열로 사용하려는 목적보다는 각 `Part`에 실제 데이터가 들어 있다는 것을 확인하기 위한 예제로 봤다.

---

## 10. 업로드된 파일을 서버 경로에 저장하기

파일을 실제로 저장하려면 먼저 저장 위치를 설정한다.

현재 프로젝트에서는 `application.properties`에 다음처럼 경로를 두었다.

```properties
file.dir=C:/Users/jsj/study/file/
```

컨트롤러에서는 이 값을 `@Value`로 가져온다.

```java
@Value("${file.dir}")
private String fileDir;
```

그다음 파일명이 존재하는 `Part`만 골라 저장한다.

```java
if (StringUtils.hasText(part.getSubmittedFileName())) {
    String fullPath = fileDir + part.getSubmittedFileName();
    part.write(fullPath);
}
```

일반 입력값인 `itemName`도 `Part`에 포함되지만 파일명이 없기 때문에 저장 대상에서 제외된다.

파일 `Part`는 `getSubmittedFileName()`으로 전달받은 파일명을 확인할 수 있고, `write()`를 사용해 지정한 경로에 저장할 수 있다.

---

## 11. 이번 예제에서 사용한 Part 메서드 정리

이번 코드에서 실제로 확인한 메서드는 다음과 같다.

- `getName()` : 폼에서 사용한 입력 필드 이름을 확인한다.
- `getHeaderNames()` / `getHeader()` : 해당 `Part`의 헤더 정보를 확인한다.
- `getSubmittedFileName()` : 파일을 업로드한 경우 전달된 파일명을 확인한다.
- `getSize()` : 해당 `Part` 데이터의 크기를 확인한다.
- `getInputStream()` : `Part`의 실제 데이터를 읽는다.
- `write()` : 전달받은 파일 데이터를 지정한 경로에 저장한다.

---











