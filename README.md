# Expense Competition Platform

Bu proje, arkadaş grupları arasında tasarruf odaklı bir yarışma platformu sunmaktadır. Kullanıcılar gruplar oluşturarak harcamalarını takip edebilir ve en az harcama yapan kişinin kazandığı eğlenceli bir rekabet ortamına katılabilirler.

## Proje Amacı

Expense Competition Platform, insanların tasarruf alışkanlıklarını eğlenceli bir şekilde geliştirmeyi amaçlamaktadır. Platform şu özellikleri sunmaktadır:

- Kullanıcılar arkadaşlarıyla gruplar oluşturabilir
- Harcamalarını kategorize ederek kaydedebilir
- Grup içi harcama sıralamaları ile rekabet edebilir
- En az harcama yapan kullanıcılar ödüllendirilir
- Haftalık ve aylık periyotlarda yarışmalar düzenlenebilir
- Harcamalar grup üyeleri arasında bölüştürülebilir

## Kullanılan Teknolojiler

### Backend

- **Spring Boot (v2.7.17)**: Ana framework olarak kullanıldı
  - Spring Web: REST API'ler için
  - Spring Data JPA: Veritabanı işlemleri için
  - Spring Security: Güvenlik ve kimlik doğrulama için

- **MySQL**: Veritabanı olarak kullanıldı
  - Kullanıcı bilgileri
  - Grup verileri
  - Harcama kayıtları
  - Arkadaşlık ilişkileri
  - Yarışma sonuçları

- **Hibernate**: ORM (Object Relational Mapping) için kullanıldı
  - Entity yönetimi
  - Veritabanı ilişkileri
  - CRUD operasyonları

### Frontend

- **HTML5/CSS3**: Temel yapı ve stil için
- **JavaScript**: Dinamik işlevsellik için
- **Bootstrap 5**: Responsive tasarım için
- **Font Awesome**: İkonlar için
- **jQuery**: DOM manipülasyonu ve AJAX istekleri için

### Güvenlik

- **BCrypt**: Şifre hashleme için
- **JWT**: Token tabanlı kimlik doğrulama için (planlanıyor)

### Veritabanı Şeması

Temel tablolar:
- Users (Kullanıcılar)
- Groups (Gruplar)
- Expenses (Harcamalar)
- Friendships (Arkadaşlıklar)
- GroupRankings (Grup Sıralamaları)

## Özellikler

1. **Kullanıcı Yönetimi**
   - Kayıt ve giriş
   - Profil yönetimi
   - Arkadaş ekleme/çıkarma

2. **Grup Yönetimi**
   - Grup oluşturma
   - Üye ekleme/çıkarma
   - Admin yetkilendirme

3. **Harcama Yönetimi**
   - Harcama ekleme/silme
   - Kategori bazlı takip
   - Harcama bölüştürme

4. **Yarışma Sistemi**
   - Haftalık/Aylık yarışmalar
   - Sıralama tablosu
   - En az harcayan kazanır prensibi

## Kurulum

1. MySQL veritabanını kurun ve yapılandırın
2. application.properties dosyasını düzenleyin
3. Maven ile bağımlılıkları yükleyin
4. Spring Boot uygulamasını başlatın

```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints

- `POST /api/auth/register`: Yeni kullanıcı kaydı
- `POST /api/auth/login`: Kullanıcı girişi
- `GET /api/groups`: Kullanıcının gruplarını listeler
- `POST /api/expenses`: Yeni harcama ekler
- `GET /api/expenses/ranking`: Grup sıralamalarını getirir

## Katkıda Bulunma

1. Bu repository'yi fork edin
2. Yeni bir branch oluşturun
3. Değişikliklerinizi commit edin
4. Branch'inizi push edin
5. Pull request oluşturun

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır.
