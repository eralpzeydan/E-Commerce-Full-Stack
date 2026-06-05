# 03 - Bean Scope, Environment, Profile ve Spring Initializr

## Bu bölümde ne anlatılıyor?

Bu bölümde Spring Framework içinde bean'lerin hangi kapsamda yaşayacağı, farklı ortamlar için profile kullanımı, configuration değerlerinin dışarıdan okunması ve Spring Boot projesi oluşturmak için Spring Initializr kullanımı anlatılıyor.

Ana konular:

- Bean scope kavramı
- Singleton, prototype, request, session, application ve websocket scope'ları
- `@Scope`
- Environment bean
- Spring profiles
- `@Profile`
- `@Value`
- `@PropertySource`
- Configuration class'larını bölme
- Spring Initializr ile proje oluşturma

---

# Bean Scope Nedir?

**Bean scope**, bir Spring bean'inin yaşam döngüsünü ve uygulama içinde nerelerde, ne kadar süreyle kullanılacağını belirler.

Başka bir ifadeyle scope şunu belirler:

- Bean ne zaman oluşturulacak?
- Kaç tane instance üretilecek?
- Aynı bean instance'ı mı paylaşılacak?
- Her istekte yeni bir instance mı oluşturulacak?
- Bean hangi context içinde geçerli olacak?

Spring'de scope seçimi özellikle **state management** açısından önemlidir.

---

# Default Bean Scope: Singleton

Spring'de default bean scope **singleton**'dır.

Yani özel bir scope belirtmezsek Spring, bean'i singleton olarak oluşturur.

## Singleton ne demek?

Singleton scope'ta:

- Application context içinde bean'den sadece **bir tane instance** oluşturulur.
- Bu bean'e ihtiyaç duyan tüm class'lar aynı instance'ı kullanır.
- Bean, Spring container tarafından yönetilir.
- Uygulama boyunca aynı nesne paylaşılır.

```java
@Service
public class PaymentService {
}
```

Bu örnekte `PaymentService` için özel scope verilmediği için Spring bunu singleton olarak yönetir.

## Singleton scope ne zaman uygundur?

Singleton scope genellikle şu durumlarda uygundur:

- Bean state tutmuyorsa
- Bean thread-safe ise
- Aynı instance'ın tüm uygulama tarafından paylaşılması sorun değilse
- Service, repository, controller gibi standart Spring component'leri için

Örneğin çoğu `@Service`, `@Repository` ve `@Controller` bean'i singleton'dır.

## Dikkat edilmesi gereken nokta

Singleton bean'ler uygulama genelinde paylaşıldığı için mutable state tutarken dikkatli olmak gerekir.

```java
@Service
public class PaymentService {

    private int counter;
}
```

Bu tarz bir field tüm kullanıcılar/thread'ler arasında paylaşılabilir. Bu da thread-safety problemlerine yol açabilir.

---

# Spring Bean Scope Türleri

Spring farklı ihtiyaçlara göre birden fazla bean scope sunar.

| Scope | Açıklama |
|---|---|
| `singleton` | Application context içinde tek instance oluşturulur. Default scope budur. |
| `prototype` | Bean her istendiğinde yeni instance oluşturulur. |
| `request` | Her HTTP request için yeni bean oluşturulur. |
| `session` | Her HTTP session için yeni bean oluşturulur. |
| `application` | ServletContext yaşam döngüsü boyunca geçerli bean oluşturulur. |
| `websocket` | WebSocket session yaşam döngüsü boyunca geçerli bean oluşturulur. |

---

# 1. Singleton Scope

Singleton, Spring'in varsayılan scope'udur.

```java
@Component
public class AccountService {
}
```

Bu class için Spring container içinde yalnızca bir instance oluşturulur.

## Özellikleri

- Tek instance vardır.
- Tüm bağımlılıklar aynı instance'ı kullanır.
- Stateless servisler için uygundur.
- Thread-safe tasarlanmalıdır.

---

# 2. Prototype Scope

Prototype scope'ta bean her istendiğinde yeni bir instance oluşturulur.

```java
@Component
@Scope("prototype")
public class ReportBuilder {
}
```

## Prototype ne zaman kullanılır?

Prototype scope şu durumlarda kullanılabilir:

- Bean state taşıyorsa
- Her kullanım için yeni nesne gerekiyorsa
- Kullanıcıya veya işleme özel geçici veri tutulacaksa

## Singleton ile farkı

Singleton:

```text
1 bean instance -> herkes kullanır
```

Prototype:

```text
Her request/getBean çağrısı -> yeni instance
```

---

# 3. Request Scope

Request scope sadece web application context içinde geçerlidir.

Her HTTP request için yeni bir bean oluşturulur.

```java
@Component
@RequestScope
public class RequestInfo {
}
```

## Ne zaman kullanılır?

- Bir HTTP request'e özel bilgi tutulacaksa
- Request boyunca kullanılacak geçici state varsa
- Request bazlı logging/context bilgisi taşınacaksa

Örneğin:

- request id
- client IP
- request zamanı
- authenticated user bilgisi

---

# 4. Session Scope

Session scope da web application context içinde geçerlidir.

Her HTTP session için yeni bir bean oluşturulur.

```java
@Component
@SessionScope
public class UserSession {
}
```

## Request scope ile farkı

Request scope:

```text
Her HTTP request için yeni bean
```

Session scope:

```text
Her kullanıcı session'ı için yeni bean
```

Session scope, aynı kullanıcının birden fazla request'i boyunca aynı bean instance'ını koruyabilir.

---

# 5. Application Scope

Application scope, servlet context seviyesinde geçerlidir.

Bu bean, web uygulamasının yaşam döngüsü boyunca kullanılabilir.

```java
@Component
@ApplicationScope
public class ApplicationCache {
}
```

Bu scope, web uygulaması seviyesinde ortak kullanılacak veriler için tercih edilebilir.

---

# 6. WebSocket Scope

WebSocket scope, WebSocket session yaşam döngüsü boyunca geçerlidir.

Bir WebSocket bağlantısına özel state tutulması gereken durumlarda kullanılabilir.

---

# Bean Scope Nasıl Tanımlanır?

Bean scope iki şekilde tanımlanabilir:

1. Scope ismiyle
2. Özel scope annotation'larıyla

---

## Scope ismiyle tanımlama

```java
@Component
@Scope("prototype")
public class ReportBuilder {
}
```

Ya da `@Bean` ile:

```java
@Configuration
public class AppConfig {

    @Bean
    @Scope("prototype")
    public ReportBuilder reportBuilder() {
        return new ReportBuilder();
    }
}
```

---

## Özel scope annotation'larıyla tanımlama

Spring bazı scope'lar için özel annotation'lar sağlar.

```java
@Component
@RequestScope
public class RequestInfo {
}
```

```java
@Component
@SessionScope
public class UserSession {
}
```

Bu kullanım string yazım hatalarını azaltır. Bu yüzden mümkün olduğunda özel annotation kullanmak daha güvenlidir.

---

# Environment Bean Nedir?

Spring, uygulamanın çalıştığı ortamla ilgili bilgilere erişmek için `Environment` abstraction'ını sağlar.

`Environment` sayesinde:

- Aktif profile bilgisi okunabilir.
- Property değerleri okunabilir.
- Ortama göre farklı bean/config davranışı sağlanabilir.
- Configuration dosyalarındaki değerler kullanılabilir.

Örneğin farklı ortamlar:

- local
- dev
- test
- cloud
- prod

---

# Environment Bean Kullanımı

`Environment` bean'i Spring tarafından hazır olarak sağlanır ve ihtiyaç halinde inject edilebilir.

```java
@Configuration
public class ApplicationConfig {

    private final Environment environment;

    public ApplicationConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public PaymentService paymentService() {
        String dbUrl = environment.getProperty("database.url");

        if (environment.acceptsProfiles(Profiles.of("cloud"))) {
            return new CloudPaymentService(dbUrl);
        }

        return new LocalPaymentService(dbUrl);
    }
}
```

Bu örnekte:

- `environment.getProperty(...)` ile property okunur.
- `environment.acceptsProfiles(...)` ile aktif profile kontrol edilir.

---

# Spring Profile Nedir?

**Profile**, belirli bean veya configuration'ların sadece belirli ortamlarda aktif olmasını sağlayan mantıksal gruplamadır.

Örneğin:

- `dev` ortamında H2 database kullanmak
- `prod` ortamında PostgreSQL kullanmak
- `test` ortamında mock servis kullanmak
- `cloud` ortamında cloud servislerini aktif etmek

---

# @Profile Kullanımı

`@Profile`, bir bean'in veya configuration class'ının sadece belirli profile aktifken kullanılmasını sağlar.

---

## 1. Component seviyesinde profile

```java
@Service
@Profile("cloud")
public class CloudPaymentService implements PaymentService {
}
```

Bu service sadece `cloud` profile aktifken Spring context'e eklenir.

---

## 2. Configuration class seviyesinde profile

```java
@Configuration
@Profile("cloud")
public class CloudConfig {

    @Bean
    public PaymentService paymentService() {
        return new CloudPaymentService();
    }
}
```

Bu configuration class sadece `cloud` profile aktifken çalışır.

---

## 3. Bean seviyesinde profile

```java
@Configuration
public class AppConfig {

    @Bean
    @Profile("cloud")
    public PaymentService cloudPaymentService() {
        return new CloudPaymentService();
    }

    @Bean
    @Profile("dev")
    public PaymentService devPaymentService() {
        return new DevPaymentService();
    }
}
```

Bu örnekte aktif profile'a göre farklı bean oluşturulur.

---

# Profile Programatik Olarak Nasıl Aktif Edilir?

Spring profile programatik olarak da aktif edilebilir.

```java
AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext();

context.getEnvironment().setActiveProfiles("cloud");
context.scan("com.example");
context.refresh();

PaymentService paymentService = context.getBean(PaymentService.class);
```

Burada:

1. Application context oluşturulur.
2. Aktif profile `cloud` olarak ayarlanır.
3. Package scan edilir.
4. Context refresh edilir.
5. Profile'a uygun bean alınır.

---

# Profile Configuration Dosyasından Nasıl Aktif Edilir?

Spring Boot uygulamalarında profile genellikle configuration dosyasından aktif edilir.

## application.properties

```properties
spring.profiles.active=cloud
```

## application.yml

```yaml
spring:
  profiles:
    active: cloud
```

Bu ayar ile uygulama `cloud` profile ile çalışır.

---

# @Value Annotation Nedir?

`@Value`, property dosyalarından, system properties'ten veya sabit değerlerden field, method veya constructor parameter seviyesinde değer inject etmek için kullanılır.

Genellikle dış konfigürasyon değerlerini class içine almak için kullanılır.

Örneğin:

- database URL
- username
- password
- API key
- region
- timeout değeri

---

# @PropertySource ile Property Dosyası Okuma

Eğer özel bir `.properties` dosyasından değer okumak istiyorsak `@PropertySource` kullanılabilir.

Örneğin `src/main/resources/database.properties` dosyamız olsun:

```properties
jdbc.url=jdbc:postgresql://localhost:5432/app
jdbc.username=postgres
jdbc.password=secret
```

Bu dosyayı configuration class içinde okuyabiliriz:

```java
@Configuration
@PropertySource("classpath:database.properties")
public class DatabaseConfig {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return new DataSource(url, username, password);
    }
}
```

Burada:

- `classpath:` ifadesi `resources` klasörünü temsil eder.
- `${jdbc.url}` ifadesi property dosyasındaki değeri okur.
- `@Value` ile değer Java field'ına inject edilir.

---

# System Properties Okuma

`@Value` sadece application property okumaz. System properties gibi kaynaklardan da değer okuyabilir.

```java
@Component
public class LocaleService {

    @Value("#{systemProperties['user.region']}")
    private String userRegion;
}
```

Bu örnekte `user.region` değeri system properties üzerinden okunur.

---

# Configuration Class'larını Bölmek

Büyük configuration class'ları zamanla yönetmesi zor hale gelir.

Bu yüzden configuration class'larını sorumluluklarına göre bölmek iyi bir pratiktir.

Örneğin:

- `ServiceConfig`
- `RepositoryConfig`
- `SecurityConfig`
- `DatabaseConfig`
- `WebConfig`

---

## Kötü örnek

```java
@Configuration
public class AppConfig {

    // service bean'leri

    // repository bean'leri

    // database bean'leri

    // security bean'leri

    // web bean'leri
}
```

Bu yapı büyüdükçe okunması zorlaşır.

---

## Daha iyi örnek

```java
@Configuration
public class ServiceConfig {

    @Bean
    public PaymentService paymentService() {
        return new DefaultPaymentService();
    }
}
```

```java
@Configuration
public class RepositoryConfig {

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository();
    }
}
```

```java
@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class AppConfig {
}
```

Bu şekilde configuration daha modüler hale gelir.

---

# @Import Annotation

`@Import`, bir configuration class içinde başka configuration class'larını dahil etmek için kullanılır.

```java
@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class AppConfig {
}
```

Bu sayede farklı configuration dosyaları bir araya getirilebilir.

---

# Spring Initializr Nedir?

**Spring Initializr**, Spring Boot projesi oluşturmak için kullanılan web tabanlı bir araçtır.

Spring Boot projesi için başlangıç dosyalarını otomatik oluşturur.

Genellikle şu adresten kullanılır:

```text
https://start.spring.io
```

---

# Spring Initializr ile Neler Seçilir?

Spring Initializr üzerinden proje oluştururken şu ayarlar yapılır:

## 1. Build tool

Projenin hangi build sistemiyle oluşturulacağı seçilir.

- Maven
- Gradle

## 2. Language

Uygulama dili seçilir.

- Java
- Kotlin
- Groovy

## 3. Spring Boot version

Kullanılacak Spring Boot versiyonu seçilir.

## 4. Project metadata

Proje bilgileri girilir.

- Group ID
- Artifact ID
- Name
- Description
- Package name

Örnek:

```text
Group: com.example
Artifact: demo
Name: demo
Package name: com.example.demo
```

## 5. Packaging

Uygulamanın nasıl paketleneceği seçilir.

- JAR
- WAR

Spring Boot için genellikle önerilen paketleme tipi **JAR**'dır.

## 6. Java version

Kullanılacak Java versiyonu seçilir.

Spring Boot 3 için minimum Java versiyonu genellikle Java 17'dir.

---

# Spring Initializr ile Oluşan Proje

Spring Initializr ile oluşturulan projede genellikle bir ana application class bulunur.

```java
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

Bu class içindeki `main` method çalıştırıldığında Spring Boot uygulaması ayağa kalkar.

Eğer `spring-boot-starter-web` dependency'si varsa uygulama default olarak şu portta çalışır:

```text
localhost:8080
```

---

# Mülakatta Sorulabilecek Sorular

## Bean scope nedir?

Bean scope, Spring bean'inin yaşam döngüsünü ve uygulama içinde hangi kapsamda kullanılacağını belirler.

## Spring'de default bean scope nedir?

Default scope `singleton`'dır. Yani application context içinde bean'den bir tane instance oluşturulur ve bu instance paylaşılır.

## Singleton scope neden dikkatli kullanılmalıdır?

Çünkü singleton bean tüm uygulama boyunca paylaşılan tek instance'tır. Mutable state tutulursa thread-safety problemleri oluşabilir.

## Prototype scope nedir?

Prototype scope, bean her istendiğinde yeni bir instance oluşturulmasıdır.

## Request scope ile session scope farkı nedir?

Request scope her HTTP request için yeni bean oluşturur. Session scope ise her HTTP session için yeni bean oluşturur ve session boyunca aynı bean kullanılabilir.

## Spring profile nedir?

Profile, belirli bean veya configuration'ların sadece belirli ortam veya koşullarda aktif olmasını sağlayan Spring mekanizmasıdır.

## @Profile ne işe yarar?

`@Profile`, bir bean'in veya configuration class'ının sadece belirli profile aktifken Spring context'e eklenmesini sağlar.

## @Value ne işe yarar?

`@Value`, property dosyalarından veya system properties'ten değerleri Java class'ına inject etmek için kullanılır.

## @PropertySource ne işe yarar?

`@PropertySource`, Spring'e özel bir properties dosyasını kaynak olarak göstermeye yarar.

## Spring Initializr ne işe yarar?

Spring Initializr, Spring Boot projesi oluşturmak için kullanılan web tabanlı bir araçtır. Build tool, Java version, dependencies ve project metadata gibi ayarlarla başlangıç projesi oluşturur.

---

# Kısa Özet

Bu bölümde Spring bean'lerinin kapsamı ve ortam bazlı configuration yönetimi anlatıldı.

Öğrenilen ana kavramlar:

- Bean scope
- Singleton scope
- Prototype scope
- Request scope
- Session scope
- Application scope
- WebSocket scope
- `@Scope`
- `@RequestScope`
- `@SessionScope`
- `Environment`
- Spring profiles
- `@Profile`
- `@Value`
- `@PropertySource`
- `@Import`
- Spring Initializr

Bu konular Spring Boot uygulamalarında bean yaşam döngüsünü, environment bazlı yapılandırmayı ve proje başlangıç ayarlarını anlamak için önemlidir.

34:50
