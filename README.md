# 📈 Crawl Chứng Khoán - News & Alerts System

Hệ thống crawl tin tức chứng khoán và cảnh báo tự động cho trader và nhà đầu tư.

## 🎯 Tính năng chính

### MVP (Minimum Viable Product)
- ✅ Crawl tin tức từ nhiều nguồn (cafef.vn, vietstock.vn, ssi.com.vn)
- ✅ Lưu trữ tiêu đề, URL, timestamp, source, tags
- ✅ Dashboard web hiển thị tin mới + filter
- ✅ Alert system: webhook/email/Telegram khi có keyword nóng
- ✅ API private trả JSON
- ✅ Authentication & User management
- ✅ Billing integration (Stripe)
- ✅ Admin panel quản lý nguồn, logs

### Gói dịch vụ
1. **FREE** - $0/tháng
   - Crawl mỗi 30-60 phút
   - Xem tin 7 ngày
   - Không có alerts

2. **PRO** - $10-20/tháng
   - Crawl mỗi 5 phút
   - Xem tin 30 ngày
   - 5 keyword alerts
   - Email/Telegram alerts

3. **PREMIUM** - $50-200/tháng
   - Crawl mỗi 1-2 phút
   - Xem tin 90 ngày
   - Unlimited alerts
   - Webhook support
   - API access với higher rate limit

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: JWT + Spring Security
- **Crawler**: Jsoup + OkHttp
- **Scheduler**: Spring @Scheduled + Quartz
- **Payment**: Stripe
- **Notification**: Telegram Bot API

### Frontend
- **Framework**: Angular 17
- **Language**: TypeScript
- **Styling**: SCSS
- **State Management**: Signals (Angular 17)
- **HTTP Client**: HttpClient with Interceptors

## 📦 Cài đặt

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Redis 6+
- Maven 3.8+

### Backend Setup

1. Clone repository
```bash
git clone <repository-url>
cd CrawlChungKhoan
```

2. Cấu hình database trong `src/main/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/crawl_chungkhoan
    username: postgres
    password: your_password
```

3. Cấu hình Redis
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

4. Build và chạy
```bash
mvn clean install
mvn spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080`

### Frontend Setup

1. Di chuyển vào thư mục frontend
```bash
cd frontend
```

2. Cài đặt dependencies
```bash
npm install
```

3. Chạy development server
```bash
npm start
```

Frontend sẽ chạy tại: `http://localhost:4200`

## 🗄️ Database Schema

### Tables
- `users` - Thông tin người dùng, subscription
- `news` - Tin tức đã crawl
- `alerts` - Cảnh báo từ khóa của user
- `crawl_sources` - Nguồn tin cần crawl
- `news_tags` - Tags của tin tức

## 🔐 Environment Variables

Tạo file `.env` hoặc cấu hình trong `application.yml`:

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/crawl_chungkhoan
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your_secret_key
JWT_EXPIRATION=86400000

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Stripe
STRIPE_API_KEY=sk_test_your_key
STRIPE_WEBHOOK_SECRET=whsec_your_secret

# Telegram
TELEGRAM_BOT_TOKEN=your_bot_token
TELEGRAM_BOT_USERNAME=your_bot_username
```

## 📡 API Endpoints

### Authentication
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập

### News
- `GET /api/news` - Lấy danh sách tin tức (có phân trang)
- `GET /api/news/search?keyword=` - Tìm kiếm tin tức
- `GET /api/news/source/{source}` - Lấy tin theo nguồn
- `GET /api/news/{id}` - Chi tiết tin tức

### Health Check
- `GET /api/health` - Kiểm tra trạng thái service

## 🚀 Deployment

### Backend (Spring Boot)
```bash
mvn clean package
java -jar target/CrawlChungKhoan-1.0.0.jar
```

### Frontend (Angular)
```bash
cd frontend
npm run build
# Deploy thư mục dist/ lên hosting
```

## 📊 Monitoring & Logs

- Application logs: `logs/application.log`
- Crawler logs: `logs/crawler.log`
- Health endpoint: `/api/health`

## 🔄 Roadmap

### Tháng 1-2: MVP
- [x] Setup cơ bản Backend + Frontend
- [ ] Implement crawler cho 3 nguồn chính
- [ ] Alert system cơ bản
- [ ] Stripe integration

### Tháng 3-4: Enhancement
- [ ] API tier với rate limiting
- [ ] Webhook support
- [ ] Mobile-friendly UI
- [ ] Performance optimization

### Tháng 5-6: Advanced Features
- [ ] Sentiment analysis (GPT/Lexicon)
- [ ] Auto-label news
- [ ] Advanced filtering

### Tháng 7+: Scale
- [ ] Backtesting signals
- [ ] User-created rules
- [ ] Signal feeds marketplace

## 📝 License

Private project - All rights reserved

## 👥 Contact

- Email: support@crawlchungkhoan.com
- Telegram: @crawlchungkhoan_support

---

**Note**: Đây là phiên bản MVP. Nhiều tính năng đang được phát triển.

