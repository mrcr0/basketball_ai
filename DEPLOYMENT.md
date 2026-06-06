# 篮球训练平台 - 系统部署文档

## 1. 项目概述

基于AI的科学智能篮球训练Web平台，提供AI训练计划生成、动作分析、实时比赛模拟、战术知识库等功能。

## 2. 技术栈

### 后端技术栈
- **框架**: Spring Boot 3.3.0
- **ORM**: MyBatis-Plus 3.5.7
- **数据库**: MySQL 8.0+ / H2 (开发环境)
- **认证**: JWT Token
- **API文档**: SpringDoc OpenAPI

### 前端技术栈
- **框架**: Vue 3 + Vite
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios

## 3. 环境要求

| 软件 | 版本要求 |
|------|----------|
| Java | 21+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0+ |

## 4. 后端部署

### 4.1 数据库配置

创建MySQL数据库：
```sql
CREATE DATABASE basketball_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'basketball'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON basketball_db.* TO 'basketball'@'localhost';
FLUSH PRIVILEGES;
```

### 4.2 修改配置文件

编辑 `backend/src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/basketball_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: basketball
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto

jwt:
  secret: your-256-bit-secret-key-here-must-be-at-least-32-characters
  expiration: 86400000

logging:
  level:
    com.example.basketball: DEBUG
```

### 4.3 编译与运行

```bash
cd backend
mvn clean package -DskipTests
java -jar target/basketball-training-1.0.0.jar
```

### 4.4 开发模式运行

```bash
cd backend
mvn spring-boot:run
```

## 5. 前端部署

### 5.1 安装依赖

```bash
cd frontend
npm install
```

### 5.2 开发模式

```bash
npm run dev
```

### 5.3 生产构建

```bash
npm run build
```

### 5.4 配置API地址

编辑 `frontend/src/utils/axios.js`:

```javascript
const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000
})
```

## 6. 服务启动验证

启动成功后，访问以下地址验证：

| 服务 | URL |
|------|-----|
| 后端API | http://localhost:8080/api/users |
| 前端页面 | http://localhost:5173 |
| Swagger文档 | http://localhost:8080/swagger-ui.html |
| H2控制台(开发) | http://localhost:8080/h2-console |

## 7. 默认管理员账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |

## 8. 备份与恢复

### 8.1 创建备份

```bash
POST /api/admin/backups/create
```

### 8.2 恢复备份

```bash
POST /api/admin/backups/{id}/restore
```

### 8.3 自动备份

系统支持定时自动备份，可在配置文件中开启：

```yaml
backup:
  enabled: true
  cron: "0 0 2 * * ?"
```

## 9. 常见问题

### 9.1 端口占用

```bash
# Windows
netstat -ano | findstr :8080
taskkill /F /PID <进程ID>

# Linux
lsof -ti:8080 | xargs kill -9
```

### 9.2 数据库连接失败

- 确认MySQL服务已启动
- 确认数据库配置信息正确
- 确认防火墙允许3306端口

### 9.3 JWT密钥问题

确保`jwt.secret`配置项至少32个字符。

## 10. 安全注意事项

1. 生产环境务必使用MySQL数据库
2. 修改默认管理员密码
3. 使用HTTPS协议
4. 配置适当的防火墙规则
5. 定期备份数据库