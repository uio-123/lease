# 青寓 H5 UI Refresh Plan

## 1. Goal

将 `rentHouseH5` 租客端统一升级为 **青寓** 品牌风格，并基于 **Vant 4** 完成移动端 UI 体系收敛。改版重点是找房列表、房源详情、个人中心，以及预约、合同、登录等关联流程的一致性补齐。

成功标准：

- 品牌名、favicon、默认头像、启动页统一为青寓。
- H5 端继续使用 Vant 4，不迁移到其他 Vue 组件库。
- 核心页面符合清爽可信风格，移动端 375px、390px、414px 下可用。
- 旧模板品牌露出被清理。
- 文档、预览和代码风格保持一致，后续新增页面可按文档扩展。

## 2. Implementation Sequence

### Phase 1: Brand Foundation

- 更新 `.env.development` 和 `.env.production` 中的页面标题为 `青寓`。
- 更新 `index.html` 的默认 `<title>`、favicon、loading 文案和 loading 色彩。
- 新增或替换青寓品牌图标，使用“青色屋檐 + 简化门窗”方向。
- 保留一份可直接打开的审稿预览页，例如 `public/qingyu-preview.html`，展示找房列表、房源详情、个人中心三个手机画幅。

### Phase 2: Global Theme

- 在 `src/styles/variables.less` 集中定义青寓 token。
- 在 `src/styles/index.less` 覆盖 Vant 主题变量和全局页面基底。
- 统一设置字体栈、页面背景、卡片、按钮、标签、Tabbar、NavBar、Dropdown、Skeleton、Empty 等基础样式。
- 保证暗色模式下颜色可读，不出现透明卡片和低对比度文字。

### Phase 3: Core Components

- `SearchBar`：
  - 保留 `van-dropdown-menu` / `van-dropdown-item`。
  - 筛选项改为轻量胶囊风格。
  - 选中态使用 Primary 和 Soft Surface。
  - 确认按钮使用 CTA 或 Primary，重置按钮使用弱化样式。
- `RoomCard` / `ApartmentCard`：
  - 保留 `van-card`、`van-image`、`van-tag`。
  - 强化图片、标题、地址、标签、价格层级。
  - 控制图片比例、卡片圆角、阴影和标签换行。
- `Tabbar` / `NavBar` / `LoadingButton`：
  - 统一使用青寓主色和触控尺寸。
  - 避免单页重复写死颜色。

### Phase 4: Core Pages

- 找房列表：
  - 增加青寓品牌头图区。
  - 搜索筛选条和房源列表使用统一间距。
  - 空状态使用 Vant Empty 并替换模板感视觉。
- 房源详情 / 公寓详情：
  - 首图使用稳定高度的 `van-swipe`。
  - 基础信息、配套、费用、付款方式、租期统一为信息卡。
  - 底部预约 CTA 使用 `van-sticky`，正文预留底部空间。
- 个人中心：
  - 默认头像使用青寓图标。
  - 头图区改为清爽品牌背景。
  - 入口和退出按钮符合 Vant 主题。
- 登录、预约、合同、我的预约、我的合同：
  - 表单、卡片、按钮、状态标签补齐青寓 token。
  - 清理红色硬编码和模板默认图片。

## 3. Component Library Policy

H5 端组件库固定为 Vant 4：

- 新增移动端基础控件优先查 Vant 4。
- Vant 无法覆盖的轻量展示样式使用本地 Vue 组件和 Less/Tailwind 样式实现。
- 不为单个页面引入新的 UI 组件库。
- 如果未来需要跨端框架能力，再单独评估 Quasar；本轮不纳入。
- 如果未来后台管理端改版，另起计划评估 Element Plus 或其他后台组件库。

Recommended Vant mapping:

| Scenario | Vant Component |
| --- | --- |
| 全局主题 | `van-config-provider` |
| 找房筛选 | `van-dropdown-menu`, `van-dropdown-item`, `van-popup`, `van-picker` |
| 房源卡片 | `van-card`, `van-image`, `van-tag` |
| 详情轮播 | `van-swipe`, `van-swipe-item`, `van-image` |
| 底部导航 | `van-tabbar`, `van-tabbar-item` |
| 顶部导航 | `van-nav-bar`, `van-sticky` |
| 表单预约 | `van-form`, `van-field`, `van-cell-group`, `van-date-picker`, `van-time-picker` |
| 状态反馈 | `showToast`, `showDialog`, `van-empty`, `van-skeleton`, `van-loading` |
| 行动按钮 | `van-button` |

## 4. Verification Plan

Commands:

```bash
npm run type-check
npm run build
```

Brand cleanup checks:

```bash
rg -n "硅谷租房|vue3-h5-template|logo_melomini" .
```

Visual QA:

- 使用移动端宽度 `375px`、`390px`、`414px` 检查核心页面。
- 检查找房列表是否横向溢出。
- 检查房源详情底部 CTA 是否遮挡内容。
- 检查 Tabbar 选中态、图标、文字是否清晰。
- 检查筛选弹层按钮是否容易点击。
- 检查暗色模式下文字、按钮、标签、卡片是否可读。
- 打开 `public/qingyu-preview.html` 确认审稿预览可见。

Known environment note:

- 如果沙箱环境下 `npm run build` 因 esbuild 读取权限失败，应在本机普通终端重新执行构建，并记录真实结果。

## 5. Acceptance Criteria

- `docs/ui/qingyu-style-guide.md` 成为青寓 H5 风格来源文档。
- `docs/ui/qingyu-ui-refresh-plan.md` 成为后续实现顺序和验收来源文档。
- H5 端组件库选择明确为 Vant 4。
- 设计 token、Vant 使用规范、品牌图标方向、核心页面改造规则均已记录。
- 后续开发不需要再判断选哪个 Vue 组件库，可直接按文档执行。

## 6. Assumptions

- 本轮只处理 `rentHouseH5`，不处理 `rentHouseAdmin`。
- 品牌名固定为 **青寓**。
- 视觉风格固定为 **清爽可信**。
- 预览图用于审稿，不作为项目运行时依赖。
- 后续新增 UI 页面默认遵循本目录下的青寓风格文档。
