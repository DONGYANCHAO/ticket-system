---
AIGC:
    ContentProducer: Minimax Agent AI
    ContentPropagator: Minimax Agent AI
    Label: AIGC
    ProduceID: "00000000000000000000000000000000"
    PropagateID: "00000000000000000000000000000000"
    ReservedCode1: 3045022100870adb3b66af3ef85e1b96437f723dd891073d5c418fbb45d02343f3701d237302207e997f74a56ace111d593bec85fdd506690013bc32fcb8a73297d8f11a5952fc
    ReservedCode2: 30460221008182a6e4447490468fb6bcab82340edd297c691fd9205416a5c1edc77b1af821022100fbff3891ddad2e0d6bf69e6783e3135ce1d26d4706f591d67761f0b94d0d374a
---

# 客户工单系统

一款面向客服场景的客户工单管理系统，支持工单创建、分配、处理、评价全流程管理。

## 功能特性

### 核心功能
- ✅ 工单全生命周期管理
- ✅ 多角色权限控制
- ✅ 工单状态自动流转
- ✅ SLA服务等级管理
- ✅ 多种通知渠道（站内信、邮件、钉钉）
- ✅ 知识库与AI推荐
- ✅ 满意度评价
- ✅ 完整操作日志
- ✅ 自定义报表与订阅

### 高级功能
- ✅ 草稿自动保存
- ✅ 工单合并/拆分
- ✅ 工单标签管理
- ✅ 抄送功能
- ✅ 催单提醒
- ✅ 差评预警
- ✅ 快捷回复
- ✅ 数据导入导出

## 技术栈

### 后端
- Spring Boot 3.2
- MyBatis-Plus 3.5
- MySQL 8.0
- Caffeine (本地缓存)
- JWT (认证授权)
- Spring Security (权限控制)

### 前端
- Vue 3
- Element Plus
- Pinia (状态管理)
- Vue Router
- Vite (构建工具)
- ECharts (图表)

## 系统要求

- JDK 17+
- MySQL 8.0+
- Node.js 18+
- Nginx 1.20+

## 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/your-repo/ticket-system.git
cd ticket-system
```

### 2. 初始化数据库
```bash
mysql -u root -p < backend/src/main/resources/sql/init.sql
```

### 3. 启动后端
```bash
cd backend
# 修改 application.yml 中的数据库配置
mvn spring-boot:run
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### 5. 访问系统
- 前端地址: http://localhost:5173
- 后端地址: http://localhost:8080
- 默认账号: admin / admin123

## 项目结构

```
ticket-system/
├── backend/                    # 后端项目
│   ├── src/main/java/com/ticket/
│   │   ├── common/           # 公共组件
│   │   ├── config/           # 配置类
│   │   ├── modules/          # 功能模块
│   │   │   ├── auth/         # 认证模块
│   │   │   ├── customer/      # 客户管理
│   │   │   ├── ticket/       # 工单管理
│   │   │   ├── dashboard/    # 仪表盘
│   │   │   ├── report/        # 报表
│   │   │   └── system/       # 系统配置
│   │   └── TicketApplication.java
│   └── src/main/resources/
│       ├── application.yml    # 配置文件
│       └── sql/              # SQL脚本
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/              # API接口
│   │   ├── components/       # 通用组件
│   │   ├── layouts/          # 布局组件
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # 状态管理
│   │   ├── styles/           # 全局样式
│   │   ├── utils/            # 工具函数
│   │   └── views/            # 页面视图
│   └── package.json
│
└── docs/                      # 文档
    ├── 部署文档.md
    └── 使用手册.md
```

## API文档

启动后端后，访问 http://localhost:8080/swagger-ui.html 查看完整的API文档。

## 演示环境

**演示地址**: (待部署)

**演示账号**:
| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 技术支持 | support | 123456 |
| 客户 | customer | 123456 |

## 截图预览

### 工作台
![工作台](docs/images/dashboard.png)

### 工单列表
![工单列表](docs/images/ticket-list.png)

### 工单详情
![工单详情](docs/images/ticket-detail.png)

## 许可证

本项目采用 MIT 许可证。

## 技术支持

如有问题，请提交 Issue 或联系技术支持。
