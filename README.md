# Personal Finance Tracker — 个人财务记账系统

> A full-stack personal finance management application built with Spring Boot 3 and Vue 3.
> 基于 Spring Boot 3 + Vue 3 的全栈个人财务管理应用，支持账户管理、账单记录、预算控制和可视化统计。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-4fc08d)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ed)](https://docs.docker.com/compose/)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.x-orange)](https://baomidou.com/)

## 功能模块 / Features

| 模块 | 说明 |
|------|------|
| **账户管理 (Account)** | 管理资金账户（现金、银行卡、信用卡、支付宝、微信等），支持余额追踪 |
| **分类管理 (Category)** | 自定义收支分类，支持收入/支出双向分类，可设置预算 |
| **账单管理 (Bill)** | 记录每一笔收支，支持日期、金额、分类、账户、备注，分页查询与筛选 |
| **统计看板 (Dashboard)** | 月度收支汇总、分类支出占比、月度趋势图、账户余额一览 |
| **预算管理 (Budget)** | 为分类设置月度预算，实时查看预算使用进度与超支提醒 |

## 技术栈 / Tech Stack

### 后端 (Backend)
- **Spring Boot 3.4** — 应用框架
- **MyBatis-Plus 3.x** — ORM 与分页
- **MySQL 8.0** — 关系型数据库
- **Docker** — 容器化部署

### 前端 (Frontend)
- **Vue 3** (Composition API + `<script setup>`)
- **Vue Router 4** — 客户端路由
- **Pinia** — 状态管理
- **Vite** — 构建工具
- **TypeScript** — 类型安全
- **Axios** — HTTP 请求封装

### 运维 (DevOps)
- **Docker Compose** — 一键启动全栈服务
- **Nginx** — 前端静态资源服务

## 快速开始 / Quick Start

### Docker 一键启动（推荐）

需要先下载docker

```bash
git clone https://github.com/713469/personalFinance.git
cd personalFinance
docker compose up -d --build
```

启动后访问：

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:45173 |
| 后端 API | http://localhost:48080 |
| MySQL | localhost:43306 |

### 本地开发

**后端：**

```bash
cd backend
# 需要本地 MySQL 运行在 43306，或修改 application.yml
mvn spring-boot:run
```

**前端：**

```bash
cd frontend
npm install
npm run dev
```

前端开发服务器默认将 `/api` 代理到 `http://localhost:48080`。

## 数据库 / Database

初始化脚本位于 `db/schema.sql`，Docker 首次启动 MySQL 容器时自动执行。

重新初始化数据库：

```bash
docker compose down -v
docker compose up -d --build
```

## 项目结构 / Project Structure

```
personalfinancetracker/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/.../
│   │   ├── account/            # 账户模块 (controller/service/mapper/entity/dto)
│   │   ├── bill/               # 账单模块
│   │   ├── budget/             # 预算模块
│   │   ├── category/           # 分类模块
│   │   ├── statistics/         # 统计看板模块
│   │   ├── common/             # 统一返回、异常处理、分页
│   │   └── config/             # MyBatis-Plus 配置
│   └── src/main/resources/
│       └── application.yml     # 应用配置
├── frontend/                   # Vue 3 前端
│   └── src/
│       ├── pages/              # 路由页面 (Dashboard/Bill/Account/Category/Budget)
│       ├── shared/
│       │   ├── components/     # 可复用组件 (对话框/选择器/统计卡片等)
│       │   ├── stores/         # Pinia 状态管理
│       │   ├── api/            # Axios 请求封装
│       │   ├── types/          # TypeScript 类型定义
│       │   ├── utils/          # 格式化、颜色工具
│       │   └── directives/     # 交互指令 (点击波纹等)
│       ├── layouts/            # 页面布局
│       ├── router/             # 路由配置
│       └── styles/             # 全局样式
├── db/
│   └── schema.sql              # 数据库初始化脚本
└── docker-compose.yml          # Docker 编排
```

## 设计约定 / Design Notes

- **无 Lombok**：使用Vscode有时候会出现依赖没有被加载而大量爆红，干脆直接不适用lombok
- **BigDecimal 金额**：所有金额字段使用 `BigDecimal`，避免浮点精度问题
- **统一响应格式**：`ApiResponse<T>` 包装所有接口返回，`PageResult<T>` 统一分页结构
- **单用户闭环**：单纯按个人使用设计，不涉及多租户与权限

## 截图 / Screenshots

### 仪表盘

<img width="1264" height="719" alt="Dashboard" src="https://github.com/user-attachments/assets/cfcfeef5-fd2f-43c3-9cde-ea0dfd34eba1" />

### 账单管理

<img width="1278" height="719" alt="Bill Management" src="https://github.com/user-attachments/assets/e1917730-b371-4672-b5a7-e4f48c2c5f74" />

## License

<img width="1274" height="725" alt="image" src="https://github.com/user-attachments/assets/f044547b-4650-4144-a5be-df17eac9a3a7" />

