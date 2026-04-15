# 工单系统 Docker 部署文档

## 目录

1. [环境要求](#环境要求)
2. [快速开始](#快速开始)
3. [目录结构](#目录结构)
4. [配置说明](#配置说明)
5. [部署命令](#部署命令)
6. [多环境部署](#多环境部署)
7. [数据管理](#数据管理)
8. [故障排查](#故障排查)
9. [安全建议](#安全建议)

---

## 环境要求

### 必需软件

| 软件 | 版本 | 说明 |
|------|------|------|
| Docker | 20.10+ | 容器引擎 |
| Docker Compose | 2.0+ | 容器编排 |
| Git | 任意 | 代码版本控制 |

### 系统要求

- **CPU**: 2 核及以上
- **内存**: 4GB 及以上（推荐 8GB）
- **磁盘**: 20GB 可用空间
- **网络**: 可访问互联网（拉取镜像）

### 端口要求

确保以下端口未被占用：

| 端口 | 服务 | 说明 |
|------|------|------|
| 80 | 前端 Nginx | Web 服务入口 |
| 8080 | 后端服务 | API 服务 |
| 3306 | MySQL | 数据库服务 |
| 6379 | Redis | 缓存服务 |

---

## 快速开始

### 1. 克隆代码

```bash
git clone <repository-url>
cd ticket-system
```

### 2. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，修改必要的配置
vi .env
```

### 3. 启动服务

**使用 Make（推荐 Linux/macOS）:**

```bash
make up
```

**使用 PowerShell 脚本（Windows）:**

```powershell
.\scripts\docker-deploy.ps1 -Action up
```

**使用 Bash 脚本（Linux/macOS）:**

```bash
./scripts/docker-deploy.sh up
```

**使用原生 Docker Compose:**

```bash
docker-compose up -d
```

### 4. 验证部署

```bash
# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

访问系统：
- 前端界面: http://localhost
- 后端 API: http://localhost:8080/api
- API 文档: http://localhost:8080/api/swagger-ui.html

---

## 目录结构

```
ticket-system/
├── backend/                    # 后端服务
│   ├── Dockerfile             # 后端镜像构建文件
│   ├── .dockerignore          # Docker 构建忽略文件
│   ├── pom.xml                # Maven 配置
│   └── src/                   # 源代码
├── frontend/                   # 前端服务
│   ├── Dockerfile             # 前端镜像构建文件
│   ├── .dockerignore          # Docker 构建忽略文件
│   ├── nginx.conf             # Nginx 配置
│   └── src/                   # 源代码
├── docker/                     # Docker 相关配置
│   ├── mysql/                 # MySQL 配置
│   │   └── my.cnf             # MySQL 配置文件
│   └── redis/                 # Redis 配置
│       └── redis.conf         # Redis 配置文件
├── scripts/                    # 部署脚本
│   ├── docker-deploy.sh       # Linux/macOS 部署脚本
│   └── docker-deploy.ps1      # Windows 部署脚本
├── docker-compose.yml          # Docker Compose 配置
├── Makefile                    # Make 命令配置
├── .env.example                # 环境变量模板
├── .dockerignore               # 全局 Docker 忽略文件
└── docs/                       # 文档
    └── Docker部署文档.md       # 本文件
```

---

## 配置说明

### 环境变量 (.env)

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `MYSQL_ROOT_PASSWORD` | Ticket@123 | MySQL root 密码 |
| `MYSQL_USER` | ticket | MySQL 应用用户名 |
| `MYSQL_PASSWORD` | Ticket@123 | MySQL 应用密码 |
| `MYSQL_PORT` | 3306 | MySQL 端口 |
| `REDIS_PORT` | 6379 | Redis 端口 |
| `REDIS_PASSWORD` | - | Redis 密码（可选） |
| `BACKEND_PORT` | 8080 | 后端服务端口 |
| `FRONTEND_PORT` | 80 | 前端服务端口 |
| `SPRING_PROFILES_ACTIVE` | prod | Spring 环境配置 |
| `JWT_SECRET` | - | JWT 密钥（生产环境必须修改） |
| `LOG_LEVEL` | INFO | 日志级别 |

### 重要安全提示

⚠️ **生产环境部署前必须修改以下配置：**

1. **修改默认密码**
   ```bash
   MYSQL_ROOT_PASSWORD=YourStrongRootPassword
   MYSQL_PASSWORD=YourStrongAppPassword
   ```

2. **修改 JWT 密钥**
   ```bash
   JWT_SECRET=Your256BitSecretKeyForProduction
   ```

3. **启用 Redis 密码（可选）**
   ```bash
   REDIS_PASSWORD=YourRedisPassword
   ```

---

## 部署命令

### Make 命令（推荐）

| 命令 | 说明 |
|------|------|
| `make help` | 显示帮助信息 |
| `make build` | 构建所有镜像 |
| `make up` | 启动所有服务 |
| `make down` | 停止所有服务 |
| `make restart` | 重启所有服务 |
| `make logs` | 查看所有日志 |
| `make logs-backend` | 查看后端日志 |
| `make ps` | 查看服务状态 |
| `make clean` | 清理容器 |
| `make clean-volumes` | 清理数据卷（⚠️ 删除数据） |

### 脚本命令

**PowerShell (Windows):**

```powershell
.\scripts\docker-deploy.ps1 -Action <命令> [-Service <服务名>]
```

**Bash (Linux/macOS):**

```bash
./scripts/docker-deploy.sh <命令> [服务名]
```

---

## 多环境部署

### 开发环境

```bash
# 使用 Make
make deploy-dev

# 使用脚本（Linux/macOS）
./scripts/docker-deploy.sh deploy-dev

# 使用脚本（Windows）
.\scripts\docker-deploy.ps1 -Action deploy-dev
```

### 测试环境

```bash
make deploy-test
```

### 生产环境

```bash
make deploy-prod
```

### 环境配置说明

| 环境 | 配置文件 | 特点 |
|------|----------|------|
| dev | `.env.development` | 调试模式，详细日志 |
| test | `.env.test` | 测试数据，独立数据库 |
| prod | `.env.production` | 生产优化，安全加固 |

---

## 数据管理

### 数据持久化

系统使用 Docker 卷进行数据持久化：

| 卷名 | 用途 | 位置 |
|------|------|------|
| `mysql_data` | MySQL 数据 | `/var/lib/mysql` |
| `redis_data` | Redis 数据 | `/data` |
| `file_uploads` | 文件上传 | `/data/files` |

### 数据库备份

```bash
# 使用 Make
make db-backup

# 使用脚本（Linux/macOS）
./scripts/docker-deploy.sh backup

# 使用脚本（Windows）
.\scripts\docker-deploy.ps1 -Action backup
```

备份文件保存在 `backups/` 目录。

### 数据库恢复

```bash
# 使用 Make
make db-restore file=backups/backup_20240101_120000.sql

# 使用脚本（Linux/macOS）
./scripts/docker-deploy.sh restore backups/backup_20240101_120000.sql
```

### 数据清理

⚠️ **警告：这将删除所有数据！**

```bash
make clean-volumes
```

---

## 故障排查

### 常见问题

#### 1. 端口被占用

**错误信息：**
```
Bind for 0.0.0.0:3306 failed: port is already allocated
```

**解决方案：**
```bash
# 查找占用端口的进程
# Linux/macOS:
sudo lsof -i :3306

# Windows:
netstat -ano | findstr :3306

# 修改 .env 文件中的端口配置
MYSQL_PORT=3307
```

#### 2. 内存不足

**错误信息：**
```
ERROR: for mysql Cannot start service mysql: OCI runtime create failed
```

**解决方案：**
- 增加 Docker 内存限制（Docker Desktop 设置）
- 减少 MySQL/Redis 内存配置

#### 3. 数据库连接失败

**错误信息：**
```
Connection refused: mysql:3306
```

**解决方案：**
```bash
# 检查 MySQL 容器状态
docker-compose ps mysql

# 查看 MySQL 日志
docker-compose logs mysql

# 重启 MySQL
docker-compose restart mysql
```

#### 4. 前端无法访问后端

**检查步骤：**
1. 确认后端服务正常运行
2. 检查 Nginx 配置
3. 查看前端容器日志

```bash
docker-compose logs frontend
docker-compose logs backend
```

### 日志查看

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql

# 查看最近 100 行日志
docker-compose logs --tail=100 backend
```

### 进入容器调试

```bash
# 进入后端容器
make shell-backend
# 或
docker-compose exec backend sh

# 进入 MySQL 容器
make shell-mysql
# 或
docker-compose exec mysql bash

# MySQL 命令行
docker-compose exec mysql mysql -u root -p
```

---

## 安全建议

### 1. 修改默认密码

生产环境部署前，务必修改所有默认密码。

### 2. 使用 HTTPS

生产环境建议使用 HTTPS：

```yaml
# docker-compose.yml 中添加
frontend:
  ports:
    - "443:443"
  volumes:
    - ./ssl:/etc/nginx/ssl:ro
```

### 3. 限制访问

```yaml
# 仅允许特定 IP 访问
mysql:
  ports:
    - "127.0.0.1:3306:3306"
```

### 4. 定期更新

```bash
# 更新基础镜像
docker-compose pull
docker-compose up -d
```

### 5. 监控和告警

建议配置容器监控和日志告警系统。

---

## 附录

### 镜像信息

| 服务 | 基础镜像 | 说明 |
|------|----------|------|
| backend | eclipse-temurin:17-jre-alpine | Java 17 运行时 |
| frontend | nginx:alpine | Nginx Web 服务器 |
| mysql | mysql:8.0 | MySQL 数据库 |
| redis | redis:7-alpine | Redis 缓存 |

### 网络配置

- 网络名称: `ticket-network`
- 子网: `172.20.0.0/16`

### 健康检查

所有服务都配置了健康检查：
- MySQL: `mysqladmin ping`
- Redis: `redis-cli ping`
- Backend: HTTP 健康端点
- Frontend: HTTP 请求

---

## 技术支持

如有问题，请通过以下方式联系：

- 提交 Issue: [项目仓库]
- 邮件支持: [支持邮箱]

---

**文档版本**: 1.0  
**更新日期**: 2024年
