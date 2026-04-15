# Docker 部署文档

## 目录
1. [环境要求](#环境要求)
2. [快速启动](#快速启动)
3. [服务说明](#服务说明)
4. [环境变量配置](#环境变量配置)
5. [生产环境部署](#生产环境部署)
6. [常见问题](#常见问题)

## 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- 建议配置：4核8G内存及以上

## 快速启动

### 方式一：使用 Makefile (推荐 Linux/macOS)

```bash
# 克隆项目
git clone <repository-url>
cd doubao_ticket

# 构建并启动所有服务
make up

# 查看服务状态
make ps

# 查看日志
make logs
```

### 方式二：使用 PowerShell 脚本 (Windows)

```powershell
# 构建并启动所有服务
.\docker.ps1 up

# 查看服务状态
.\docker.ps1 ps

# 查看日志
.\docker.ps1 logs
```

### 方式三：原生 Docker Compose 命令

```bash
# 构建并启动
docker compose up -d

# 查看状态
docker compose ps

# 查看日志
docker compose logs -f
```

## 服务说明

启动成功后，可以访问以下地址：

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端应用 | http://localhost | 工单系统前端页面 |
| 后端API | http://localhost/api | 后端API接口 |
| API文档 | http://localhost/api/swagger-ui.html | 开发环境可用 |
| 健康检查 | http://localhost/api/actuator/health | 服务健康状态 |

## 服务架构

```
                        ┌─────────────────┐
                        │      Nginx      │  ←  前端 + API网关
                        └────────┬────────┘
                                 │
           ┌─────────────────────┼─────────────────────┐
           │                     │                     │
┌──────────▼──────────┐  ┌──────▼───────┐  ┌──────────▼──────────┐
│   Spring Boot API   │  │    MySQL     │  │        Redis        │
│      (Backend)      │  │      8.0     │  │          7.x        │
└─────────────────────┘  └──────────────┘  └─────────────────────┘
```

## 环境变量配置

可以在项目根目录创建 `.env` 文件自定义配置：

```env
# 数据库配置
DB_PASSWORD=your_secure_password
DB_ROOT_PASSWORD=your_secure_root_password

# Redis配置
REDIS_PASSWORD=your_redis_password

# JWT密钥
JWT_SECRET=your-256-bit-secret-key-change-in-production

# Spring环境
SPRING_PROFILES_ACTIVE=prod

# 管理员密码
INIT_ADMIN_PASSWORD=Admin@Secure123
```

## 数据持久化

Docker Compose 自动创建以下数据卷：

- `mysql_data`: MySQL 数据库数据
- `redis_data`: Redis 持久化数据
- `backend_logs`: 后端应用日志
- `file_uploads`: 用户上传的文件

## 生产环境部署

### 1. 配置生产环境参数

创建 `.env` 文件，务必修改所有默认密码：

```env
DB_PASSWORD=ComplexPassword@2024
REDIS_PASSWORD=RedisSecurePass2024
JWT_SECRET=change-this-to-your-own-jwt-secret-at-least-256-bits
INIT_ADMIN_PASSWORD=YourAdminPassword@2024
SPRING_PROFILES_ACTIVE=prod
```

### 2. 启动服务

```bash
docker compose up -d
```

### 3. 验证服务状态

```bash
# 检查所有容器健康状态
docker compose ps

# 检查后端健康检查
curl http://localhost/api/actuator/health
```

### 4. 查看日志

```bash
# 所有服务日志
docker compose logs -f

# 特定服务日志
docker compose logs -f backend
docker compose logs -f mysql
```

## 常用命令

### Makefile 命令

| 命令 | 说明 |
|------|------|
| `make build` | 构建所有镜像 |
| `make up` | 启动所有服务 |
| `make down` | 停止并移除容器 |
| `make logs` | 查看所有日志 |
| `make logs-be` | 查看后端日志 |
| `make logs-fe` | 查看前端日志 |
| `make restart` | 重启所有服务 |
| `make ps` | 列出运行中的容器 |
| `make clean` | 清理所有容器、镜像和数据卷 |
| `make test-be` | 运行后端测试 |
| `make test-fe` | 运行前端测试 |

### Docker Compose 常用命令

```bash
# 单个服务重启
docker compose restart backend

# 进入容器内部
docker compose exec -it mysql bash
docker compose exec -it redis redis-cli

# 查看资源使用情况
docker stats

# 清理未使用的镜像
docker system prune -af
```

## 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| Nginx/Frontend | 80 | HTTP 访问端口 |
| MySQL | 3306 | 数据库端口（宿主机可访问） |
| Redis | 6379 | Redis端口（宿主机可访问） |
| Backend | 8080 | 后端端口（仅Docker内部网络可访问） |

## 常见问题

### 1. 后端服务启动失败

**问题现象：** `ticket-backend` 容器状态不健康

**解决方案：**
```bash
# 查看后端日志
docker compose logs backend

# 检查 MySQL 是否正常启动
docker compose logs mysql

# 确保数据库初始化完成后再启动后端
docker compose restart backend
```

### 2. 数据库连接失败

**问题现象：** 日志显示 `Connection refused`

**解决方案：**
- 确保 MySQL 容器健康检查通过
- 检查环境变量中的数据库密码配置
- 等待 MySQL 完全启动（约30秒）

### 3. 前端无法访问后端API

**问题现象：** 前端页面显示网络错误

**解决方案：**
- 检查 Nginx 配置是否正确代理 `/api` 请求
- 确认后端服务已正常启动
- 检查浏览器控制台的具体错误信息

### 4. 上传文件失败

**问题现象：** 文件上传功能报错

**解决方案：**
```bash
# 检查文件上传目录权限
docker compose exec backend ls -la /data/files

# 如权限有问题，重新创建数据卷
docker compose down -v
docker compose up -d
```

## 性能优化建议

### 1. JVM 参数调优

修改 `backend/Dockerfile` 中的 JVM 参数：

```dockerfile
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Xms1g", "-Xmx2g", \
    "-jar", "app.jar"]
```

### 2. MySQL 性能调优

在 `docker-compose.yml` 中添加 MySQL 配置：

```yaml
command:
  --innodb_buffer_pool_size=1G
  --max_connections=2000
  --query_cache_size=64M
```

### 3. Nginx 缓存配置

已默认启用静态资源缓存，缓存有效期为1年。

## 备份与恢复

### 数据库备份

```bash
# 创建备份
docker compose exec mysql mysqldump -u root -p ticket_system > backup.sql

# 恢复备份
docker compose exec -T mysql mysql -u root -p ticket_system < backup.sql
```

### 文件备份

```bash
# 备份上传的文件
docker run --rm -v ticket-system_file_uploads:/data -v $(pwd):/backup alpine tar czf /backup/files_backup.tar.gz -C /data .
```

## 更新升级

```bash
# 拉取最新代码
git pull

# 重新构建镜像并启动
docker compose up -d --build
```

---

**注意：** 生产环境请务必：
1. 修改所有默认密码
2. 配置 HTTPS（建议使用反向代理如 Traefik 或 Nginx Proxy Manager）
3. 定期备份数据
4. 配置防火墙限制端口访问
5. 启用日志收集和监控告警
