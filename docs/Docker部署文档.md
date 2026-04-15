# Docker 部署文档

## 目录

1. [环境要求](#环境要求)
2. [快速开始](#快速开始)
3. [配置说明](#配置说明)
4. [部署方式](#部署方式)
5. [常用命令](#常用命令)
6. [故障排查](#故障排查)

## 环境要求

### 软件要求

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| Docker | >= 20.10 | 容器运行时 |
| Docker Compose | >= 2.0 | 容器编排工具 |
| Make (可选) | 任意版本 | 简化操作命令 |

### 硬件要求

| 环境 | CPU | 内存 | 磁盘 |
|------|-----|------|------|
| 开发环境 | 2核 | 4GB | 20GB |
| 测试环境 | 4核 | 8GB | 50GB |
| 生产环境 | 8核+ | 16GB+ | 100GB+ |

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd glm_ticket
```

### 2. 配置环境变量

```bash
# 复制环境变量模板
cp .env.production .env

# 编辑配置（生产环境必须修改）
vim .env
```

**重要：生产环境必须修改以下配置：**
- `MYSQL_ROOT_PASSWORD` - MySQL root 密码
- `MYSQL_PASSWORD` - 应用数据库密码
- `JWT_SECRET` - JWT 密钥（至少 256 位）

### 3. 启动服务

```bash
# 使用 Makefile
make prod

# 或使用 docker-compose
docker-compose up -d
```

### 4. 验证部署

```bash
# 查看服务状态
make ps

# 查看健康状态
docker inspect --format='{{.Name}}: {{.State.Health.Status}}' ticket-mysql ticket-redis ticket-backend ticket-frontend
```

## 配置说明

### 环境变量文件

| 文件 | 用途 |
|------|------|
| `.env.development` | 开发环境配置 |
| `.env.test` | 测试环境配置 |
| `.env.production` | 生产环境配置 |

### 主要配置项

```bash
# MySQL 配置
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_USER=ticket
MYSQL_PASSWORD=your_password
MYSQL_PORT=3306

# Redis 配置
REDIS_PORT=6379

# 后端配置
BACKEND_PORT=8080
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your_secure_jwt_secret_key

# 前端配置
FRONTEND_PORT=80
```

### 后端配置文件

后端使用 Spring Profile 管理多环境配置：

| 文件 | 环境 |
|------|------|
| `application-dev.yml` | 开发环境 |
| `application-test.yml` | 测试环境 |
| `application-prod.yml` | 生产环境 |

### 前端配置文件

前端使用 Vite 环境变量：

| 文件 | 环境 |
|------|------|
| `.env.development` | 开发环境 |
| `.env.test` | 测试环境 |
| `.env.production` | 生产环境 |

## 部署方式

### 开发环境部署

```bash
# 使用开发环境配置启动
make dev

# 查看日志
make logs
```

### 测试环境部署

```bash
# 使用测试环境配置启动
make test

# 运行测试
docker-compose exec backend mvn test
```

### 生产环境部署

```bash
# 1. 配置生产环境变量
cp .env.production .env
vim .env  # 修改敏感配置

# 2. 构建并启动
make prod

# 3. 验证服务健康
make status
```

### 单独构建镜像

```bash
# 构建后端镜像
docker build -t ticket-backend:latest ./backend

# 构建前端镜像
docker build -t ticket-frontend:latest ./frontend
```

## 常用命令

### Makefile 命令

```bash
make help           # 显示帮助信息
make build          # 构建所有容器
make up             # 启动所有服务
make down           # 停止所有服务
make restart        # 重启所有服务
make logs           # 查看所有日志
make logs-backend   # 查看后端日志
make logs-frontend  # 查看前端日志
make ps             # 查看容器状态
make clean          # 清理容器和卷
make dev            # 启动开发环境
make test           # 启动测试环境
make prod           # 启动生产环境
make rebuild        # 强制重新构建
make shell-backend  # 进入后端容器
make shell-mysql    # 进入 MySQL
make shell-redis    # 进入 Redis
make status         # 查看详细状态
```

### Docker Compose 命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 查看日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 重新构建
docker-compose up -d --build

# 查看状态
docker-compose ps
```

## 故障排查

### 常见问题

#### 1. 容器无法启动

```bash
# 查看容器日志
docker-compose logs backend

# 检查容器状态
docker-compose ps

# 检查健康状态
docker inspect ticket-backend | grep -A 10 "Health"
```

#### 2. 数据库连接失败

```bash
# 检查 MySQL 是否就绪
docker-compose exec mysql mysql -uroot -p

# 检查网络连接
docker-compose exec backend ping mysql
```

#### 3. 前端无法访问后端 API

```bash
# 检查 Nginx 配置
docker-compose exec frontend cat /etc/nginx/conf.d/default.conf

# 检查后端是否响应
docker-compose exec frontend wget -qO- http://backend:8080/api/actuator/health
```

#### 4. 内存不足

```bash
# 调整 JVM 参数
# 在 .env 文件中设置
JAVA_OPTS=-Xms256m -Xmx512m
```

### 日志查看

```bash
# 查看所有日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend

# 查看最近 100 行日志
docker-compose logs --tail=100 backend
```

### 数据备份

```bash
# 备份 MySQL
docker-compose exec mysql mysqldump -uroot -p ticket_system > backup.sql

# 备份上传文件
docker cp ticket-backend:/data/files ./backup/files
```

### 重置环境

```bash
# 停止并删除所有容器、网络、卷
docker-compose down -v

# 重新启动
make prod
```

## 安全建议

1. **修改默认密码**：生产环境必须修改所有默认密码
2. **JWT 密钥**：使用强随机密钥，至少 256 位
3. **HTTPS**：生产环境建议配置 SSL 证书
4. **防火墙**：只开放必要端口（80, 443）
5. **定期备份**：设置自动备份计划
6. **日志监控**：配置日志收集和告警

## 端口说明

| 服务 | 内部端口 | 外部端口 | 说明 |
|------|---------|---------|------|
| frontend | 80 | 80 | Nginx 前端 |
| backend | 8080 | 8080 | Spring Boot API |
| mysql | 3306 | 3306 | MySQL 数据库 |
| redis | 6379 | 6379 | Redis 缓存 |
