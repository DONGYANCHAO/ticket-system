#!/bin/bash
# ==========================================
# 工单系统 Docker 部署脚本 (Bash)
# 适用于 Linux/macOS 环境
# ==========================================

set -e

# 颜色定义
BLUE='\033[36m'
GREEN='\033[32m'
YELLOW='\033[33m'
RED='\033[31m'
NC='\033[0m' # No Color

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

# 显示帮助信息
show_help() {
    echo -e "${BLUE}工单系统 Docker 部署脚本${NC}"
    echo ""
    echo -e "${GREEN}用法:${NC}"
    echo "  ./scripts/docker-deploy.sh <命令> [服务名]"
    echo ""
    echo -e "${GREEN}命令:${NC}"
    echo "  up              - 启动所有服务（后台运行）"
    echo "  down            - 停止并移除所有服务"
    echo "  restart         - 重启所有服务"
    echo "  build           - 构建所有服务镜像"
    echo "  logs [服务名]   - 查看服务日志"
    echo "  status          - 查看服务状态"
    echo "  clean           - 清理所有容器和卷"
    echo "  deploy-dev      - 部署开发环境"
    echo "  deploy-test     - 部署测试环境"
    echo "  deploy-prod     - 部署生产环境"
    echo "  update          - 更新并重启服务"
    echo "  backup          - 备份数据库"
    echo "  restore <文件>  - 恢复数据库"
    echo "  help            - 显示帮助信息"
    echo ""
    echo -e "${GREEN}服务名（用于 logs 命令）:${NC}"
    echo "  backend, frontend, mysql, redis"
    echo ""
    echo -e "${YELLOW}示例:${NC}"
    echo "  ./scripts/docker-deploy.sh up"
    echo "  ./scripts/docker-deploy.sh logs backend"
    echo "  ./scripts/docker-deploy.sh deploy-prod"
}

# 启动服务
start_services() {
    echo -e "${GREEN}正在启动所有服务...${NC}"
    docker-compose up -d
    echo -e "${GREEN}服务已启动，访问: http://localhost${NC}"
}

# 停止服务
stop_services() {
    echo -e "${YELLOW}正在停止所有服务...${NC}"
    docker-compose down
}

# 重启服务
restart_services() {
    echo -e "${YELLOW}正在重启所有服务...${NC}"
    docker-compose restart
}

# 构建镜像
build_services() {
    echo -e "${GREEN}正在构建所有服务镜像...${NC}"
    docker-compose build
}

# 查看日志
show_logs() {
    local service=$1
    if [ -n "$service" ]; then
        echo -e "${BLUE}正在查看 $service 日志...${NC}"
        docker-compose logs -f "$service"
    else
        echo -e "${BLUE}正在查看所有服务日志...${NC}"
        docker-compose logs -f
    fi
}

# 查看状态
show_status() {
    echo -e "${BLUE}服务状态:${NC}"
    docker-compose ps
}

# 清理环境
clean_environment() {
    echo -e "${RED}警告: 这将删除所有容器和数据卷！${NC}"
    read -p "确定要继续吗? [y/N] " confirm
    if [[ $confirm =~ ^[Yy]$ ]]; then
        docker-compose down -v --remove-orphans
        echo -e "${GREEN}环境清理完成${NC}"
    else
        echo -e "${YELLOW}操作已取消${NC}"
    fi
}

# 部署环境
deploy_environment() {
    local env=$1
    echo -e "${GREEN}正在部署 $env 环境...${NC}"

    local env_file=".env.$env"
    if [ -f "$env_file" ]; then
        cp "$env_file" .env
    fi

    export SPRING_PROFILES_ACTIVE=$env
    docker-compose -f docker-compose.yml up -d --build
    echo -e "${GREEN}$env 环境部署完成${NC}"
}

# 更新服务
update_services() {
    echo -e "${GREEN}正在更新服务...${NC}"
    git pull
    docker-compose down
    docker-compose build --no-cache
    docker-compose up -d
    echo -e "${GREEN}服务更新完成${NC}"
}

# 备份数据库
backup_database() {
    echo -e "${GREEN}正在备份数据库...${NC}"
    mkdir -p backups
    local timestamp=$(date +%Y%m%d_%H%M%S)
    local backup_file="backups/backup_${timestamp}.sql"

    local mysql_password=$(docker-compose exec -T mysql printenv MYSQL_ROOT_PASSWORD 2>/dev/null || echo "Ticket@123")
    docker-compose exec -T mysql mysqldump -u root -p"$mysql_password" ticket_system > "$backup_file"
    echo -e "${GREEN}数据库备份完成: $backup_file${NC}"
}

# 恢复数据库
restore_database() {
    local file=$1
    if [ -z "$file" ]; then
        echo -e "${RED}错误: 请指定备份文件${NC}"
        echo "用法: ./scripts/docker-deploy.sh restore <备份文件>"
        exit 1
    fi

    if [ ! -f "$file" ]; then
        echo -e "${RED}错误: 备份文件不存在: $file${NC}"
        exit 1
    fi

    echo -e "${GREEN}正在恢复数据库...${NC}"
    local mysql_password=$(docker-compose exec -T mysql printenv MYSQL_ROOT_PASSWORD 2>/dev/null || echo "Ticket@123")
    docker-compose exec -T mysql mysql -u root -p"$mysql_password" ticket_system < "$file"
    echo -e "${GREEN}数据库恢复完成${NC}"
}

# 主逻辑
case "${1:-help}" in
    up)
        start_services
        ;;
    down)
        stop_services
        ;;
    restart)
        restart_services
        ;;
    build)
        build_services
        ;;
    logs)
        show_logs "$2"
        ;;
    status)
        show_status
        ;;
    clean)
        clean_environment
        ;;
    deploy-dev)
        deploy_environment "dev"
        ;;
    deploy-test)
        deploy_environment "test"
        ;;
    deploy-prod)
        deploy_environment "prod"
        ;;
    update)
        update_services
        ;;
    backup)
        backup_database
        ;;
    restore)
        restore_database "$2"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}未知命令: $1${NC}"
        show_help
        exit 1
        ;;
esac
