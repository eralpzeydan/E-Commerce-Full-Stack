# 01 - Spring Boot & Spring Data JPA Kursuna Giriş

## Bu bölümde ne anlatılıyor?

Bu bölüm, kursun genel kapsamını ve Spring ekosistemine giriş konularını anlatıyor. Kurs iki ana bölümden oluşuyor:

1. **Spring Boot**
2. **Spring Data JPA**

Amaç, modern Java backend geliştirmede kullanılan Spring Boot ve Spring Data JPA teknolojilerini öğrenmek.

---

# Spring Boot Nedir?

Spring Boot, Spring tabanlı uygulamaları daha az konfigürasyonla, hızlı ve kolay şekilde geliştirmeyi sağlayan bir framework'tür.

## Spring Boot'un Amacı

Normal Spring uygulamalarında çok fazla manuel konfigürasyon gerekebilir. Spring Boot bu süreci kolaylaştırır.

Spring Boot sayesinde:

- Standalone uygulamalar geliştirilebilir.
- Production-ready uygulamalar daha hızlı oluşturulur.
- Gereksiz boilerplate kod azalır.
- REST API geliştirme kolaylaşır.
- Dependency Injection, validation, testing, persistence gibi konular daha düzenli yönetilir.

---

# Spring Data JPA Nedir?

Spring Data JPA, Java uygulamalarında veritabanı işlemlerini kolaylaştırmak için kullanılan Spring modülüdür.

Normalde veritabanı işlemleri için çok fazla SQL veya `EntityManager` kodu yazmak gerekir. Spring Data JPA, repository yapısı sayesinde bu işlemleri sadeleştirir.

## Spring Data JPA ile Öğrenilecek Konular

Bu kursta Spring Data JPA tarafında şu konular işlenecek:

- Repository yapısı
- Entity ilişkileri
- Inheritance
- Embedded entities
- ID generation
- Named queries
- Specifications
- Advanced querying

---

# Spring Framework Nedir?

Spring Framework, enterprise seviyede Java uygulamaları geliştirmek için kullanılan açık kaynaklı bir framework'tür.

Spring'in temel amacı, karmaşık Java enterprise uygulama geliştirme sürecini basitleştirmektir.

Spring Framework şunları sağlar:

- Dependency Injection
- Inversion of Control
- Aspect Oriented Programming
- Spring MVC
- Data access desteği
- Transaction management
- Daha modüler ve test edilebilir kod yapısı

---

# Spring Framework'ün Temel Özellikleri

## 1. IoC Container

IoC, **Inversion of Control** anlamına gelir.

Spring'de nesnelerin oluşturulması, yönetilmesi ve birbirine bağlanması Spring Container tarafından yapılır.

Normalde bir class içinde başka bir class'a ihtiyaç duyarsak kendimiz `new` ile nesne oluşturabiliriz.

```java
PaymentService paymentService = new PaymentService();
```

Fakat Spring'de bu nesne yönetimini framework üstlenir.

### IoC Container'ın Görevi

Spring IoC Container:

- Bean'leri oluşturur.
- Bean'lerin yaşam döngüsünü yönetir.
- Dependency Injection yapar.
- Nesneleri birbirine bağlar.
- Uygulamadaki object yönetimini merkezi hale getirir.

---

# Dependency Injection Nedir?

Dependency Injection, bir class'ın ihtiyaç duyduğu bağımlılıkların dışarıdan verilmesidir.

Örneğin `PaymentService`, `AccountRepository` kullanıyorsa bu bağımlılığı kendi içinde oluşturmak yerine dışarıdan alır.

```java
public class PaymentService {

    private final AccountRepository accountRepository;

    public PaymentService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

Bu yaklaşım sayesinde kod:

- Daha test edilebilir olur.
- Daha esnek olur.
- Bağımlılıklar daha rahat yönetilir.
- Class'lar birbirine daha az sıkı bağlı olur.

---

## 2. AOP - Aspect Oriented Programming

AOP, **Aspect Oriented Programming** anlamına gelir.

AOP'nin amacı, uygulamanın farklı yerlerinde tekrar eden ortak işlemleri merkezi hale getirmektir.

Bu ortak işlemlere **cross-cutting concerns** denir.

### Cross-cutting Concern Örnekleri

- Logging
- Caching
- Transaction management
- Authentication
- Authorization
- Exception handling

Örneğin her service metodunda log yazmak yerine, AOP ile bu işlem merkezi olarak yönetilebilir.

---

## 3. Data Access Framework

Spring, veritabanı işlemlerini kolaylaştırmak için çeşitli veri erişim teknolojilerini destekler.

Desteklenen teknolojilere örnekler:

- JDBC
- Hibernate
- JPA

Spring bu teknolojilerle çalışırken şunları da kolaylaştırır:

- Resource management
- Exception handling
- Transaction management
- Database connection yönetimi

---

## 4. Spring MVC

Spring MVC, web uygulamaları ve REST API'ler geliştirmek için kullanılan Spring modülüdür.

MVC şu anlama gelir:

- **Model**
- **View**
- **Controller**

Spring MVC, request-based bir framework'tür. Yani kullanıcıdan veya client'tan gelen HTTP request'leri işler.

### Spring MVC'de Temel Akış

1. Client bir request gönderir.
2. Request önce `DispatcherServlet` tarafından karşılanır.
3. `DispatcherServlet` uygun controller'ı bulur.
4. Controller request'i işler.
5. Gerekirse model oluşturulur.
6. Response client'a döner.

### DispatcherServlet Nedir?

`DispatcherServlet`, Spring MVC'nin merkezi bileşenidir.

Görevi gelen HTTP request'i karşılamak ve doğru controller'a yönlendirmektir.

---

# Spring Bean Nedir?

Spring Bean, Spring Container tarafından yönetilen Java nesnesidir.

Yani bir nesne Spring tarafından oluşturuluyor, konfigüre ediliyor ve yaşam döngüsü yönetiliyorsa buna **bean** denir.

## Spring Bean'in Özellikleri

Spring Bean:

- Spring Container tarafından oluşturulur.
- Spring tarafından yönetilir.
- Dependency Injection ile diğer bean'lere bağlanabilir.
- Yaşam döngüsü Spring tarafından kontrol edilir.

---

# Bean Life Cycle

Bean life cycle, bir bean'in oluşturulmasından yok edilmesine kadar geçen süreci ifade eder.

## Bean Yaşam Döngüsü Genel Olarak Şöyledir

1. Spring Container başlatılır.
2. Bean instance'ı oluşturulur.
3. Bean'in bağımlılıkları inject edilir.
4. Bean kullanıma hazır hale gelir.
5. Uygulama çalıştığı sürece bean kullanılır.
6. Spring Container kapanırken bean destroy edilir.

---

# Bean Nasıl Tanımlanır?

Spring'de bean tanımlamanın birden fazla yolu vardır.

Bu bölümde özellikle şu iki annotation anlatılıyor:

- `@Configuration`
- `@Bean`

---

## @Configuration

`@Configuration`, bir class'ın Spring configuration class'ı olduğunu belirtir.

Bu class içinde bean tanımları yapılabilir.

```java
@Configuration
public class AppConfig {

}
```

Bu class genellikle public ve non-final olmalıdır.

---

## @Bean

`@Bean`, bir metodun Spring Container'a bean olarak kaydedileceğini belirtir.

```java
@Configuration
public class AppConfig {

    @Bean
    public PaymentService paymentService() {
        return new PaymentServiceImpl();
    }
}
```

Bu örnekte `paymentService()` metodunun döndürdüğü nesne Spring Bean olarak kaydedilir.

---

# Bean Dependency Örneği

Bir bean başka bir bean'e bağımlı olabilir.

```java
@Configuration
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        return new DataSource();
    }

    @Bean
    public AccountRepository accountRepository(DataSource dataSource) {
        return new AccountRepository(dataSource);
    }

    @Bean
    public PaymentService paymentService(AccountRepository accountRepository) {
        return new PaymentServiceImpl(accountRepository);
    }
}
```

Bu örnekte Spring sırasıyla:

1. `DataSource` bean'ini oluşturur.
2. `AccountRepository` bean'ini oluştururken `DataSource` inject eder.
3. `PaymentService` bean'ini oluştururken `AccountRepository` inject eder.

Burada bağımlılık zincirini Spring kendisi çözer.

---

# @Component Nedir?

`@Component`, bir class'ın Spring tarafından bean olarak algılanmasını sağlar.

```java
@Component
public class PaymentServiceImpl implements PaymentService {

    private final AccountRepository accountRepository;

    public PaymentServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

Bu class `@Component` ile işaretlendiği için Spring bunu otomatik olarak bean yapar.

---

# Constructor Injection

Constructor injection, bağımlılıkların constructor üzerinden verilmesidir.

Spring'de önerilen dependency injection yöntemi genellikle constructor injection'dır.

```java
@Component
public class PaymentServiceImpl implements PaymentService {

    private final AccountRepository accountRepository;

    public PaymentServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

Eğer class'ta sadece bir constructor varsa `@Autowired` yazmak zorunlu değildir. Spring otomatik olarak constructor injection yapabilir.

---

# Stereotype Annotation'lar

Spring'de bazı özel component annotation'lar vardır.

Bunlar aslında `@Component` temellidir ama class'ın görevini daha net ifade eder.

## Temel Stereotype Annotation'lar

| Annotation | Kullanım Amacı |
|---|---|
| `@Component` | Genel amaçlı Spring bean |
| `@Service` | Business logic içeren service class'ları |
| `@Repository` | Veritabanı erişim katmanı |
| `@Controller` | MVC controller class'ları |
| `@RestController` | REST API controller class'ları |

---

# Bean Naming

Spring bean'lere otomatik olarak isim verir.

Eğer özel bir isim verilmezse, bean ismi genellikle method adından veya class adından türetilir.

## @Bean ile İsimlendirme

```java
@Bean
public PaymentService paymentService() {
    return new PaymentServiceImpl();
}
```

Bu bean'in adı:

```text
paymentService
```

olur.

## Özel Bean İsmi Verme

```java
@Bean(name = "ds")
public DataSource dataSource() {
    return new DataSource();
}
```

Bu durumda bean'in adı:

```text
ds
```

olur.

Bean isimlendirme özellikle application context içerisinden belirli bir bean'i programatik olarak almak istediğimizde önemlidir.

---

# Mülakatta Sorulabilecek Sorular

## Spring Framework nedir?

Spring Framework, Java ile enterprise uygulamalar geliştirmek için kullanılan açık kaynaklı bir framework'tür. Dependency Injection, IoC Container, Spring MVC, data access ve AOP gibi özellikler sağlar.

## IoC Container nedir?

IoC Container, Spring'de bean'lerin oluşturulmasından, bağımlılıklarının inject edilmesinden ve yaşam döngülerinin yönetilmesinden sorumlu yapıdır.

## Spring Bean nedir?

Spring Bean, Spring Container tarafından oluşturulan ve yönetilen Java nesnesidir.

## Dependency Injection nedir?

Dependency Injection, bir class'ın ihtiyaç duyduğu bağımlılıkları kendisinin oluşturması yerine dışarıdan almasıdır. Bu sayede kod daha esnek, test edilebilir ve sürdürülebilir olur.

## @Component ile @Bean farkı nedir?

`@Component`, class seviyesinde kullanılır ve Spring'in o class'ı otomatik olarak bean olarak algılamasını sağlar.

`@Bean` ise method seviyesinde kullanılır ve genellikle `@Configuration` class'ı içinde manuel bean tanımlamak için tercih edilir.

## @Service, @Repository ve @Controller neden kullanılır?

Bu annotation'lar class'ın uygulama içindeki rolünü belirtmek için kullanılır.

- `@Service`: Business logic
- `@Repository`: Database access
- `@Controller`: Web/MVC katmanı

---
1) BUILD'A BASTIĞINDA
────────────────────────────────────

Senin proje dosyaların
.java dosyaları
pom.xml
application.properties
        ↓

Maven / Gradle başlar
        ↓

pom.xml okunur
        ↓

Dependency'ler kontrol edilir
Örn:
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- postgresql driver
- lombok
        ↓

Eksik dependency varsa indirilir
Daha önce indiyse lokalden alınır
~/.m2/repository
        ↓

Java compiler çalışır
        ↓

.java dosyaları .class dosyalarına çevrilir
        ↓

Testler çalışabilir
        ↓

Kodda compile hatası var mı kontrol edilir
        ↓

target/ klasörü oluşur
        ↓

.jar dosyası oluşturulur
        ↓

BUILD BİTER

Build sonunda uygulama çalışmıyor. Sadece çalıştırılabilir hale geliyor.

Build sonucu:

src/main/java/OrderService.java
        ↓
target/classes/OrderService.class

ve en sonda:

target/ecommerce-app.jar

2) RUN'A BASTIĞINDA
────────────────────────────────────

.jar veya classpath çalıştırılır
        ↓

JVM başlar
        ↓

main() metodu bulunur
        ↓

Spring Boot main class çalışır

@SpringBootApplication
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
        ↓

SpringApplication.run(...) çalışır
        ↓

Spring Container / ApplicationContext oluşturulur
        ↓

application.properties / application.yml okunur
        ↓

Component Scan yapılır
Spring class'ları tarar
        ↓

Şunlar bulunur:
- @RestController
- @Service
- @Repository
- @Component
- @Configuration
        ↓

Bean'ler oluşturulur
Örn:
- OrderController bean
- OrderService bean
- ProductService bean
- SecurityConfig bean
        ↓

Dependency Injection yapılır
Örn:
OrderController içine OrderService verilir
OrderService içine OrderRepository verilir
        ↓

Spring Data JPA repository proxy'leri oluşturulur
Örn:
ProductRepository interface'inin runtime implementation'ı hazırlanır
        ↓

Hibernate / JPA ayarlanır
        ↓

Entity class'ları okunur
Örn:
- User
- Product
- Order
- CartItem
        ↓

Database connection ayarları hazırlanır
        ↓

Connection pool hazırlanır
        ↓

Embedded Tomcat başlar
        ↓

Port açılır
Örn:
localhost:8080
        ↓

Endpoint'ler hazır hale gelir
        ↓

Uygulama request beklemeye başlar
        ↓

RUN BİTER / APP ÇALIŞIYOR

BUILD
────────────────────
.java
 ↓
.class
 ↓
.jar


RUN
────────────────────
.jar çalışır
 ↓
JVM başlar
 ↓
main() çalışır
 ↓
Spring başlar
 ↓
Bean'ler oluşur
 ↓
Dependency Injection yapılır
 ↓
Hibernate/JPA hazırlanır
 ↓
Tomcat başlar
 ↓
API request bekler

13:26

# 02 - Bean Injection ve Dependency Injection Yöntemleri

## Bu bölümde ne anlatılıyor?

Bu bölümde Spring Framework içinde **bean injection** konusu anlatılıyor.

Spring'de bean injection dediğimiz şey aslında çoğu zaman **Dependency Injection** anlamına gelir. Yani bir class'ın ihtiyaç duyduğu bağımlılıkların Spring Container tarafından o class'a verilmesidir.

Bu bölümde dört temel injection yöntemi anlatılıyor:

1. Constructor Injection
2. Field Injection
3. Method Injection
4. Setter Injection

---

# Bean Injection Nedir?

Bean injection, Spring Container'ın bir bean'in ihtiyaç duyduğu başka bean'leri ona vermesidir.

Örneğin `PaymentService`, `AccountRepository` kullanıyorsa `AccountRepository` nesnesini manuel olarak `new` ile oluşturmak yerine Spring'in inject etmesini isteriz.

```java
@Service
public class DefaultPaymentService {

    private final AccountRepository accountRepository;

    public DefaultPaymentService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

Burada `DefaultPaymentService`, `AccountRepository` bağımlılığına sahiptir. Spring, `AccountRepository` bean'ini bulur ve constructor üzerinden service class'ına verir.

---

# Spring'de Injection Yöntemleri

Spring Framework dependency injection için temel olarak dört farklı yöntem sunar.

## 1. Constructor Injection

Constructor injection, bağımlılıkların class constructor'ı üzerinden verilmesidir.

Bu yöntemde bağımlılık class oluşturulurken verilir.

```java
@Service
public class DefaultPaymentService {

    private final AccountRepository accountRepository;

    public DefaultPaymentService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

## Constructor Injection'ın avantajları

- Zorunlu bağımlılıkları net gösterir.
- `final` field kullanımına izin verir.
- Nesne eksik dependency ile oluşturulamaz.
- Test yazmak daha kolaydır.
- Spring'in önerdiği dependency injection yöntemidir.

---

# Constructor Injection Repository Örneği

Bir repository class'ı da constructor injection ile başka bir bağımlılık alabilir.

```java
@Repository
public class JdbcAccountRepository implements AccountRepository {

    private final DataSource dataSource;

    public JdbcAccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
```

Bu örnekte `JdbcAccountRepository`, `DataSource` kullanır.

Spring:

1. `DataSource` bean'ini bulur.
2. `JdbcAccountRepository` oluştururken constructor'a inject eder.

---

# Aynı Type'ta Birden Fazla Bean Olursa Ne Olur?

Bazen uygulamada aynı interface'i implement eden veya aynı type'a sahip birden fazla bean olabilir.

Örneğin iki farklı `AccountRepository` bean'i olduğunu düşünelim:

```java
@Configuration
public class ApplicationConfig {

    @Bean
    public AccountRepository primaryAccountRepository() {
        return new JdbcAccountRepository();
    }

    @Bean
    public AccountRepository secondaryAccountRepository() {
        return new InMemoryAccountRepository();
    }
}
```

Bu durumda Spring, `AccountRepository` istendiğinde hangisini inject edeceğini bilemeyebilir.

Bu problemi çözmek için iki önemli annotation kullanılır:

- `@Qualifier`
- `@Primary`

---

# @Qualifier Nedir?

`@Qualifier`, aynı type'ta birden fazla bean olduğunda hangi bean'in inject edileceğini belirtmek için kullanılır.

## Bean tanımlarken qualifier verme

```java
@Configuration
public class ApplicationConfig {

    @Bean
    @Qualifier("primary")
    public AccountRepository primaryAccountRepository() {
        return new JdbcAccountRepository();
    }

    @Bean
    @Qualifier("secondary")
    public AccountRepository secondaryAccountRepository() {
        return new InMemoryAccountRepository();
    }
}
```

## Injection sırasında qualifier kullanma

```java
@Service
public class DefaultPaymentService {

    private final AccountRepository accountRepository;

    public DefaultPaymentService(
            @Qualifier("primary") AccountRepository accountRepository
    ) {
        this.accountRepository = accountRepository;
    }
}
```

Bu örnekte Spring, `AccountRepository` type'ındaki bean'ler arasından qualifier değeri `primary` olan bean'i inject eder.

---

# @Primary Nedir?

`@Primary`, aynı type'ta birden fazla bean olduğunda varsayılan olarak hangi bean'in tercih edileceğini belirtir.

```java
@Configuration
public class ApplicationConfig {

    @Bean
    @Primary
    public AccountRepository primaryAccountRepository() {
        return new JdbcAccountRepository();
    }

    @Bean
    public AccountRepository secondaryAccountRepository() {
        return new InMemoryAccountRepository();
    }
}
```

Bu durumda herhangi bir `@Qualifier` kullanılmazsa Spring otomatik olarak `@Primary` ile işaretlenen bean'i inject eder.

```java
@Service
public class DefaultPaymentService {

    private final AccountRepository accountRepository;

    public DefaultPaymentService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

Burada `AccountRepository` için `primaryAccountRepository` inject edilir.

---

# @Qualifier ve @Primary Önceliği

Eğer hem `@Primary` hem de `@Qualifier` kullanılırsa, `@Qualifier` daha spesifik olduğu için hangi bean'in seçileceğini o belirler.

Yani:

- `@Primary`: Varsayılan bean'i belirler.
- `@Qualifier`: Spesifik olarak hangi bean'in inject edileceğini söyler.

---

# 2. Field Injection

Field injection, bağımlılığın doğrudan field üzerine inject edilmesidir.

```java
@Service
public class DefaultPaymentService {

    @Autowired
    private AccountRepository accountRepository;
}
```

Bu yöntemde constructor veya setter yazılmaz. Spring, reflection kullanarak field'a dependency inject eder.

## Field Injection Neden Önerilmez?

Field injection genellikle önerilmez.

Çünkü:

- Test yazmayı zorlaştırır.
- Dependency'ler class dışından net görünmez.
- `final` field kullanılamaz.
- Nesne Spring dışında oluşturulduğunda dependency eksik kalabilir.
- Class'ın gerçekten neye bağımlı olduğu constructor imzasından anlaşılmaz.

Bu yüzden field injection daha çok test class'larında veya çok özel durumlarda kullanılmalıdır.

---

# 3. Method Injection

Method injection, bağımlılıkların herhangi bir method üzerinden verilmesidir.

Bir method birden fazla dependency alabilir.

```java
@Service
public class DefaultPaymentService {

    private AccountRepository accountRepository;
    private FeeCalculator feeCalculator;

    @Autowired
    public void configure(
            AccountRepository accountRepository,
            FeeCalculator feeCalculator
    ) {
        this.accountRepository = accountRepository;
        this.feeCalculator = feeCalculator;
    }
}
```

Spring, `@Autowired` ile işaretlenen method'u çağırır ve parametrelerdeki bean'leri inject eder.

## Method Injection Ne İşe Yarar?

Method injection:

- Birden fazla dependency'yi tek method ile almak için kullanılabilir.
- Dependency geldikten sonra ek initialization işlemleri yapılacaksa tercih edilebilir.
- Constructor kadar yaygın değildir.

---

# 4. Setter Injection

Setter injection, dependency'nin setter method üzerinden verilmesidir.

```java
@Service
public class DefaultPaymentService {

    private AccountRepository accountRepository;

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

Bu yöntem Java Bean naming convention yapısına uygundur.

Yani method adı genelde şu formatta olur:

```text
set + FieldName
```

Örneğin:

```text
setAccountRepository
```

---

# Constructor Injection mı Setter Injection mı?

Spring'in resmi önerisine göre:

- **Zorunlu dependency'ler için constructor injection**
- **Opsiyonel dependency'ler için setter injection veya configuration method injection**

kullanılmalıdır.

## Zorunlu dependency örneği

`PaymentService` çalışmak için mutlaka `AccountRepository`'ye ihtiyaç duyuyorsa constructor injection kullanılmalıdır.

```java
@Service
public class PaymentService {

    private final AccountRepository accountRepository;

    public PaymentService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

## Opsiyonel dependency örneği

Bir dependency varsa kullanılsın, yoksa class yine de çalışabilsin istiyorsak setter injection tercih edilebilir.

```java
@Service
public class PaymentService {

    private NotificationService notificationService;

    @Autowired(required = false)
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
```

---

# Type Injection ve Name Injection

Spring dependency inject ederken iki farklı yaklaşımla bean bulabilir.

## Type Injection

Type injection, Spring'in dependency'yi type'a göre bulmasıdır.

```java
public DefaultPaymentService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
}
```

Burada Spring, `AccountRepository` type'ında bir bean arar.

Eğer sadece bir tane varsa onu inject eder.

---

## Name Injection

Name injection, Spring'in dependency'yi bean adına göre bulmasıdır.

Özellikle aynı type'ta birden fazla bean varsa isim önem kazanır.

```java
public DefaultPaymentService(
        @Qualifier("primary") AccountRepository accountRepository
) {
    this.accountRepository = accountRepository;
}
```

Burada Spring sadece `primary` isimli/qualifier'lı bean'i inject eder.

---

# Özet Tablo

| Injection Türü | Nasıl Çalışır? | Öneri |
|---|---|---|
| Constructor Injection | Dependency constructor parametresi olarak verilir. | En çok önerilen yöntemdir. |
| Field Injection | Dependency direkt field üzerine inject edilir. | Genellikle önerilmez. |
| Method Injection | Dependency herhangi bir method parametresiyle verilir. | Özel initialization durumlarında kullanılabilir. |
| Setter Injection | Dependency setter method ile verilir. | Opsiyonel dependency için kullanılabilir. |

---

# Mülakatta Sorulabilecek Sorular

## Dependency Injection nedir?

Dependency Injection, bir class'ın ihtiyaç duyduğu bağımlılıkları kendisinin oluşturması yerine dışarıdan almasıdır. Spring'de bu bağımlılıklar genellikle Spring Container tarafından inject edilir.

---

## Constructor Injection neden önerilir?

Constructor injection önerilir çünkü zorunlu dependency'leri açıkça gösterir, `final` field kullanımına izin verir, test yazmayı kolaylaştırır ve nesnenin eksik dependency ile oluşturulmasını engeller.

---

## Field Injection neden önerilmez?

Field injection önerilmez çünkü dependency'ler class'ın constructor'ında görünmez, test yazmayı zorlaştırır, `final` field kullanılamaz ve class Spring dışında oluşturulduğunda dependency eksik kalabilir.

---

## @Qualifier ne işe yarar?

`@Qualifier`, aynı type'ta birden fazla bean olduğunda hangi bean'in inject edileceğini belirtmek için kullanılır.

---

## @Primary ne işe yarar?

`@Primary`, aynı type'ta birden fazla bean olduğunda varsayılan olarak hangi bean'in seçileceğini belirtir.

---

## @Primary ve @Qualifier birlikte kullanılırsa hangisi baskındır?

`@Qualifier` daha spesifiktir. Bu yüzden injection noktasında `@Qualifier` kullanılmışsa Spring onu dikkate alır. `@Primary` sadece varsayılan seçim için kullanılır.

---

## Setter injection ne zaman kullanılır?

Setter injection genellikle opsiyonel dependency'ler için kullanılır. Yani dependency olmadan da class çalışabiliyorsa setter injection tercih edilebilir.

---

21:57



