# McServerWebByKotlin

Minecraft 服务器列表 Web 后端，基于 **Kotlin + Spring Boot 4 + JPA + MySQL**。

## 功能特性

- **账户体系**：注册 / 登录 / 登出，JWT 无状态认证，密码 BCrypt 加密存储
- **服务器列表**：分页浏览、新增服务器、单个服务器在线状态查询
- **在线状态探测**：支持两种模式
  - `serverFlag = true`：服务端通过 MCPing 定时主动探测（每 10 分钟）
  - `serverFlag = false`：客户端 Agent 心跳上报，9 秒内有心跳即视为在线
- **好友系统**：发送 / 接受好友请求、好友列表、删除关系
- **用户搜索**：按用户名 / 显示名模糊搜索
- **Agent 心跳**：接收房间 Agent 心跳并更新服务器的活跃时间

## 技术栈

| 组件 | 说明 |
|------|------|
| Kotlin 2.3 | 主要开发语言 |
| Spring Boot 4.1 | Web MVC、Data JPA、Security、Validation |
| MySQL 8 | 生产 / 本地开发数据库 |
| H2 | 测试用内存数据库 |
| MCPing | Minecraft 服务器 Ping 协议库 |
| JJWT | JWT 令牌签发与解析 |

## 环境要求

- JDK 21+
- Maven（或使用项目自带的 `mvnw` / `mvnw.cmd`）
- MySQL 8（本地开发）

## 快速启动

1. 创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS mc_server_list
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

若之前跑过旧版本并遇到 `Field 'id' doesn't have a default value`，可重建表：

```sql
DROP TABLE IF EXISTS server;
DROP TABLE IF EXISTS server_seq;
-- 重启应用后 Hibernate 会自动建表
```

2. 修改 `src/main/resources/application.yaml` 中的数据库用户名和密码，并按需配置 JWT：

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:<Base64 编码的密钥>}
    expiration-minutes: 1440   # 令牌有效期（分钟），默认 24 小时
```

生产环境建议通过 `JWT_SECRET` 环境变量覆盖密钥，不要使用默认值。

3. 启动应用：

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

4. 验证：

- 健康检查：<http://localhost:8080/>
- 服务器列表（第 1 页，page 从 0 开始）：<http://localhost:8080/server/list/0>

## API 说明

除标注「公开」外，其余接口都需要在请求头携带登录令牌：

```
Authorization: Bearer <token>
```

### 认证

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/auth/register` | 注册（用户名、显示名、密码） | 公开 |
| POST | `/auth/login` | 登录，返回 JWT 令牌 | 公开 |
| POST | `/auth/logout` | 登出，使当前令牌立即失效 | 登录 |
| GET | `/auth/me` | 获取当前登录用户信息 | 登录 |

### 服务器列表

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/` | 服务状态 | 公开 |
| GET | `/server/list/{page}` | 分页获取服务器列表（每页 10 条，附带在线状态） | 公开 |
| GET | `/server/list/servers/{id}` | 获取单个服务器详情与在线状态 | 公开 |
| POST | `/server/list/create` | 新增服务器；`ownerId` 由后端根据令牌设置 | 登录 |

### 用户与好友

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/users?keyword=xxx` | 按用户名 / 显示名搜索用户（不含自己） | 登录 |
| GET | `/friends` | 好友 / 好友请求列表 | 登录 |
| POST | `/friends/{userId}/request` | 向某用户发送好友请求 | 登录 |
| POST | `/friends/{friendshipId}/accept` | 接受好友请求（仅请求接收方可接受） | 登录 |
| DELETE | `/friends/{friendshipId}` | 删除好友关系（任一方可操作） | 登录 |

### Agent 心跳

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/room/agent/heartbeat` | Agent 上报心跳，更新对应服务器的 `updatedAt` | 登录 |

### 请求示例

```bash
# 注册
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"steve\",\"displayName\":\"Steve\",\"password\":\"123456\"}"

# 登录，返回 token 和用户信息
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"steve\",\"password\":\"123456\"}"

# 携带令牌新增服务器
curl -X POST http://localhost:8080/server/list/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d "{\"name\":\"Hypixel\",\"host\":\"mc.hypixel.net\",\"port\":25565,\"serverFlag\":true}"
```

> 提示：`serverFlag` 决定在线状态的判定方式。创建服务器时传入的 `active` 会在写入前被探测结果覆盖，无需手动维护。

## 在线状态判定

- `serverFlag = true`：`ScheduledFetchService` 每 10 分钟用 MCPing 探测一次标记为 true 的服务器，并把结果写入 `active` 字段。
- `serverFlag = false`：不主动探测，由客户端 Agent 定期调用 `/room/agent/heartbeat` 上报；`updatedAt` 距今不超过 9 秒即视为在线，否则视为离线。

## 运行测试

测试使用 H2 内存库，无需 MySQL：

```bash
.\mvnw.cmd test
```

## 项目结构

```
src/main/kotlin/com/cvkno80098/mcserverwebbykotlin/
├── controller/   REST 接口（认证、服务器、好友、用户、健康检查、Agent 心跳）
├── dto/          请求 / 响应数据结构
├── entity/       JPA 实体（用户、服务器、好友关系）
├── repository/   数据访问层
├── security/     JWT 认证过滤器与安全配置
└── service/      业务逻辑、MCPing 探测与定时任务
```