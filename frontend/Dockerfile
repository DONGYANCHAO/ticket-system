# ==========================================
# 工单系统前端 Dockerfile
# 使用多阶段构建优化镜像大小
# 基础镜像: node:18-alpine (构建) + nginx:alpine (运行)
# ==========================================

# ---------------- 阶段1: 构建阶段 ----------------
FROM node:18-alpine AS builder

# 设置工作目录
WORKDIR /app

# 设置环境变量
ENV NODE_ENV=production

# 安装 pnpm（更快的包管理器）
RUN npm install -g pnpm

# 先复制包管理文件（利用 Docker 缓存层）
COPY package.json pnpm-lock.yaml* ./

# 安装依赖
RUN pnpm install --frozen-lockfile

# 复制源代码
COPY . .

# 构建应用
RUN pnpm run build

# ---------------- 阶段2: 运行阶段 ----------------
FROM nginx:alpine

# 维护者信息
LABEL maintainer="ticket-system-team"
LABEL description="Ticket System Frontend Service"

# 设置时区
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 删除默认的 Nginx 配置
RUN rm -rf /etc/nginx/conf.d/default.conf

# 复制自定义 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/

# 复制构建产物到 Nginx 目录
COPY --from=builder /app/dist /usr/share/nginx/html

# 创建必要的目录并设置权限
RUN mkdir -p /var/cache/nginx/client_temp && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# 暴露端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]
