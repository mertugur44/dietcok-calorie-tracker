# DietCok

DietCok, İstanbul Beykent Üniversitesi Yazılım Kalite Güvencesi ve Testi dersi kapsamında geliştirdiğimiz web tabanlı bir kalori ve besin takip uygulamasıdır.

Projede sadece uygulamayı geliştirmeye değil, farklı test türlerini gerçek bir proje üzerinde uygulamaya da odaklandık. Bu nedenle test tarafı uygulamanın önemli bir bölümünü oluşturuyor.

## Proje Hakkında

Kullanıcılar sisteme kayıt olabilir, günlük kalori ve makro hedeflerini belirleyebilir, öğünlerine besin ekleyebilir ve geçmiş günlerdeki tüketimlerini görüntüleyebilir.

Uygulamada kahvaltı, öğle yemeği, akşam yemeği ve ara öğünler ayrı şekilde takip edilir. Günlük toplam kalori, protein, karbonhidrat ve yağ değerleri sistem tarafından hesaplanır.

## Temel Özellikler

- Kullanıcı kayıt ve giriş işlemleri
- Spring Security ile kimlik doğrulama
- BCrypt ile parola şifreleme
- Günlük kalori ve makro hedefi belirleme
- Öğün bazlı besin ekleme
- Günlük kalori ve makro hesaplama
- Besin arama ve seçim ekranı
- Geçmiş günleri takvim üzerinden görüntüleme
- Aylık kalori ve makro özeti
- Profil ve hedef bilgilerini güncelleme
- Eklenen besinleri silme
- Kullanıcıya ait kayıtlar için erişim kontrolü
- Form ve veri girişlerinde sunucu tarafı doğrulama

## Kullanılan Teknolojiler

### Uygulama

- Java 17
- Spring Boot 3.2
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySQL

### Test

- JUnit 5
- Mockito
- MockMvc
- Spring Boot Test
- Spring Security Test
- H2 In-Memory Database
- HtmlUnit
- Selenium WebDriver
- JaCoCo

### Proje Yapısı

Uygulama Controller, Service ve Repository katmanlarından oluşan bir yapı ile geliştirilmiştir.

```text
Controller -> Service -> Repository -> Database
```

Ana kaynak kodları aşağıdaki klasör altında bulunur.

```text
src/main/java/com/calorie/tracker/
├── config
├── controller
├── entity
├── repository
├── security
└── service
```

Testler ise `src/test/java/com/calorie/tracker/` altında test türlerine göre ayrılmıştır.

## Test Süreci

Bu proje Yazılım Kalite Güvencesi ve Testi dersi için hazırlandığı için test süreci normal bir ders projesine göre daha ayrıntılı tutuldu.

Projede aşağıdaki test türleri üzerinde çalışıldı.

- Unit Test
- Service Test
- Repository Test
- Controller Test
- Integration Test
- Security Test
- UI Test
- End-to-End Test
- Regression Test

Servis katmanındaki bağımlılıkları izole etmek için Mockito kullanıldı. Repository testleri ana MySQL veritabanından bağımsız çalışabilmesi için H2 veritabanı ile hazırlandı. Controller testlerinde MockMvc kullanıldı. Kullanıcı akışlarını tarayıcı üzerinden kontrol etmek için Selenium WebDriver ile E2E testleri eklendi.

Test kapsamını incelemek için JaCoCo kullanıldı. Ders raporunda ayrıca pozitif ve negatif test senaryoları, sınır değer analizi, eşdeğerlik bölümleme, hata kayıtları ve regresyon testleri ele alındı.

## Projeyi Çalıştırma

### Gereksinimler

- Java 17 veya üzeri
- MySQL

Veritabanı bağlantı bilgileri ortam değişkenleri üzerinden verilebilir.

```text
DB_URL=jdbc:mysql://localhost:3306/calorietracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=<MySQL şifreniz>
```

Windows CMD için örnek kullanım:

```bat
set DB_PASSWORD=your_mysql_password
mvnw.cmd spring-boot:run
```

PowerShell:

```powershell
$env:DB_PASSWORD="your_mysql_password"
.\mvnw.cmd spring-boot:run
```

macOS veya Linux:

```bash
export DB_PASSWORD=your_mysql_password
./mvnw spring-boot:run
```

Uygulama çalıştıktan sonra aşağıdaki adres üzerinden giriş ekranına ulaşılabilir.

```text
http://localhost:8080/login
```

## Testleri Çalıştırma

Standart testleri çalıştırmak için:

```bash
./mvnw test
```

Windows için:

```bat
mvnw.cmd test
```

JaCoCo raporu testlerden sonra aşağıdaki klasörde oluşturulur.

```text
target/site/jacoco/index.html
```

### Selenium E2E Testi

Selenium testi gerçek bir tarayıcı kullandığı için standart test çalıştırmasına dahil edilmedi. Bilgisayarda Chrome kuruluysa aşağıdaki komutla ayrıca çalıştırılabilir.

```bash
./mvnw -De2e=true -Dtest=SeleniumE2ETest test
```

## Katkıda Bulunanlar

- Mert Uğur
- Tolga Uslu
