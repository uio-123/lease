# 青寓 H5 UI Style Guide

## 1. Brand Direction

青寓是 `rentHouseH5` 租客端的新品牌名。视觉目标是清爽、可信、轻量，优先服务移动端找房、看房源、预约、合同和个人中心等租客核心流程。

品牌气质：

- 清爽可信：使用青绿色建立安心、干净、真实的租住印象。
- 移动优先：所有关键操作满足 H5 触控体验，按钮和筛选项保持清晰可点。
- 房源优先：真实房源图片是视觉主体，装饰元素只用于增强层级。
- 轻量现代：柔和卡片、低强度阴影、圆角适中，避免后台系统感和强促销感。

不采用的方向：

- 不走黑金轻奢风。
- 不走强促销红色风。
- 不使用桌面后台式密集表格布局。
- 不继续暴露模板品牌，如 `vue3-h5-template`、`logo_melomini`、`硅谷租房`。

## 2. Component Library

H5 端组件库固定选择 **Vant 4**。

选择原因：

- 当前项目已依赖 `vant@^4.6.0`，并已大量使用 `van-card`、`van-image`、`van-tag`、`van-dropdown-menu`、`van-tabbar`、`van-config-provider` 等组件。
- 项目是移动端 H5 租客端，Vant 与移动端表单、筛选、列表、Tabbar、Popup、Picker 等场景匹配度最高。
- 继续使用 Vant 可以避免组件库迁移带来的样式冲突、包体增量和交互回归。

组件库边界：

- `rentHouseH5` 不引入 Element Plus、Ant Design Vue、Naive UI、Vuetify、Quasar、PrimeVue、Arco Design Vue、TDesign Vue Next。
- `rentHouseAdmin` 后台如果需要改版，可单独按后台产品路线保留或升级 Element Plus，不纳入本指南。

## 3. Design Tokens

Core colors:

| Token | Value | Usage |
| --- | --- | --- |
| Primary | `#0F766E` | 品牌主色、Tabbar 选中态、主要标签、重要高亮 |
| Secondary | `#14B8A6` | 渐变辅助色、轻量状态、图标辅助色 |
| CTA | `#0369A1` | 预约、提交、确认等强行动按钮 |
| Background | `#F0FDFA` | 页面主背景、浅色品牌区 |
| Text | `#134E4A` | 标题、核心正文 |
| Border | `#CCFBF1` | 卡片边框、筛选项边界、浅色分割 |
| Surface | `#FFFFFF` | 卡片、弹层、列表容器 |

Suggested extended tokens:

| Token | Value | Usage |
| --- | --- | --- |
| Muted Text | `#64748B` | 次要信息、说明文案、位置和时间 |
| Soft Surface | `#ECFEFF` | 轻提示、选中背景、空状态背景 |
| Shadow | `0 12px 28px rgba(15, 118, 110, 0.12)` | 房源卡片、品牌头图、浮层 |
| Warning | `#F59E0B` | 待处理状态、提醒 |
| Danger | `#EF4444` | 删除、取消、失败 |

Dark mode:

- 背景使用深青灰，不使用纯黑。
- 卡片保持可读的深色 surface，边框降低透明度。
- Primary 和 Secondary 可适度提亮，保证标签和按钮对比度。
- 所有价格、CTA、Tabbar 选中态必须在暗色模式下保持清晰。

## 4. Typography

字体策略：

- 中文优先使用系统字体栈：`-apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif`。
- 英文和数字参考 Plus Jakarta Sans 的现代感，但不强制引入远程字体。
- 不使用负字距，不用随 viewport 等比缩放字体。

层级建议：

| Role | Size | Weight | Usage |
| --- | --- | --- | --- |
| Page Title | 20-24px | 700 | 页面主标题、品牌露出 |
| Card Title | 16-18px | 700 | 房源名、公寓名 |
| Body | 14-16px | 400-500 | 正文、字段值 |
| Meta | 12-13px | 400 | 地址、时间、说明 |
| Price | 18-24px | 800 | 租金、金额 |

## 5. Vant Theming Rules

使用 `van-config-provider` 作为 Vant 主题入口，配合全局 Less token 覆盖组件变量。

需要统一覆盖的组件：

- Button：主按钮使用 CTA，普通 primary 使用 Primary，圆角适中，最小触控高度 `44px`。
- Tag：默认青绿色浅底，重要状态用 Primary，危险状态仅用于取消、删除、失败。
- Tabbar：背景为 Surface，选中态使用 Primary，未选中为 Muted Text。
- NavBar：标题使用 Text，返回箭头使用 Primary。
- DropdownMenu：筛选栏使用浅色胶囊视觉，选中态使用 Primary 和 Soft Surface。
- Card：卡片白底、浅边框、柔和阴影，图片保持明确尺寸和圆角。
- Skeleton / Empty：加载和空状态使用品牌浅色，不出现模板默认猫图或突兀灰块。
- Popup / Picker：底部弹层保持 Vant 原生移动体验，只调整颜色、圆角和按钮状态。

## 6. Core Patterns

### Search Bar

- 用 `van-dropdown-menu` 和 `van-dropdown-item` 承载地区、价格、付款方式、排序。
- 筛选项视觉为轻量胶囊，不做重边框按钮堆叠。
- 选中项使用 Primary 文本和 Soft Surface 背景。
- 下拉面板底部操作使用两个按钮：重置为次按钮，确认为 CTA 或 Primary。

### Room / Apartment Card

- 图片是卡片第一视觉重点，必须有固定比例和圆角。
- 标题、区域、标签、价格形成稳定层级。
- 价格使用较高字重和 CTA/Primary 体系色。
- 标签数量较多时允许换行，不允许撑破卡片。
- 卡片间距保持 12-16px，避免列表压迫感。

### Detail Page

- 首图使用 `van-swipe`，高度稳定，图片不拉伸变形。
- 基础信息、配套、费用、付款方式、租期等区块使用一致的信息卡。
- 底部预约 CTA 使用 `van-sticky`，底部预留安全距离，不能遮挡内容。
- 信息图标使用线性、轻量、青绿色，不使用重装饰图标。

### User Center

- 默认头像使用青寓品牌图标。
- 头图区使用清爽品牌背景，避免纯模板渐变。
- 操作入口卡片化，文字和图标保持可扫读。
- 退出登录按钮使用主按钮或危险弱化样式，避免误触风险。

## 7. Brand Assets

青寓图标方向：

- 青色屋檐。
- 简化门窗。
- 小尺寸下仍能识别为居住/公寓品牌。
- 用于 favicon、默认头像、启动页品牌露出和预览稿。

资源规范：

- favicon 指向青寓图标。
- 默认头像使用青寓图标。
- 启动页 loading 使用青寓品牌色。
- 审稿预览图只作为设计产物，不作为运行时页面依赖。

## 8. Acceptance Checklist

- 页面标题显示为 `青寓`。
- favicon 显示青寓图标。
- H5 源码不再出现 `硅谷租房`、`vue3-h5-template`、`logo_melomini` 品牌露出。
- 375px、390px、414px 宽度无横向溢出。
- 房源详情底部 CTA 不遮挡正文。
- Tabbar 图标和文字清晰可读。
- 暗色模式下文字、按钮、卡片、标签对比度可读。
- 新增页面和组件默认优先使用 Vant 4，而不是引入其他 Vue 组件库。
