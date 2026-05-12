# 阶段二：功能补全与交互打磨

## 已完成任务

### 后端

- **账单分页与高级筛选** — 支持按月份、类型、分类、账户、起止时间组合筛选，分页查询账单列表。
- **账户余额一致性** — 账单新增/编辑/删除时自动回滚对应账户余额，确保统计一致。
- **分类与账户完整 CRUD** — 分类和账户各自独立模块，含 controller、service、mapper、entity、DTO/VO 全层。
- **统计接口** — 新增月度汇总（收入/支出/结余/总资产）、近 6 个月收支趋势、当前月支出分类占比、账户余额快照四个统计接口。
- **统一响应格式** — ApiResponse 统一包装返回体，BusinessException + GlobalExceptionHandler 集中异常处理。
- **分页工具** — PageResult 泛型封装分页结果。

### 前端

- **仪表板** — 首页展示本月收入/支出/结余/总资产四张统计卡片；SVG 折线+面积图展示近 6 个月收支趋势；SVG 环形饼图展示支出分类占比；最近账单列表；账户概览。
- **账单管理页** — 列表分页、组合筛选面板（月份/类型/分类/账户/起止时间）、新建/编辑弹窗、详情弹窗、删除确认。支持通过 URL query 参数预筛选（从首页饼图点击跳转传入 `month`、`categoryId`、`type`）。
- **账户管理页** — 账户列表含余额展示，新建/编辑/详情弹窗，删除确认。
- **分类管理页** — 分类列表含类型和颜色标识，新建/编辑弹窗，删除确认。
- **共享组件** — PageHeader、AppSelect、StatCard（含数字滚动动画）、EmptyState（含插图）、BillFormDialog、BillDetailDialog、AccountFormDialog、AccountDetailDialog、CategoryFormDialog、ConfirmDialog、QuickCreateModal。
- **状态管理** — Pinia finance store 集中管理财务数据，ui store 管理全局 UI 状态。
- **API 层** — finance API 封装所有后端接口调用，http 客户端统一拦截与错误处理。
- **饼图交互** — SVG 环形图支持扇形和右侧图例点击，跳转至账单页并自动按分类筛选；扇形 hover 采用 brightness + drop-shadow 浮起效果，图例 hover 有平移+高亮反馈。
- **视觉打磨** — 现代简约风格，CSS 变量统一品牌色/阴影/圆角/过渡；卡片微纹理；按钮波纹反馈；弹窗进出动画；列表行悬停微交互；响应式布局适配窄屏。

## 备注

- 本阶段聚焦功能完整性和交互手感，前端从单纯展示扩展为可筛选、分页、编辑、删除的完整管理工具。
- 饼图从 CSS conic-gradient 方案迭代为纯 SVG 方案，解决了点击区域与涂色区域不一致、伪元素遮挡、hover 效果溢出等问题。
