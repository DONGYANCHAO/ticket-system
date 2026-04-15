# ==========================================
# 工单系统 Docker 管理 Makefile
# 简化 Docker Compose 操作
# ==========================================

.PHONY: help build up down restart logs ps clean test deploy

# 默认目标
.DEFAULT_GOAL := help

# 颜色定义
BLUE := \033[36m
GREEN := \033[32m
YELLOW := \033[33m
RED := \033[31m
NC := \033[0m # No Color

# ---------------- 帮助信息 ----------------
help: ## 显示帮助信息
	@echo "$(BLUE)工单系统 Docker 管理命令$(NC)"
	@echo ""
	@echo "$(GREEN)构建命令:$(NC)"
	@echo "  make build        - 构建所有服务镜像"
	@echo "  make build-backend - 仅构建后端镜像"
	@echo "  make build-frontend - 仅构建前端镜像"
	@echo ""
	@echo "$(GREEN)运行命令:$(NC)"
	@echo "  make up           - 启动所有服务（后台运行）"
	@echo "  make up-d         - 启动所有服务（前台运行）"
	@echo "  make down         - 停止并移除所有服务"
	@echo "  make restart      - 重启所有服务"
	@echo ""
	@echo "$(GREEN)查看命令:$(NC)"
	@echo "  make logs         - 查看所有服务日志"
	@echo "  make logs-backend - 查看后端服务日志"
	@echo "  make logs-frontend - 查看前端服务日志"
	@echo "  make logs-mysql   - 查看 MySQL 日志"
	@echo "  make ps           - 查看运行中的服务状态"
	@echo ""
	@echo "$(GREEN)维护命令:$(NC)"
	@echo "  make clean        - 清理所有容器、卷和镜像"
	@echo "  make clean-volumes - 清理数据卷（会删除数据！）"
	@echo "  make shell-backend - 进入后端容器 shell"
	@echo "  make shell-mysql  - 进入 MySQL 容器 shell"
	@echo ""
	@echo "$(GREEN)部署命令:$(NC)"
	@echo "  make deploy-dev   - 部署开发环境"
	@echo "  make deploy-prod  - 部署生产环境"
	@echo "  make deploy-test  - 部署测试环境"

# ---------------- 构建命令 ----------------
build: ## 构建所有服务镜像
	@echo "$(GREEN)正在构建所有服务镜像...$(NC)"
	docker-compose build

build-backend: ## 仅构建后端镜像
	@echo "$(GREEN)正在构建后端镜像...$(NC)"
	docker-compose build backend

build-frontend: ## 仅构建前端镜像
	@echo "$(GREEN)正在构建前端镜像...$(NC)"
	docker-compose build frontend

build-no-cache: ## 不使用缓存构建所有镜像
	@echo "$(GREEN)正在重新构建所有服务镜像（不使用缓存）...$(NC)"
	docker-compose build --no-cache

# ---------------- 运行命令 ----------------
up: ## 启动所有服务（后台运行）
	@echo "$(GREEN)正在启动所有服务...$(NC)"
	docker-compose up -d
	@echo "$(GREEN)服务已启动，访问: http://localhost$(NC)"

up-d: ## 启动所有服务（前台运行）
	@echo "$(GREEN)正在启动所有服务（前台模式）...$(NC)"
	docker-compose up

down: ## 停止并移除所有服务
	@echo "$(YELLOW)正在停止所有服务...$(NC)"
	docker-compose down

stop: ## 停止所有服务
	@echo "$(YELLOW)正在停止所有服务...$(NC)"
	docker-compose stop

start: ## 启动已停止的服务
	@echo "$(GREEN)正在启动服务...$(NC)"
	docker-compose start

restart: ## 重启所有服务
	@echo "$(YELLOW)正在重启所有服务...$(NC)"
	docker-compose restart

restart-backend: ## 仅重启后端服务
	@echo "$(YELLOW)正在重启后端服务...$(NC)"
	docker-compose restart backend

restart-frontend: ## 仅重启前端服务
	@echo "$(YELLOW)正在重启前端服务...$(NC)"
	docker-compose restart frontend

# ---------------- 查看命令 ----------------
logs: ## 查看所有服务日志
	docker-compose logs -f

logs-backend: ## 查看后端服务日志
	docker-compose logs -f backend

logs-frontend: ## 查看前端服务日志
	docker-compose logs -f frontend

logs-mysql: ## 查看 MySQL 日志
	docker-compose logs -f mysql

logs-redis: ## 查看 Redis 日志
	docker-compose logs -f redis

ps: ## 查看运行中的服务状态
	docker-compose ps

top: ## 查看服务资源使用情况
	docker-compose top

# ---------------- 维护命令 ----------------
clean: ## 清理所有容器
	@echo "$(RED)正在清理所有容器...$(NC)"
	docker-compose down --remove-orphans

clean-volumes: ## 清理数据卷（⚠️ 会删除所有数据！）
	@echo "$(RED)警告: 这将删除所有数据卷，包括数据库数据！$(NC)"
	@read -p "确定要继续吗? [y/N] " confirm; \
	if [ "$$confirm" = "y" ] || [ "$$confirm" = "Y" ]; then \
		docker-compose down -v; \
		echo "$(GREEN)数据卷已清理$(NC)"; \
	else \
		echo "$(YELLOW)操作已取消$(NC)"; \
	fi

clean-all: ## 清理所有容器、卷和镜像（⚠️ 完全清理）
	@echo "$(RED)警告: 这将删除所有容器、卷和镜像！$(NC)"
	@read -p "确定要继续吗? [y/N] " confirm; \
	if [ "$$confirm" = "y" ] || [ "$$confirm" = "Y" ]; then \
		docker-compose down --rmi all -v --remove-orphans; \
		docker system prune -f; \
		echo "$(GREEN)完全清理完成$(NC)"; \
	else \
		echo "$(YELLOW)操作已取消$(NC)"; \
	fi

shell-backend: ## 进入后端容器 shell
	docker-compose exec backend sh

shell-mysql: ## 进入 MySQL 容器 shell
	docker-compose exec mysql bash

shell-redis: ## 进入 Redis 容器 shell
	docker-compose exec redis sh

# ---------------- 部署命令 ----------------
deploy-dev: ## 部署开发环境
	@echo "$(GREEN)正在部署开发环境...$(NC)"
	cp .env.development .env 2>/dev/null || true
	export SPRING_PROFILES_ACTIVE=dev && docker-compose -f docker-compose.yml up -d --build

deploy-test: ## 部署测试环境
	@echo "$(GREEN)正在部署测试环境...$(NC)"
	cp .env.test .env 2>/dev/null || true
	export SPRING_PROFILES_ACTIVE=test && docker-compose -f docker-compose.yml up -d --build

deploy-prod: ## 部署生产环境
	@echo "$(GREEN)正在部署生产环境...$(NC)"
	cp .env.production .env 2>/dev/null || true
	export SPRING_PROFILES_ACTIVE=prod && docker-compose -f docker-compose.yml up -d --build

# ---------------- 数据库命令 ----------------
db-backup: ## 备份数据库
	@echo "$(GREEN)正在备份数据库...$(NC)"
	@mkdir -p backups
	@docker-compose exec -T mysql mysqldump -u root -p"$$(docker-compose exec mysql printenv MYSQL_ROOT_PASSWORD)" ticket_system > backups/backup_$$(date +%Y%m%d_%H%M%S).sql
	@echo "$(GREEN)数据库备份完成$(NC)"

db-restore: ## 恢复数据库（需要指定备份文件）
	@if [ -z "$(file)" ]; then \
		echo "$(RED)错误: 请指定备份文件，例如: make db-restore file=backup_20240101_120000.sql$(NC)"; \
		exit 1; \
	fi
	@echo "$(GREEN)正在恢复数据库...$(NC)"
	@docker-compose exec -T mysql mysql -u root -p"$$(docker-compose exec mysql printenv MYSQL_ROOT_PASSWORD)" ticket_system < $(file)
	@echo "$(GREEN)数据库恢复完成$(NC)"

# ---------------- 更新命令 ----------------
update: ## 更新并重启服务（拉取最新代码后使用）
	@echo "$(GREEN)正在更新服务...$(NC)"
	git pull
	docker-compose down
	docker-compose build --no-cache
	docker-compose up -d
	@echo "$(GREEN)服务更新完成$(NC)"

update-backend: ## 仅更新后端服务
	@echo "$(GREEN)正在更新后端服务...$(NC)"
	docker-compose stop backend
	docker-compose rm -f backend
	docker-compose build --no-cache backend
	docker-compose up -d backend
	@echo "$(GREEN)后端服务更新完成$(NC)"

update-frontend: ## 仅更新前端服务
	@echo "$(GREEN)正在更新前端服务...$(NC)"
	docker-compose stop frontend
	docker-compose rm -f frontend
	docker-compose build --no-cache frontend
	docker-compose up -d frontend
	@echo "$(GREEN)前端服务更新完成$(NC)"
