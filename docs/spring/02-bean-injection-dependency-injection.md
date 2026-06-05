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

# Kısa Özet

Bu bölümde Spring'de bean injection yöntemleri anlatıldı.

Öğrenilen ana kavramlar:

- Bean injection
- Dependency injection
- Constructor injection
- Field injection
- Method injection
- Setter injection
- `@Qualifier`
- `@Primary`
- Type-based injection
- Name-based injection

Genel kural:

> Spring'de dependency injection için varsayılan tercihin constructor injection olmalı. Field injection'dan mümkün olduğunca kaçınmalısın.
