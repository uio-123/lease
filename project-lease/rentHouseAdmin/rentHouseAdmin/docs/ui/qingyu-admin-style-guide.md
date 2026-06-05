# 青寓运营后台 UI Style Guide

## 1. Brand Direction

青寓运营后台是 `rentHouseAdmin` 的后管新品牌。目标是在青寓品牌（清爽可信）基础上，满足后台管理的运营效率和数据密集需求。

品牌气质：

- **运营效率优先**：表格、搜索、表单等后台核心操作工具保持紧凑，避免营销落地页风格的宽大白色区块。
- **青绿色调**：侧边栏深青绿，主色青绿，CTA 按钮使用专业蓝，页面浅青色背景。
- **数据密集但清爽**：信息卡柔和边框和低强度阴影，表格表头浅青底、行 hover 方便扫读。
- **专业稳重**：使用 Element Plus 原生表格、分页、弹层、抽屉布局，只调整颜色、圆角和间距，不重构交互。

不采用的方向：

- 不走黑金轻奢风。
- 不走强促销红色风。
- 不使用后台模板默认蓝色和深黑侧边栏。
- 不继续暴露模板品牌，如 `guigu-rent-house-admin`、`#001529`、`#409EFF`、`后台管理`。

## 2. Component Library

后台端组件库固定选择 **Element Plus 2.x**。

选择原因：

- 当前项目已依赖 `element-plus@^2.3.7`，并已大量使用 el-table、el-form、el-menu、el-drawer、el-dialog 等组件。
- 项目是后台管理系统，Element Plus 的表格、表单、分页、菜单与后台场景匹配度最高。
- 继续使用 Element Plus 可以避免组件库迁移带来的样式冲突、包体增量和交互回归。

组件库边界：

- `rentHouseAdmin` 不引入 Vant、Naive UI、Ant Design Vue、TDesign、Arco Design Vue、Vuetify。
- `rentHouseAdmin` 的图表继续使用 ECharts 5 + vue-echarts。
- 图标使用 Element Plus Icons + SVG Sprite + Iconify（在线图标）。

## 3. Design Tokens

### Core Colors

| Token | Value | Usage |
|-------|-------|-------|
| Primary | `#0F766E` | 品牌主色、菜单激活态、表格操作按钮 |
| Secondary | `#14B8A6` | 辅助色、统计数值、图标辅色 |
| CTA | `#0369A1` | 搜索按钮、确认操作 |
| Background | `#F6FAF9` | 页面主背景、内容区 |
| Surface | `#FFFFFF` | 卡片、表单区域、弹层 |
| Text | `#134E4A` | 标题、核心正文 |
| Border | `#D6F5EF` | 卡片边框、表单边界、分隔线 |
| Sidebar | `#073B3A` | 侧边栏深青绿底色 |

### Extended Tokens

| Token | Value | Usage |
|-------|-------|-------|
| Muted Text | `#64748B` | 次要信息、表头辅助文 |
| Soft Surface | `#ECFEFF` | 表头背景、选中项背景 |
| Table Hover | `#F0FDFA` | 表格行 hover 背景 |
| Shadow | `0 2px 8px rgba(15, 118, 110, 0.08)` | 卡片柔和阴影 |
| Shadow Card | `0 1px 4px rgba(15, 118, 110, 0.06)` | 轻量卡片阴影 |
| Warning | `#F59E0B` | 待处理状态、提醒 |
| Danger | `#EF4444` | 删除、取消、失败 |
| Success | `#10B981` | 已完成、签约成功 |

### Dark Mode

- 背景使用深青灰 #0D1F1E / #0A1817，不使用纯黑。
- 侧边栏在暗色模式下更深，但保留可读边界。
- Primary 和 Secondary 适度提亮，保证对比度。
- 所有表格、按钮、标签在暗色模式下保持清晰。

## 4. Typography

字体策略：

- 中文使用系统字体栈：`"PingFang SC", Arial, "Microsoft YaHei", sans-serif`。
- 数字和表格内容可使用系统等宽 fallback。
- 不使用远程字体，不随 viewport 等比缩放。
- 不使用负字距。

层级建议：

| Role | Size | Weight | Usage |
|------|------|--------|-------|
| Page Title | 18-20px | 700 | 页面标题 |
| Card Title | 15-16px | 700 | 卡片标题、ProTable标题 |
| Table Header | 13px | 600 | 表格表头 |
| Body | 14px | 400 | 正文、字段值 |
| Meta | 12px | 400 | 描述、辅助文字 |

## 5. Layout Rules

### Sidebar

- 宽度 256px（折叠后 64px）。
- 背景色 `#073B3A`（亮色）/ `#071716`（暗色）。
- 菜单文字浅青白色，激活态青绿色高亮条。
- Logo 区使用青寓品牌图标 + 后台名称。

### NavBar

- 高度 60px，白色/半透明背景。
- 左侧：折叠按钮 + 面包屑。
- 右侧：刷新、全屏、设置、用户头像。
- 阴影细线 1px。

### TabsBar

- 高度 55px（标签 34px），多标签导航。
- 激活标签：浅青背景 + 左侧 3px 青绿色实线。
- 未激活标签：白色背景，hover 时浅青。

### Main Content

- 背景色 `#F6FAF9`。
- 内边距 16px。
- 内容区嵌入 ProTable 卡片和 SearchForm。

## 6. Component Styling Rules

### ProTable

- 卡片式容器，白色背景，圆角 8px，柔和阴影。
- 标题区含表格名 + 工具按钮（刷新、全屏、列设置）。
- 表头背景 `#ECFEFF`，文字 `#0F766E`，字号 13px，字重 600。
- 行 hover 背景 `#F0FDFA`。
- 分页选中按钮使用 `#0F766E`。

### SearchForm

- 紧凑搜索面板，白色背景，圆角 8px。
- 搜索按钮使用 CTA `#0369A1`，重置按钮弱化。
- Grid 布局响应式控制每行搜索项数量。

### Buttons

- 主按钮（Primary）：`#0F766E`，圆角适中。
- 强行动按钮（CTA）：`#0369A1`，仅用于搜索、提交等关键操作。
- 危险按钮：`#EF4444`，仅用于删除、取消。
- 最小触控高度 32px（后台操作密度）。

### Drawer/Dialog

- header 和 footer 边框使用 `#D6F5EF`。
- 内容区保持 Element Plus 默认间距和样式。
- 双列表单保持现有布局。

## 7. Page Patterns

### Login

- 全屏居中布局，渐变背景 `#F0FDFA → #ECFEFF → #E0F2FE`。
- 白色卡片，大圆角 16px，柔和阴影。
- 登录标题 "青寓运营后台"，副标题 "公寓运营管理系统"。
- 输入框使用 Element Plus 原生样式 + 图标前缀。

### Home Dashboard

- 欢迎卡片：时间问候 + 用户名 + 统计数据（今日预约/在租合同/房源总数）。
- 快捷入口网格：6 个导航卡片，带 hover 上浮效果。
- 待处理事项：动态展示状态标签列表，空状态显示 `el-empty`。

### List Pages

- 依赖 `ProTable` 和 `SearchForm` 的全局样式收敛。
- 页面内不硬编码颜色值。
- 状态标签使用青寓 token（Primary/Warning/Danger/Success）。

## 8. Acceptance Checklist

- [ ] 页面标题显示为 `青寓运营后台`。
- [ ] favicon 显示青寓后台图标。
- [ ] 侧边栏为深青绿，激活态使用青绿色高亮。
- [ ] TabsBar 激活标签使用浅青底 + 左竖线。
- [ ] 表格表头使用浅青背景，行 hover 有反馈。
- [ ] 搜索表单使用紧凑面板，搜索按钮为 CTA 蓝色。
- [ ] 登录页使用青寓品牌风格。
- [ ] 首页运营工作台显示问候语、统计数据、快捷入口。
- [ ] 暗色模式下文字、按钮、表格对比度可读。
- [ ] no remaining old brand: `guigu`, `硅谷`, `#409eff`, `#001529` in runtime code.
