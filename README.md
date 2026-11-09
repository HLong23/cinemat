# Cinemat - Web đặt vé xem phim

Spring Boot 3.3 / Java 17 · Thymeleaf + Bootstrap 5 · Spring Data JPA (H2 / MySQL) · Spring Security + JWT · AOP

## Chạy dự án

```bash
mvn spring-boot:run                                   
mvn spring-boot:run -Dspring-boot.run.profiles=mysql  
mvn test                                              
docker compose up -d --build                          
```
Trong IntelliJ: mở `CinematApplication` → Run.

- Web: http://localhost:8080
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:cinemadb`, user `sa`, mật khẩu trống)
- Tài khoản mẫu: **admin / admin123** (ADMIN), **user / user123** (USER)

## Luồng sử dụng
1. Trang chủ: tìm phim → chi tiết phim (suất chiếu sắp tới).
2. Chọn suất → đăng nhập → chọn ghế (tối đa 8) → xác nhận → "Vé của tôi" (có thể huỷ).
3. Admin (`/admin`): thống kê, nhật ký AOP, CRUD phim / rạp / phòng / suất chiếu.

Chống đặt trùng ghế: bảng `booking_seats` có ràng buộc unique `(showtime_id, seat_code)`.

## REST API (`/api`)
Đăng nhập lấy token, gửi kèm header `Authorization: Bearer <token>`.

| Method | URL | Quyền |
|---|---|---|
| POST | `/api/auth/login`, `/api/auth/register` | công khai |
| GET | `/api/movies`, `/api/movies/{id}`, `/api/cinemas`, `/api/showtimes`, `/api/showtimes/{id}/seats` | công khai |
| POST/PUT/DELETE | `/api/movies`, `/api/cinemas`, `/api/rooms`, `/api/showtimes` | ADMIN |
| GET | `/api/rooms` | đã đăng nhập |
| POST | `/api/bookings` `{ "showtimeId": 1, "seats": ["C3","C4"] }` | đã đăng nhập |
| GET | `/api/bookings/me` | đã đăng nhập |
| DELETE | `/api/bookings/{id}` | chủ vé hoặc ADMIN |
| GET | `/api/bookings` | ADMIN |

```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"user","password":"user123"}' | python -c "import sys,json;print(json.load(sys.stdin)['token'])")
curl -X POST localhost:8080/api/bookings -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"showtimeId":1,"seats":["C3","C4"]}'
```

## CI/CD
- **Jenkins local**: tạo Pipeline job trỏ tới repo (Pipeline script from SCM). Cần khai báo tool `JDK17` và `Maven3`; deploy dùng Docker (`DEPLOY_MODE=docker`) hoặc chọn `none` nếu chỉ cần build + test.
- **Heroku** (thay thế): thêm add-on MySQL (vd JawsDB), đặt biến `SPRING_PROFILES_ACTIVE=mysql`, `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`; `Procfile` và `system.properties` đã có sẵn.

## Cấu trúc
```
src/main/java/vn/edu/cinema
├── CinematApplication
├── config/      SecurityConfig, WebConfig (i18n), DataInitializer (dữ liệu mẫu)
├── security/    JwtService, JwtAuthenticationFilter, CustomUserDetailsService
├── entity/ repository/ service/ dto/ exception/
├── api/         REST controllers (/api/**)
├── controller/  MVC controllers (Thymeleaf)
└── aspect/      AOP
```
