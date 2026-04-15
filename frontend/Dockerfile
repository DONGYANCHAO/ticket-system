# Stage 1: Build stage
FROM node:20-alpine AS builder

WORKDIR /build

COPY package*.json ./

RUN npm ci --registry=https://registry.npmmirror.com

COPY . .

RUN npm run build

# Stage 2: Runtime stage
FROM nginx:alpine

LABEL maintainer="Ticket System"
LABEL version="1.0.0"
LABEL description="Ticket System Frontend Service"

RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

COPY --from=builder /build/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD curl -f http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
