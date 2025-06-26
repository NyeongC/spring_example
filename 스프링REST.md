# CHAPTER 4 스프링 REST

### REST란?

**REST(Representational State Transfer)** 는  
웹에서 **데이터를 주고받기 쉽게 만든 방식(설계 스타일)** 입니다.

---

#### ✅ 쉽게 말해서

- 웹에서 **서버와 클라이언트가 약속된 규칙**에 따라 데이터를 주고받는 방식이에요.
- 특히 **HTTP의 기본 기능(GET, POST, PUT, DELETE 등)을 활용**해서 통신합니다.
- **브라우저, 앱, 서버, IoT 등 다양한 시스템이 서로 연결**될 수 있도록 도와줍니다.

---

#### ✅ REST의 목적

- 환경이 달라도 서로 **문제를 쉽게 주고받게** 하기 위해
- **시스템 간 연결을 단순하고 일관되게** 만들기 위해

---

#### 📌 예시

| HTTP 메서드 | URL | 의미 |
|-------------|-----|------|
| GET | `/users/1` | 1번 사용자 정보 조회 |
| POST | `/users` | 새 사용자 등록 |
| PUT | `/users/1` | 1번 사용자 정보 수정 |
| DELETE | `/users/1` | 1번 사용자 삭제 |

→ 이렇게 **"주소 + 동작(메서드)"** 만으로  
   데이터를 주고받는 구조가 REST입니다.

---

#### ✅ REST를 쓰면 좋은 점

- 누구나 쉽게 이해할 수 있는 API 설계 가능
- 웹 표준만 따라도 통신 가능 (HTTP만 알면 됨)
- 다양한 시스템에서 사용 가능 (웹, 앱, 서버 등)



---

### 엔드포인트(Endpoint)란?

**엔드포인트**는 클라이언트가 서버에 요청을 보낼 수 있는 **명확한 접점(URL)** 입니다.  
-> (URL + HTTP 메서드(GET, POST, PUT, DELETE 등)의 조합)

REST에서는 각각의 리소스에 대해 **고유한 URL**을 가지며, 이 URL을 통해 해당 리소스를 조작하거나 조회합니다.

```
GET     /api/products           → 상품 전체 목록 조회  
GET     /api/products/{id}      → 특정 상품 조회  
POST    /api/products           → 상품 추가  
PUT     /api/products/{id}      → 상품 정보 수정  
DELETE  /api/products/{id}      → 상품 삭제
```

---

### `@RequestMapping`과 매핑 어노테이션

**`@RequestMapping`** 은 클라이언트 요청 URL을 **컨트롤러의 메서드에 매핑**하는 어노테이션입니다.  
기본적으로 모든 HTTP 메서드(GET, POST 등)에 대응하지만, 메서드별 전용 애너테이션을 사용하는 것이 일반적입니다.

#### HTTP 메서드 전용 애너테이션
- `@GetMapping`
- `@PostMapping`
- `@PutMapping`
- `@DeleteMapping`

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // 상품 조회 (셀렉트)
    @GetMapping("/{id}")
    public String selectProduct(@PathVariable int id) {
        return "조회한 Product ID: " + id;
    }

    // 상품 저장 (인서트)
    @PostMapping("/{id}")
    public String insertProduct(@PathVariable int id) {
        return "저장한 Product ID: " + id;
    }
}
```

---

### `@PathVariable`로 경로 변수 추출

**`@PathVariable`** 은 URL 경로에서 **변수를 추출** 할 때 사용합니다.

```java
@GetMapping("/users/{userId}")
public String getUser(@PathVariable String userId) {
    return "User ID: " + userId;
}
```

##### POST 요청 시 @PathVariable, @RequestBody 병행 가능

```java
@PostMapping("/users/{userId}/orders")
public ResponseEntity<Order> createOrder(
    @PathVariable Long userId,
    @RequestBody OrderRequest orderRequest
) {
    // userId: URL에서 추출된 사용자 ID
    // orderRequest: Request Body에서 전달된 주문 정보
    return ResponseEntity.ok(orderService.createOrder(userId, orderRequest));
}
```
---

### 마셜링(Marshalling) View로 XML 만들기(요즘 거의 안씀..)

스프링에서는 **객체를 XML로 변환하는 마셜링 기능** 을 제공합니다.  
`MarshallingView`를 사용하여 XML 형식의 응답을 생성할 수 있습니다.

```java
@Bean
public MarshallingView marshallingView() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setClassesToBeBound(Product.class);
    return new MarshallingView(marshaller);
}
```

---

### `@RequestBody`로 XML 받기

**`@RequestBody`** 는 요청 본문(body)에 담긴 데이터를 **자바 객체로 변환**해줍니다.  
스프링은 내부적으로 **HttpMessageConverter**를 사용하여 **XML → 객체** 로 역직렬화합니다.

```java
@PostMapping("/products")
public String createProduct(@RequestBody Product product) {
    return "Product Created: " + product.getName();
}
```

---

### `@PathVariable`로 결과 거르기

경로에 따라 데이터를 분기 처리할 수 있습니다.

```java
@GetMapping("/orders/{status}")
public String filterOrders(@PathVariable String status) {
    if (status.equals("completed")) {
        return "Completed Orders Only";
    }
    return "All Orders";
}
```

---

### `ResponseEntity`로 클라이언트에게 응답 제어

**`ResponseEntity`** 는 HTTP 응답의 **상태 코드, 헤더, 바디를 명확하게 설정**할 수 있게 해주는 유틸입니다.

```java
@GetMapping("/products/{id}")
public ResponseEntity<Product> getProduct(@PathVariable int id) {
    Product product = new Product(id, "Toy");
    return ResponseEntity.ok(product);  // 200 OK
}
```

---

### REST 방식으로 JSON 응답하기 – Jackson2JsonView

스프링 부트에서는 기본적으로 **Jackson 라이브러리**가 포함되어 있어,  
객체를 JSON으로 자동 변환해줍니다.

---

### `@ResponseBody`로 JSON 만들기

**`@ResponseBody`**는 메서드의 반환 값을 **HTTP 응답 본문에 직접 출력**합니다.  
JSON 변환은 Jackson이 자동으로 수행합니다.

```java
@Controller
public class MyController {

    @GetMapping("/json")
    @ResponseBody
    public Product getProductAsJson() {
        return new Product(1, "Coffee");
    }
}
```

> `@RestController`는 `@Controller + @ResponseBody`의 조합입니다.

---

### Gson으로 JSON 만들기

스프링 부트는 기본적으로 **Jackson**을 사용하지만, **Gson** 라이브러리를 사용해도 JSON을 만들 수 있습니다.  
Gson은 구글에서 만든 JSON 라이브러리로, 간단하고 직관적인 API를 제공합니다.

#### Gradle 의존성

```groovy
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

#### 예제

```java
import com.google.gson.Gson;

public class GsonExample {
    public static void main(String[] args) {
        Product product = new Product(1, "Keyboard");
        Gson gson = new Gson();

        String json = gson.toJson(product);
        System.out.println(json);
    }
}
```

결과:

```json
{"id":1,"name":"Keyboard"}
```

### ✅ Jackson vs Gson 요약 정리

- **Jackson**  
  - Spring Boot 기본 포함 (설정 없이 바로 사용 가능)  
  - 기능 많고 유연함  
  - 성능 좋음  
  - 대부분의 REST API에서 기본 사용

- **Gson**  
  - Google에서 만든 JSON 라이브러리  
  - 더 가볍고 간단함  
  - 콘솔 앱, 테스트 코드, 외부 라이브러리(Retrofit 등)에서 자주 사용  
  - Spring에서 사용하려면 직접 등록해야 함

---

### 📌 결론

- 기본은 **Jackson 쓰면 됨**
- **간단하게 JSON 변환만 필요하거나**,  
  **외부 라이브러리가 Gson을 쓸 때** → Gson 선택


---

### 스프링 애플리케이션에서 외부 REST 서비스 호출하기

스프링에서는 다른 서버의 REST API를 호출해서 데이터를 받을 수 있습니다.  
이를 통해 외부 시스템(서드파티 API)에서 응답된 JSON 데이터를 받아 비즈니스 로직에 사용할 수 있습니다.

---

### 서드파티(Third-Party)란?

**서드파티(3rd Party)**란  
**내가 만든 것이 아닌 외부에서 제공한 제품, 소프트웨어, 서비스 또는 API**를 말합니다.

---

### ✅ 쉽게 말해

> **"제3자가 만든 것"**  
> → 내가 개발하지 않았지만, 내 서비스에서 사용하는 외부 기능이나 도구

---

#### ✅ RestTemplate 클래스 개요

`RestTemplate`은 외부 REST API를 호출할 수 있게 해주는 스프링의 HTTP 클라이언트입니다.  
주로 **동기 방식**으로 사용되며, 다양한 HTTP 메서드를 지원합니다.

---

#### 표 4-1. 주요 메서드 소개

| 메서드 | 설명 |
|--------|------|
| `getForObject(String url, Class<T> responseType)` | GET 요청 후 객체 반환 |
| `getForEntity(String url, Class<T> responseType)` | GET 요청 후 ResponseEntity 반환 |
| `postForObject(String url, Object request, Class<T> responseType)` | POST 요청 후 객체 반환 |
| `postForEntity(String url, Object request, Class<T> responseType)` | POST 요청 후 ResponseEntity 반환 |
| `put(String url, Object request)` | PUT 요청 전송 |
| `delete(String url)` | DELETE 요청 전송 |
| `optionsForAllow(URI url)` | 해당 URI에서 허용하는 HTTP 메서드 목록 조회 |

---

#### ✅ 간단 예시 1: 문자열로 응답 받기

```java
RestTemplate restTemplate = new RestTemplate();
String result = restTemplate.getForObject(
    "https://jsonplaceholder.typicode.com/posts/1",
    String.class
);
System.out.println(result);
```

```json
// 실행 결과 예시 (JSON 문자열)
{
  "userId": 1,
  "id": 1,
  "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
  "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum..."
}
```

---

#### ✅ 간단 예시 2: 객체로 응답 받기

```java
Product product = restTemplate.getForObject(
    "https://api.example.com/products/1",
    Product.class
);
```

> 위처럼 외부 API에서 응답받은 JSON을  
> `Product` 클래스 형태로 바로 변환하여 사용할 수 있습니다.

### 순수 자바로 REST API 호출할 때의 번거로움

기존 자바 방식으로 외부 API를 호출하려면 아래처럼  
**커넥션 생성 → 요청 설정 → 응답 읽기 → 스트림 닫기 등**  
복잡한 과정을 거쳐야 합니다.

```java
try {
    URL url = new URL("https://api.example.com/data");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");

    BufferedReader in = new BufferedReader(
        new InputStreamReader(conn.getInputStream())
    );

    String inputLine;
    StringBuilder response = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
    }
    in.close();

    System.out.println(response.toString());
} catch (IOException e) {
    e.printStackTrace();
}
```

너무 길고 복잡하고, 예외 처리도 따로 해줘야 함..

---

### ✅ RestTemplate을 쓰면 이렇게 간단해집니다!

```java
RestTemplate restTemplate = new RestTemplate();
String result = restTemplate.getForObject(
    "https://api.example.com/data",
    String.class
);
System.out.println(result);
```

💡 자동으로 다음을 처리해줍니다:

- HTTP 연결 열기/닫기
- 요청 메서드 지정 (GET/POST 등)
- 응답 스트림 읽기
- JSON/텍스트 자동 변환
- 예외 처리

---

### 결론

> **RestTemplate은 자바의 복잡한 HTTP 호출 과정을 깔끔하게 추상화한 스프링의 도구입니다.**  
> 외부 REST API를 호출할 때는 무조건 이걸 쓰는 게 실무 표준입니다.



---

> ✅ 이처럼, **외부 REST 서비스로부터 JSON 응답을 받아** 내부 서비스에서 활용하는 방식은  
> 흔히 **서드파티 API 연동, MSA 간 통신, 외부 시스템 통합** 에서 사용됩니다.

# RSS / Atom 피드 정리

## ✅ 피드란?

웹사이트에서 **새로운 콘텐츠(글, 뉴스, 에피소드 등)** 를  
**정형화된 포맷(XML)** 으로 만들어 **외부 시스템이 자동으로 읽을 수 있도록 공개**한 문서.

> 즉, **"새 글 올라왔으니 필요하면 읽어가세요"** 하는 방식.

---

## 피드의 종류

| 종류 | 설명 | 포맷 |
|------|------|------|
| **RSS** | Really Simple Syndication | XML (`application/rss+xml`) |
| **Atom** | 더 최신이고 구조화된 피드 표준 | XML (`application/atom+xml`) |

둘 다 거의 같은 용도로 쓰이며, 많은 리더 앱들이 **RSS/Atom 둘 다 지원**함.

---

## 동작 방식

1. 서버에서 RSS 또는 Atom 포맷으로 **피드 주소 생성**  
   예: `https://cheol.dev/rss.xml`

2. 사용자가 **RSS 리더 앱**이나 서비스에 **그 주소를 등록(=구독)**

3. 앱/서비스가 주기적으로 해당 피드 URL을 **요청해서 새 글 확인**

4. 새 콘텐츠가 있을 경우 사용자에게 알림 or 화면에 노출

---

## 📌 주요 특징

- **피드 URL은 고정** → `https://cheol.dev/rss.xml` 같은 주소는 **계속 그대로 유지**
- **최신 콘텐츠가 위로 누적** → 보통 **최신 10~20개 글**이 포함됨
- **정해진 XML 포맷** → 리더기나 앱이 구조를 인식하고 자동 파싱
- **콘텐츠는 `<item>` 혹은 `<entry>` 형태로 나열**

---

## 사용되는 곳

| 분야 | 설명 |
|------|------|
| 블로그 | 개발자 블로그, 티스토리, 브런치 등 구독용 |
| 뉴스 | 언론사 기사 → 구글 뉴스, 네이버 뉴스 등에서 수집 |
| 팟캐스트 | Apple/Spotify Podcast는 **RSS 기반 배포** |
| 크롤링/자동화 | 특정 시스템에서 새 글 감지 후 알림 처리 |
| 내부 시스템 | 배포 알림, 장애 보고 등을 RSS로 제공하기도 함 |

---

## 실제 RSS 예시 (요약)

```xml
<rss version="2.0">
  <channel>
    <title>철녕 블로그</title>
    <link>https://cheol.dev</link>
    <description>개발 이야기</description>

    <item>
      <title>스프링 REST 정리</title>
      <link>https://cheol.dev/spring-rest</link>
      <pubDate>Wed, 25 Jun 2025 09:00:00 +0900</pubDate>
    </item>
  </channel>
</rss>
```

