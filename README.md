# IT Ticket Management System

IT部门工单生命周期管理系统 - 支持需求/Bug的提交、流转、处理、统计分析等功能。

## 技术栈

| 层级 | 技术选型 |
|------|----------|
| 前端 | Vue 3 + TypeScript + Vite + Tailwind CSS |
| 后端 | Spring Boot 3.2 + Java 17 + Maven |
| 数据库 | PostgreSQL 15 |
| 认证 | JWT Token |
| 文件存储 | 本地文件系统 |
| API文档 | OpenAPI/Swagger |

## 项目结构

```
it-ticket-system/
├── frontend/                    # Vue 3 前端
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── components/         # 通用组件
│   │   ├── stores/             # Pinia状态管理
│   │   ├── api/                # API请求封装
│   │   ├── router/             # 路由配置
│   │   └── assets/             # 静态资源
│   ├── public/
│   │   └── it-ticket-sdk.js    # JS SDK
│   └── package.json
├── backend/                     # Spring Boot 后端
│   ├── src/main/java/com/itticket/
│   │   ├── controller/         # REST API控制器
│   │   ├── service/            # 业务逻辑层
│   │   ├── repository/         # 数据访问层
│   │   ├── entity/             # JPA实体类
│   │   ├── dto/                # 数据传输对象
│   │   ├── config/             # 配置类
│   │   └── security/           # 安全配置
│   └── pom.xml
├── init.sh                      # 启动脚本
├── feature_list.json            # 功能清单
├── claude-progress.txt          # 进度日志
└── README.md
```

## 功能特性

### 用户与权限模块
- ✅ 用户注册/登录
- ✅ JWT认证
- ✅ 角色管理（管理员/产品经理/程序员/普通用户）
- ✅ 权限控制（RBAC）
- ✅ 用户个人信息管理
- ✅ 组织架构管理（部门、团队）

### 工单核心模块
- ✅ 工单创建表单（标题、描述、类型、优先级、附件）
- ✅ 工单编号规则（自动生成唯一编号）
- ✅ 工单列表/看板视图
- ✅ 工单详情页
- ✅ 工单搜索/筛选
- ✅ 工单模板
- ✅ 附件上传

### 工作流引擎
- ✅ 状态流转（新建→待评估→开发中→待测试→已完成→已关闭）
- ✅ 自动派发规则
- ✅ 手动分配/转派
- ✅ 驳回/退回操作
- ✅ SLA服务等级（响应时限、处理时限）
- ✅ 超时提醒
- ✅ 工单历史/操作日志

### 通知系统
- ✅ 站内消息
- ✅ 邮件通知
- ✅ 通知偏好设置

### 数据统计与报表
- ✅ 个人工作台/数据看板
- ✅ 团队工作量统计
- ✅ 工单趋势分析
- ✅ 响应时间统计
- ✅ 导出Excel报表

### 系统管理
- ✅ 系统配置
- ✅ 操作审计日志
- ✅ 系统公告管理

### 嵌入式组件
- ✅ JS SDK（供第三方系统嵌入）
- ✅ API接口（供第三方系统调用）

## 快速开始

### 环境要求

- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Maven 3.8+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd it-ticket-system
```

2. **配置数据库**
```bash
# 创建数据库
psql -U postgres
CREATE DATABASE it_ticket_db;
```

3. **配置后端**
```bash
cd backend
# 编辑 src/main/resources/application.yml
# 配置数据库连接信息
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/it_ticket_db
    username: postgres
    password: your_password
```

4. **启动后端**
```bash
cd backend
export JAVA_HOME=/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

5. **启动前端**
```bash
cd frontend
npm install
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

### 一键启动

```bash
./init.sh
```

## API 文档

启动后端服务后，访问以下地址查看API文档：

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 主要API端点

| 模块 | 端点 | 说明 |
|------|------|------|
| 认证 | `POST /api/auth/login` | 用户登录 |
| 认证 | `POST /api/auth/register` | 用户注册 |
| 工单 | `GET /api/tickets` | 获取工单列表 |
| 工单 | `POST /api/tickets` | 创建工单 |
| 工单 | `GET /api/tickets/{id}` | 获取工单详情 |
| 工单 | `PUT /api/tickets/{id}` | 更新工单 |
| 工单 | `POST /api/tickets/{id}/assign` | 分配工单 |
| 工单 | `POST /api/tickets/{id}/status` | 更新状态 |
| 统计 | `GET /api/statistics/team-workload` | 团队工作量 |
| 统计 | `GET /api/statistics/trend` | 工单趋势 |
| 统计 | `GET /api/statistics/response-time` | 响应时间 |
| 统计 | `GET /api/statistics/export/tickets` | 导出Excel |
| 系统配置 | `GET /api/system/config` | 获取配置 |
| 审计日志 | `GET /api/audit-logs` | 查询日志 |
| 公告 | `GET /api/announcements` | 公告列表 |

## JS SDK

第三方系统可以通过JS SDK快速集成工单提交功能。

### 引入SDK

```html
<script src="https://your-domain.com/it-ticket-sdk.js"></script>
```

### 使用示例

```javascript
// 初始化
ITTicketSDK.init({
  apiUrl: 'https://your-domain.com/api',
  token: 'your-jwt-token'
});

// 打开工单表单
ITTicketSDK.openTicketForm();

// 编程式创建工单
ITTicketSDK.createTicket({
  title: 'Bug Report',
  description: 'Description here',
  type: 'BUG',
  priority: 'HIGH'
});

// 创建嵌入式组件
ITTicketSDK.createWidget('widget-container', {
  buttonText: '提交工单'
});
```

详细文档请参考 [SDK README](frontend/public/sdk-README.md)

## 数据库表结构

### 核心表

```sql
-- 用户表
users (id, username, password, email, role_id, department_id, created_at)

-- 角色表
roles (id, name, code, permissions)

-- 工单表
tickets (id, ticket_no, title, description, type, priority, status,
         reporter_id, assignee_id, department_id, sla_id, created_at, updated_at)

-- 工单历史
ticket_histories (id, ticket_id, action, from_status, to_status,
                  operator_id, comment, created_at)

-- 附件表
attachments (id, ticket_id, file_name, file_path, file_size, uploader_id, created_at)

-- 通知表
notifications (id, user_id, title, content, is_read, link, created_at)

-- 评论表
comments (id, ticket_id, user_id, content, created_at)

-- 系统配置表
system_configs (id, config_key, config_value, description, category)

-- 审计日志表
audit_logs (id, user_id, username, action, entity_type, entity_id,
            description, ip_address, created_at)

-- 公告表
announcements (id, title, content, author_id, priority, status,
               start_time, end_time, is_pinned, created_at)
```

## 默认账户

系统初始化后会创建以下默认账户：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| pm | pm123 | 产品经理 |
| dev | dev123 | 程序员 |
| user | user123 | 普通用户 |

> ⚠️ 生产环境请务必修改默认密码

## 配置说明

### 后端配置 (application.yml)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/it_ticket_db
    username: postgres
    password: your_password

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

  mail:
    host: smtp.example.com
    port: 587
    username: your-email
    password: your-password

jwt:
  secret: your-jwt-secret-key
  expiration: 86400000

file:
  upload-dir: ./uploads
```

### 前端配置 (.env)

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## 开发指南

### 后端开发

```bash
cd backend

# 编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package

# 运行
java -jar target/it-ticket-system-1.0.0.jar
```

### 前端开发

```bash
cd frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 部署

### Docker 部署

```bash
# 构建镜像
docker build -t it-ticket-system .

# 运行容器
docker run -p 8080:8080 it-ticket-system
```

### 传统部署

1. 打包后端
```bash
cd backend
mvn clean package
```

2. 打包前端
```bash
cd frontend
npm run build
```

3. 部署到服务器
- 将 `backend/target/it-ticket-system-1.0.0.jar` 部署到应用服务器
- 将 `frontend/dist` 目录部署到 Web 服务器（如 Nginx）

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

如有问题，请提交 Issue 或联系维护团队。
