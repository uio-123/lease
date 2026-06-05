# 青寓运营后台 UI Refresh Plan

## 1. Goal

将 `rentHouseAdmin` 后台管理系统统一升级为 **青寓运营后台** 品牌风格，并基于 **Element Plus** 完成后台 UI 体系收敛。改版重点是侧边栏、顶栏、TabsBar、ProTable、SearchForm、首页工作台和登录页。

成功标准：

- 品牌名、favicon、页面标题统一为青寓。
- 后台端继续使用 Element Plus，不迁移到其他 UI 库。
- 侧边栏从深蓝色改为深青绿，导航菜单风格统一。
- 表格、搜索、表单等工具区卡面化、清爽化。
- 旧模板品牌露出被清理。
- 文档、样式和代码风格保持一致。

## 2. Implementation Sequence

### Phase 1: Brand Foundation

- 更新 `.env.development` 和 `.env.production` 中的页面标题为 `青寓运营后台`。
- 更新 `index.html` 的 favicon 为 `/qingyu-admin-icon.svg`。
- 创建青寓后台图标 `public/qingyu-admin-icon.svg`。
- 创建青寓 Logo SVG `src/icons/svg/qingyu-logo.svg`（用于 SvgIcon 组件）。
- 更新 `package.json` 和 `package-lock.json` 的 name 为 `qingyu-admin`。
- 更新 `src/config/config.ts` 中 `DEFAULT_PRIMARY` 为主色 `#0F766E`。

### Phase 2: Global Theme

- 在 `src/styles/variable.scss` 中更新 SCSS 变量为青寓色系。
- 创建 `src/styles/qingyu-admin-variables.scss` 记录完整 CSS Design Tokens。
- 在 `src/styles/index.scss` 中导入新变量。
- 更新 `src/styles/element.scss` 中 Element Plus 覆盖：
  - 表头背景色、字号、边框色
  - Drawer/Dialog 边框色
- 更新 `src/styles/theme.scss` 中的暗色模式变量。

### Phase 3: Layout Styling

- `layout/Logo`：替换 logo 图片为青寓品牌图标。
- `layout/SideBar`：侧边栏背景色、菜单色、激活态、阴影。
- `layout/NavBar`：顶栏阴影色。
- `layout/TabsBar`：标签激活态改为浅青底 + 左竖线，移除原有 mask 背景图。
- `layout/Main`：内容区背景色和内边距。

### Phase 4: Component Styling

- `ProTable` 卡片样式：圆角、阴影、表头背景、行 hover、分页。
- `SearchForm` 面板样式：圆角、阴影、按钮颜色。

### Phase 5: Page Styling

- 首页：替换为运营工作台（问候语 + 统计数据 + 快捷入口 + 待处理事项）。
- 登录页：青寓品牌登录卡片，渐变背景，干净表单。

## 3. Component Library Policy

后台端组件库固定为 Element Plus：

- 新增后台业务组件优先查 Element Plus。
- Element Plus 无法覆盖的样式使用本地 SCSS 和 CSS 变量实现。
- 不为单个页面引入新的 UI 组件库。
- 如果未来需要 H5 端移动能力，另开 `rentHouseH5` 计划，不纳入本后台项目。

Recommended Element Plus mapping:

| Scenario | Component |
|----------|-----------|
| 全局主题 | `el-config-provider` |
| 菜单导航 | `el-menu`, `el-sub-menu`, `el-menu-item` |
| 高级表格 | `ProTable`（基于 `el-table`） |
| 搜索筛选 | `SearchForm`（基于 `el-form` + Grid） |
| 表单弹窗 | `el-drawer`, `el-dialog`, `el-form` |
| 分页 | `el-pagination` |
| 状态反馈 | `el-message`, `el-notification`, `el-empty`, `el-loading` |
| 按钮 | `el-button` |
| 标签 | `el-tag` |
| 面包屑 | `el-breadcrumb` |
| Tabs | `el-tabs` |

## 4. Verification Plan

Commands:

```bash
npm run build
```

Brand cleanup checks:

```bash
rg -n "guigu|硅谷|后台管理|logo\.png|#001529|#409eff|尚硅谷" .env.development .env.production index.html package.json src
```

Visual QA:

- 桌面宽度 1366px、1440px、1920px 检查核心页面。
- 窄屏 768px、1024px 侧边栏折叠正常。
- 检查登录页视觉风格是否统一。
- 检查首页工作台是否显示统计数据、快捷入口、待处理事项。
- 检查 ProTable 列表页表头浅青背景、行 hover 反馈。
- 检查 SearchForm 搜索面板卡片化风格。
- 检查 TabsBar 激活标签左侧绿色实线。
- 检查暗色模式下文字、表格、按钮对比度。

## 5. Acceptance Criteria

- `docs/ui/qingyu-admin-style-guide.md` 成为青寓后台风格来源文档。
- `docs/ui/qingyu-admin-ui-refresh-plan.md` 成为验证来源文档。
- 后台端组件库选择明确为 Element Plus。
- 设计 token、布局规则、ProTable/SearchForm 样式规则均已记录。
- 前端源码不再出现 `guigu-rent-house-admin`、`#409EFF`、`#001529` 等旧模板品牌。

## 6. Assumptions

- 本轮只处理 `rentHouseAdmin`，不处理 `rentHouseH5`。
- 品牌名固定为 **青寓运营后台**。
- 管理员端继续使用 **Element Plus**。
- 首页运营卡片使用现有可用信息或静态展示，不新增 API。
- 目标是风格统一和后台可用性提升，不重构业务逻辑。
- 如果有图标名称不一致等问题，需在构建预览时修复。
