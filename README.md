# FC Project

这是一个基于SpringBoot的Web项目，使用PostgreSQL作为数据库。

## 技术栈

- Spring Boot 2.7.18
- Spring Data JPA
- PostgreSQL
- Maven

## 项目结构

```
fc-project/
├── src/
│   ├── main/
│   │   ├── java/com/example/fcproject/
│   │   │   ├── controller/    # 控制器层
│   │   │   ├── model/         # 数据模型
│   │   │   ├── repository/    # 数据访问层
│   │   │   ├── util/          # 工具类
│   │   │   └── FcProjectApplication.java  # 应用入口
│   │   └── resources/
│   │       ├── application.properties  # 属性配置文件
│   │       └── application.yml         # YAML配置文件
│   └── test/                  # 测试代码
├── pom.xml                    # Maven配置文件
├── .gitignore                 # Git忽略文件
└── README.md                  # 项目说明
```

## 快速开始

### 前提条件

- JDK 11+
- Maven 3.6+
- PostgreSQL 10+

### 配置数据库

1. 创建PostgreSQL数据库：
   ```sql
   CREATE DATABASE fc_project;
   CREATE USER postgres WITH PASSWORD 'postgres';
   GRANT ALL PRIVILEGES ON DATABASE fc_project TO postgres;
   ```

2. 修改配置文件中的数据库连接信息（如果需要）：
   - `src/main/resources/application.properties`
   - 或 `src/main/resources/application.yml`

### 构建和运行

1. 构建项目：
   ```bash
   mvn clean package
   ```

2. 运行应用：
   ```bash
   mvn spring-boot:run
   ```

3. 应用将在 http://localhost:8080/api 启动

## API端点

- GET `/api/users` - 获取所有用户
- GET `/api/users/{id}` - 根据ID获取用户
- POST `/api/users` - 创建新用户
- PUT `/api/users/{id}` - 更新用户信息
- DELETE `/api/users/{id}` - 删除用户

## 测试

运行单元测试：
```bash
mvn test
```