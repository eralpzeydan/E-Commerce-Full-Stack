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

