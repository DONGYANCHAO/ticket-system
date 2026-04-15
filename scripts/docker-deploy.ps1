# ==========================================
# 工单系统 Docker 部署脚本 (PowerShell)
# 适用于 Windows 环境
# ==========================================

param(
    [Parameter()]
    [ValidateSet("up", "down", "restart", "build", "logs", "clean", "status", "deploy-dev", "deploy-test", "deploy-prod", "update", "backup", "help")]
    [string]$Action = "help",

    [Parameter()]
    [string]$Service = "",

    [Parameter()]
    [string]$Env = "dev"
)

# 颜色定义
$Colors = @{
    Blue = "Cyan"
    Green = "Green"
    Yellow = "Yellow"
    Red = "Red"
    NC = "White"
}

function Write-ColorMessage {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

function Show-Help {
    Write-ColorMessage "工单系统 Docker 部署脚本" $Colors.Blue
    Write-ColorMessage ""
    Write-ColorMessage "用法:" $Colors.Green
    Write-ColorMessage "  .\docker-deploy.ps1 -Action <命令> [-Service <服务名>] [-Env <环境>]"
    Write-ColorMessage ""
    Write-ColorMessage "命令:" $Colors.Green
    Write-ColorMessage "  up          - 启动所有服务（后台运行）"
    Write-ColorMessage "  down        - 停止并移除所有服务"
    Write-ColorMessage "  restart     - 重启所有服务"
    Write-ColorMessage "  build       - 构建所有服务镜像"
    Write-ColorMessage "  logs        - 查看服务日志"
    Write-ColorMessage "  status      - 查看服务状态"
    Write-ColorMessage "  clean       - 清理所有容器和卷"
    Write-ColorMessage "  deploy-dev  - 部署开发环境"
    Write-ColorMessage "  deploy-test - 部署测试环境"
    Write-ColorMessage "  deploy-prod - 部署生产环境"
    Write-ColorMessage "  update      - 更新并重启服务"
    Write-ColorMessage "  backup      - 备份数据库"
    Write-ColorMessage "  help        - 显示帮助信息"
    Write-ColorMessage ""
    Write-ColorMessage "服务名（用于 logs 命令）:" $Colors.Green
    Write-ColorMessage "  backend, frontend, mysql, redis"
    Write-ColorMessage ""
    Write-ColorMessage "示例:" $Colors.Yellow
    Write-ColorMessage "  .\docker-deploy.ps1 -Action up"
    Write-ColorMessage "  .\docker-deploy.ps1 -Action logs -Service backend"
    Write-ColorMessage "  .\docker-deploy.ps1 -Action deploy-prod"
}

function Start-Services {
    Write-ColorMessage "正在启动所有服务..." $Colors.Green
    docker-compose up -d
    Write-ColorMessage "服务已启动，访问: http://localhost" $Colors.Green
}

function Stop-Services {
    Write-ColorMessage "正在停止所有服务..." $Colors.Yellow
    docker-compose down
}

function Restart-Services {
    Write-ColorMessage "正在重启所有服务..." $Colors.Yellow
    docker-compose restart
}

function Build-Services {
    Write-ColorMessage "正在构建所有服务镜像..." $Colors.Green
    docker-compose build
}

function Show-Logs {
    param([string]$svc)
    if ($svc) {
        Write-ColorMessage "正在查看 $svc 日志..." $Colors.Blue
        docker-compose logs -f $svc
    } else {
        Write-ColorMessage "正在查看所有服务日志..." $Colors.Blue
        docker-compose logs -f
    }
}

function Show-Status {
    Write-ColorMessage "服务状态:" $Colors.Blue
    docker-compose ps
}

function Clear-Environment {
    Write-ColorMessage "警告: 这将删除所有容器和数据卷！" $Colors.Red
    $confirm = Read-Host "确定要继续吗? [y/N]"
    if ($confirm -eq "y" -or $confirm -eq "Y") {
        docker-compose down -v --remove-orphans
        Write-ColorMessage "环境清理完成" $Colors.Green
    } else {
        Write-ColorMessage "操作已取消" $Colors.Yellow
    }
}

function Deploy-Environment {
    param([string]$environment)
    Write-ColorMessage "正在部署 $environment 环境..." $Colors.Green

    $envFile = ".env.$environment"
    if (Test-Path $envFile) {
        Copy-Item $envFile .env -Force
    }

    $env:SPRING_PROFILES_ACTIVE = $environment
    docker-compose -f docker-compose.yml up -d --build
    Write-ColorMessage "$environment 环境部署完成" $Colors.Green
}

function Update-Services {
    Write-ColorMessage "正在更新服务..." $Colors.Green
    git pull
    docker-compose down
    docker-compose build --no-cache
    docker-compose up -d
    Write-ColorMessage "服务更新完成" $Colors.Green
}

function Backup-Database {
    Write-ColorMessage "正在备份数据库..." $Colors.Green
    $backupDir = "backups"
    if (!(Test-Path $backupDir)) {
        New-Item -ItemType Directory -Path $backupDir | Out-Null
    }
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $backupFile = "$backupDir\backup_$timestamp.sql"

    $mysqlPassword = docker-compose exec -T mysql printenv MYSQL_ROOT_PASSWORD 2>$null
    docker-compose exec -T mysql mysqldump -u root -p"$mysqlPassword" ticket_system > $backupFile
    Write-ColorMessage "数据库备份完成: $backupFile" $Colors.Green
}

# 主逻辑
switch ($Action) {
    "up" { Start-Services }
    "down" { Stop-Services }
    "restart" { Restart-Services }
    "build" { Build-Services }
    "logs" { Show-Logs -svc $Service }
    "status" { Show-Status }
    "clean" { Clear-Environment }
    "deploy-dev" { Deploy-Environment -environment "dev" }
    "deploy-test" { Deploy-Environment -environment "test" }
    "deploy-prod" { Deploy-Environment -environment "prod" }
    "update" { Update-Services }
    "backup" { Backup-Database }
    "help" { Show-Help }
    default { Show-Help }
}
