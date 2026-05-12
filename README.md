# Personal Finance Tracker

个人财务记账系统，第一版按单用户闭环设计：

- `account` 账户管理
- `category` 分类管理
- `bill` 账单管理
- `statistics` 统计看板

## 启动方式

这个项目按本地常驻运行设计，推荐使用 Docker 一键拉起前端、后端和 MySQL。

1. Docker 一键启动：

```bash
docker compose up -d --build
```

2. 打开页面：

```text
前端: http://localhost:45173
后端: http://localhost:48080
MySQL: localhost:43306
```

3. 本地开发启动也保留：

```bash
cd backend
mvn spring-boot:run

cd frontend
npm install
npm run dev
```

前端开发服务器默认把 `/api` 代理到 `http://localhost:48080`，如果容器内运行会自动切到后端服务名。

## 数据库

初始化脚本在 `db/schema.sql`。第一次启动 MySQL 容器时会自动执行。

如果需要重新初始化数据库：

```bash
docker compose down -v
docker compose up -d --build
```

## 前端结构

- `src/layouts` - 页面布局
- `src/pages` - 路由页面
- `src/shared/components` - 可复用组件
- `src/shared/stores` - 状态管理与后端数据聚合
- `src/shared/api` - API 请求封装
- `src/shared/types` - 前端视图类型与后端响应类型
- `src/shared/utils` - 格式化、颜色等纯工具
- `src/shared/directives` - 通用交互指令，例如点击波纹
- `src/shared/navigation.ts` - 主导航配置
- `src/styles` - 全局样式

前端已接入后端接口，页面切换、按钮点击波纹、卡片 hover 等动态效果集中在布局、指令和全局样式中维护。

## 后端结构

- `common` - 统一返回、异常、分页
- `config` - MyBatis-Plus 配置
- `account` - 账户模块
- `category` - 分类模块
- `bill` - 账单模块
- `statistics` - 看板统计模块

后端使用 Spring Boot + MyBatis-Plus + MySQL，不使用 Lombok。

## 维护约定

- `frontend/dist`、`backend/target`、`node_modules` 等构建产物不作为源码维护，已经由 `.gitignore` 忽略。
- README 中的端口、目录说明和启动方式需要随代码同步更新。
