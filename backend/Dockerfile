# ==========================================
# 工单系统后端 Dockerfile
# 使用多阶段构建优化镜像大小
# 基础镜像: eclipse-temurin:17-jre-alpine
# ==========================================

# ---------------- 阶段1: 构建阶段 ----------------
FROM eclipse-temurin:17-jdk-alpine AS builder

# 设置工作目录
WORKDIR /build

# 安装必要的工具
RUN apk add --no-cache maven

# 先复制 pom.xml 和依赖相关文件（利用 Docker 缓存层）
COPY pom.xml .

# 下载依赖（这一步会被缓存，除非 pom.xml 改变）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 打包应用（跳过测试，测试在 CI 阶段执行）
RUN mvn clean package -DskipTests -B \
    && mkdir -p target/dependency \
    && cd target/dependency \
    && jar -xf ../*.jar

# ---------------- 阶段2: 运行阶段 ----------------
FROM eclipse-temurin:17-jre-alpine

# 维护者信息
LABEL maintainer="ticket-system-team"
LABEL description="Ticket System Backend Service"

# 设置时区
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apk del tzdata

# 创建应用用户（安全最佳实践）
RUN addgroup -S ticket && adduser -S ticket -G ticket

# 设置工作目录
WORKDIR /app

# 创建必要的目录
RUN mkdir -p /data/files /app/logs && \
    chown -R ticket:ticket /app /data

# 从构建阶段复制依赖和类文件
COPY --from=builder /build/target/dependency/BOOT-INF/lib /app/lib
COPY --from=builder /build/target/dependency/META-INF /app/META-INF
COPY --from=builder /build/target/dependency/BOOT-INF/classes /app

# 设置文件权限
RUN chown -R ticket:ticket /app

# 切换到非 root 用户
USER ticket

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.backgroundpreinitializer.ignore=true", \
    "-cp", "app:app/lib/*", \
    "com.ticket.TicketApplication" \
]
