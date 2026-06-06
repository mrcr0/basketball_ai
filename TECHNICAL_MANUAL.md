# 篮球训练平台 - 技术维护文档

## 1. 项目结构

```
basketball/
├── backend/                    # 后端代码
│   ├── src/main/java/com/example/basketball/
│   │   ├── controller/         # REST API控制层
│   │   ├── service/            # 业务逻辑层接口
│   │   ├── impl/               # 业务逻辑层实现
│   │   ├── mapper/             # 数据访问层
│   │   ├── entity/             # 数据库实体
│   │   ├── dto/                # 数据传输对象
│   │   ├── config/             # 配置类
│   │   ├── util/               # 工具类
│   │   └── ai/                 # AI相关模块
│   ├── src/main/resources/
│   │   ├── application.yml     # 应用配置
│   │   ├── schema.sql          # 数据库初始化DDL
│   │   └── data.sql            # 初始化数据
│   └── pom.xml                 # Maven配置
├── frontend/                   # 前端代码
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── components/         # 公共组件
│   │   ├── store/              # Pinia状态管理
│   │   ├── router/             # 路由配置
│   │   ├── utils/              # 工具函数
│   │   └── App.vue             # 根组件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── DEPLOYMENT.md               # 部署文档
├── USER_MANUAL.md              # 用户手册
└── TECHNICAL_MANUAL.md         # 技术维护文档（本文件）
```

## 2. 核心模块说明

### 2.1 用户认证模块

**文件位置**:
- `controller/UserController.java`
- `service/UserService.java`
- `impl/UserServiceImpl.java`
- `util/JwtUtil.java`

**功能说明**:
- 用户注册、登录、登出
- JWT Token生成与验证
- 密码加密与验证

**关键流程**:
1. 用户登录 → 验证用户名密码 → 生成Token → 返回Token
2. 请求携带Token → 拦截器验证Token → 提取用户信息

### 2.2 训练计划模块

**文件位置**:
- `controller/TrainingPlanController.java`
- `controller/AIQAController.java`
- `service/BasketballPlanService.java`
- `entity/BasketballPlan.java`
- `entity/TrainingTemplate.java`

**功能说明**:
- AI生成个性化训练计划
- 训练计划管理
- 训练模板管理

### 2.3 动作库模块

**文件位置**:
- `controller/BasketballActionController.java`
- `service/BasketballActionService.java`
- `entity/BasketballAction.java`

**功能说明**:
- 动作分类管理
- 动作详情展示
- 难度等级划分

### 2.4 比赛模拟模块

**文件位置**:
- `controller/GameMatchController.java`
- `service/GameMatchService.java`
- `entity/GameMatch.java`

**功能说明**:
- 实时比赛模拟
- 比赛数据统计
- 动态数据推送

### 2.5 社区模块

**文件位置**:
- `controller/DynamicController.java`
- `service/DynamicService.java`
- `entity/Dynamic.java`
- `entity/Comment.java`

**功能说明**:
- 动态发布与审核
- 评论与点赞
- 内容管理

### 2.6 战术知识库模块

**文件位置**:
- `controller/TacticInfoController.java`
- `service/TacticInfoService.java`
- `entity/TacticInfo.java`

**功能说明**:
- 战术文章管理
- 分类浏览
- 阅读统计

### 2.7 日志模块

**文件位置**:
- `entity/LoginLog.java`
- `entity/OperationLog.java`
- `service/LogService.java`
- `config/OperationLogInterceptor.java`

**功能说明**:
- 登录日志记录
- 操作日志自动记录
- 日志查询与统计

### 2.8 数据备份模块

**文件位置**:
- `controller/DataBackupController.java`
- `controller/AdminController.java`
- `service/DataBackupService.java`
- `entity/DataBackup.java`

**功能说明**:
- 数据库备份
- 数据恢复
- 备份管理

### 2.9 管理员模块

**文件位置**:
- `controller/AdminController.java`

**功能说明**:
- 用户管理
- 内容审核
- 数据统计
- 系统监控

## 3. 数据库设计

### 3.1 用户表 (app_user)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名 |
| password | VARCHAR(255) | 密码(加密) |
| nickname | VARCHAR(50) | 昵称 |
| position | VARCHAR(20) | 位置 |
| avatar | VARCHAR(255) | 头像URL |
| role | VARCHAR(20) | 角色(user/admin) |
| status | INT | 状态(0禁用/1启用) |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

### 3.2 训练计划表 (basketball_plan)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| plan_name | VARCHAR(100) | 计划名称 |
| plan_content | TEXT | 计划内容(JSON) |
| duration | INT | 时长(分钟) |
| difficulty | VARCHAR(20) | 难度 |
| created_at | TIMESTAMP | 创建时间 |

### 3.3 动态表 (dynamic)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| content | TEXT | 内容 |
| images | VARCHAR(1000) | 图片URL(逗号分隔) |
| like_count | INT | 点赞数 |
| comment_count | INT | 评论数 |
| is_approved | INT | 是否审核(0待审核/1通过) |
| created_at | TIMESTAMP | 创建时间 |

### 3.4 登录日志表 (login_log)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| username | VARCHAR(50) | 用户名 |
| login_time | TIMESTAMP | 登录时间 |
| login_ip | VARCHAR(50) | 登录IP |
| user_agent | VARCHAR(500) | 浏览器信息 |
| login_status | VARCHAR(20) | 登录状态(SUCCESS/FAILED) |
| error_message | VARCHAR(500) | 错误信息 |

### 3.5 操作日志表 (operation_log)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| username | VARCHAR(50) | 用户名 |
| operation_time | TIMESTAMP | 操作时间 |
| operation_type | VARCHAR(50) | 操作类型 |
| operation_module | VARCHAR(50) | 操作模块 |
| operation_desc | VARCHAR(500) | 操作描述 |
| request_url | VARCHAR(500) | 请求URL |
| request_method | VARCHAR(10) | 请求方法 |
| request_params | TEXT | 请求参数 |
| response_code | INT | 响应码 |
| response_message | VARCHAR(500) | 响应信息 |
| operation_ip | VARCHAR(50) | 操作IP |

### 3.6 数据备份表 (data_backup)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| backup_name | VARCHAR(100) | 备份名称 |
| backup_type | VARCHAR(20) | 备份类型(full/incremental) |
| backup_path | VARCHAR(500) | 备份文件路径 |
| file_size | BIGINT | 文件大小 |
| status | VARCHAR(20) | 状态(completed/failed) |
| backup_time | TIMESTAMP | 备份时间 |
| restore_time | TIMESTAMP | 恢复时间 |

## 4. API接口清单

### 4.1 用户接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/users/register | 用户注册 | 公开 |
| POST | /api/users/login | 用户登录 | 公开 |
| GET | /api/users/profile | 获取用户信息 | 登录 |
| PUT | /api/users/profile | 更新用户信息 | 登录 |
| PUT | /api/users/password | 修改密码 | 登录 |

### 4.2 训练计划接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/ai/generate-plan | AI生成训练计划 | 登录 |
| GET | /api/plans | 获取用户训练计划列表 | 登录 |
| GET | /api/plans/{id} | 获取训练计划详情 | 登录 |
| POST | /api/plans | 创建训练计划 | 登录 |
| PUT | /api/plans/{id} | 更新训练计划 | 登录 |
| DELETE | /api/plans/{id} | 删除训练计划 | 登录 |

### 4.3 动作库接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/actions | 获取动作列表 | 公开 |
| GET | /api/actions/{id} | 获取动作详情 | 公开 |
| POST | /api/actions | 创建动作 | 管理员 |
| PUT | /api/actions/{id} | 更新动作 | 管理员 |
| DELETE | /api/actions/{id} | 删除动作 | 管理员 |

### 4.4 比赛接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/matches | 创建比赛 | 登录 |
| GET | /api/matches | 获取比赛列表 | 登录 |
| GET | /api/matches/{id} | 获取比赛详情 | 登录 |
| DELETE | /api/matches/{id} | 删除比赛 | 登录 |

### 4.5 动态接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /api/dynamics | 发布动态 | 登录 |
| GET | /api/dynamics/list | 获取动态列表 | 公开 |
| GET | /api/dynamics/{id} | 获取动态详情 | 公开 |
| POST | /api/dynamics/{id}/like | 点赞 | 登录 |
| POST | /api/dynamics/{id}/comments | 评论 | 登录 |

### 4.6 战术资讯接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/tactics | 获取战术列表 | 公开 |
| GET | /api/tactics/{id} | 获取战术详情 | 公开 |
| POST | /api/tactics | 创建战术文章 | 管理员 |
| PUT | /api/tactics/{id} | 更新战术文章 | 管理员 |
| DELETE | /api/tactics/{id} | 删除战术文章 | 管理员 |

### 4.7 管理员接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | /api/admin/dashboard | 数据看板 | 管理员 |
| GET | /api/admin/users | 获取用户列表 | 管理员 |
| PUT | /api/admin/users/{id}/status | 修改用户状态 | 管理员 |
| PUT | /api/admin/users/{id}/role | 修改用户角色 | 管理员 |
| DELETE | /api/admin/users/{id} | 删除用户 | 管理员 |
| GET | /api/admin/dynamics/pending | 待审核动态 | 管理员 |
| PUT | /api/admin/dynamics/{id}/approve | 审核通过 | 管理员 |
| DELETE | /api/admin/dynamics/{id} | 删除动态 | 管理员 |
| GET | /api/admin/login-logs | 登录日志 | 管理员 |
| GET | /api/admin/operation-logs | 操作日志 | 管理员 |
| GET | /api/admin/backups | 备份列表 | 管理员 |
| POST | /api/admin/backups/create | 创建备份 | 管理员 |
| POST | /api/admin/backups/{id}/restore | 恢复备份 | 管理员 |
| DELETE | /api/admin/backups/{id} | 删除备份 | 管理员 |

## 5. 安全机制

### 5.1 JWT认证

- Token有效期：24小时
- Token存储：客户端localStorage
- 自动刷新：前端可实现Token过期前自动刷新

### 5.2 密码加密

- 使用BCrypt算法加密
- 盐值自动生成
- 存储格式：$2a$10$...

### 5.3 权限控制

| 角色 | 权限说明 |
|------|----------|
| 普通用户(user) | 可访问基本功能，发布动态 |
| 管理员(admin) | 全部功能权限 |

### 5.4 输入验证

- 使用@Valid注解验证请求参数
- 防止SQL注入（MyBatis参数化查询）
- XSS防护（前端转义）

## 6. 日志管理

### 6.1 日志类型

1. **登录日志**：记录所有登录尝试
2. **操作日志**：记录所有API操作
3. **系统日志**：应用运行日志

### 6.2 日志查询

- 支持按用户名筛选
- 支持按时间排序
- 支持按状态筛选

## 7. 性能优化

### 7.1 数据库优化

- 使用MyBatis-Plus分页查询
- 添加索引优化查询
- 批量操作减少数据库交互

### 7.2 缓存策略

- Redis缓存热门数据（可选扩展）
- 前端本地缓存用户信息

### 7.3 异步处理

- 日志记录异步执行
- 备份操作异步执行

## 8. 故障排查

### 8.1 常见错误

| 错误码 | 说明 | 排查方向 |
|--------|------|----------|
| 401 | 未授权 | Token无效或过期 |
| 403 | 无权限 | 检查用户角色 |
| 404 | 资源不存在 | 检查请求路径 |
| 500 | 服务器错误 | 查看后端日志 |

### 8.2 日志查看

```bash
# 后端日志位置（运行时）
tail -f logs/basketball.log

# 常见日志级别
DEBUG: 调试信息
INFO: 一般信息
WARN: 警告信息
ERROR: 错误信息
```

### 8.3 数据库连接问题

1. 确认数据库服务运行
2. 检查配置文件中的连接信息
3. 确认数据库用户名密码正确

## 9. 代码规范

### 9.1 Java代码规范

- 类名：大驼峰命名（UserController）
- 方法名：小驼峰命名（getUserById）
- 变量名：小驼峰命名（userName）
- 常量名：全大写+下划线（MAX_SIZE）

### 9.2 前端代码规范

- 组件名：大驼峰命名（UserProfile.vue）
- 变量名：小驼峰命名（userProfile）
- 文件命名：小写+连字符（user-profile.vue）

### 9.3 数据库规范

- 表名：小写+下划线（app_user）
- 字段名：小写+下划线（user_name）

## 10. 维护流程

### 10.1 版本更新

1. 更新代码
2. 执行测试
3. 编译打包
4. 部署到服务器
5. 验证功能

### 10.2 数据备份

- 定期手动备份
- 配置自动备份（可选）
- 备份文件存储在安全位置

### 10.3 日志审查

- 定期审查操作日志
- 关注异常登录记录
- 及时处理安全事件

## 11. 扩展建议

### 11.1 性能监控

- 集成Spring Boot Actuator
- 添加Prometheus监控
- 配置Grafana可视化

### 11.2 消息队列

- 使用RabbitMQ处理异步任务
- 解耦高并发场景

### 11.3 分布式缓存

- 集成Redis缓存
- 减少数据库压力

### 11.4 容器化部署

- 使用Docker容器
- 配置Docker Compose
- 集成Kubernetes（大规模部署）