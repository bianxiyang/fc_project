# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 技术栈

- **Spring Boot 2.7.18** + JDK 11
- **Spring Data JPA** (Hibernate, `ddl-auto=update`)
- **PostgreSQL** - 连接配置在 `src/main/resources/application.properties`
- **Spring Security** - 认证授权 (USER/ADMIN/ROOT 三级角色)
- **Thymeleaf** - 服务端 HTML 渲染
- **Maven** 构建

## 开发命令

```bash
# 构建
mvn clean package

# 运行
mvn spring-boot:run

# 单次测试
mvn test

# 运行单个测试类
mvn test -Dtest=UserPermissionServiceImplTest
```

## 架构概览

**分层结构**: Controller → Service (impl/) → Repository → Database

```
controller/    # @RestController 处理 API 请求
service/       # 接口 + impl/ 实现类
repository/    # JpaRepository 数据访问
model/         # JPA Entity 实体
dto/           # 请求/响应数据传输对象
config/        # SecurityConfig (安全配置)
init/          # 应用启动时数据初始化
```

**SecurityConfig 授权规则** (`config/SecurityConfig.java:120`):
- `/api/tournaments/**` - 已认证用户可访问
- `/api/matches/**/score` PUT - USER/ADMIN/ROOT 可修改相关比赛
- `/api/matches/audit/**` - 仅 ROOT 可访问
- 页面路径 `/rank-table` (USER+), `/schedule-generator` (ADMIN+), `/audit-page` (ROOT)

## 核心业务模型

**FcUser** - 用户，含 win/tie/lose/powerful 字段，通过 `userPermissions` 关联权限

**Tournament** - 淘汰赛，含 `TournamentMatch` 比赛列表和 `TournamentParticipant` 参与者

**TournamentMatch** - 每局比赛（3局2胜制），含轮次/场次/局数信息，支持 `TournamentMatchScoreAudit` 比分审核

**Permission** - 权限码，与 FcUser 通过 UserPermission 多对多关联

## API 约定

响应统一格式 `ApiResponse`:
- `ApiResponse.success(data)` - 成功
- `ApiResponse.error(message)` - 失败

API 前缀 `/api/`，关键端点:
- `POST /api/tournaments` - 创建锦标赛并生成赛程
- `GET /api/tournaments/{id}/bracket` - 获取锦标赛树状图
- `PUT /api/tournaments/matches/{id}/score` - 提交比分修改（需审核）
- `PUT /api/tournaments/audit/{id}/approve` - ROOT 审核批准

## 数据库

- 开发环境 PostgreSQL，端口 5432
- `spring.jpa.hibernate.ddl-auto=update` - 启动时自动同步表结构
- 敏感配置（数据库密码）在 `application.properties`，勿提交到 git
