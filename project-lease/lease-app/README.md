# 公寓租赁 APP 前端

基于 React + TypeScript + Vite 的公寓租赁应用前端项目。

## 功能特性

- 🏠 公寓浏览：按区域筛选、查看公寓详情
- 🛏️ 房间查询：分页查看房间、查看房间详情
- 📅 看房预约：在线预约看房、查看预约记录
- 📝 租约管理：签约、查看租约、申请退租
- 📜 浏览历史：记录浏览足迹
- 👤 个人中心：用户信息管理

## 技术栈

- React 18
- TypeScript
- Vite
- React Router 6
- Axios
- Zustand (状态管理)
- Ant Design Mobile (UI组件)
- Lucide React (图标)

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

## 项目结构

```
src/
├── api/           # API 请求封装
├── components/    # 公共组件
├── hooks/         # 自定义 Hooks
├── pages/         # 页面组件
│   ├── HomePage.tsx           # 首页
│   ├── LoginPage.tsx          # 登录页
│   ├── ApartmentDetailPage.tsx # 公寓详情
│   ├── RoomDetailPage.tsx     # 房间详情
│   ├── AppointmentListPage.tsx # 预约列表
│   ├── AgreementListPage.tsx  # 租约列表
│   ├── HistoryPage.tsx        # 浏览历史
│   └── ProfilePage.tsx       # 个人中心
├── store/         # 状态管理
├── types/         # TypeScript 类型定义
├── utils/         # 工具函数
├── App.tsx        # 根组件
└── main.tsx       # 入口文件
```

## API 接口

后端 API 基础路径: `/app`

### 登录模块
- `GET /app/login/getCode` - 获取短信验证码
- `POST /app/login` - 用户登录
- `GET /app/info` - 获取用户信息

### 地区信息
- `GET /app/region/province/list` - 获取省份列表
- `GET /app/region/city/listByProvinceId` - 获取城市列表
- `GET /app/region/district/listByCityId` - 获取区县列表

### 公寓信息
- `GET /app/apartment/getDetailById` - 获取公寓详情
- `GET /app/apartment/listByDistrictId` - 按区县获取公寓列表
- `GET /app/apartment/listByQuery` - 条件查询公寓

### 房间信息
- `GET /app/room/pageItem` - 分页查询房间
- `GET /app/room/getDetailById` - 获取房间详情
- `GET /app/room/pageItemByApartmentId` - 按公寓查询房间

### 预约管理
- `POST /app/appointment/saveOrUpdate` - 保存预约
- `GET /app/appointment/listItem` - 获取预约列表
- `GET /app/appointment/getDetailById` - 获取预约详情

### 租约管理
- `GET /app/agreement/listItem` - 获取租约列表
- `GET /app/agreement/getDetailById` - 获取租约详情
- `POST /app/agreement/updateStatusById` - 更新租约状态
- `POST /app/agreement/saveOrUpdate` - 保存租约

### 浏览历史
- `GET /app/history/pageItem` - 获取浏览历史

### 支付和租期
- `GET /app/payment/list` - 获取支付方式列表
- `GET /app/payment/listByRoomId` - 获取房间可选支付方式
- `GET /app/term/listByRoomId` - 获取房间可选租期

## 配置代理

项目配置了开发服务器代理，将 `/app` 请求转发到后端服务器。

在 `vite.config.ts` 中修改：

```ts
server: {
  proxy: {
    '/app': {
      target: 'http://localhost:8080', // 修改为你的后端地址
      changeOrigin: true,
    },
  },
},
```

## License

MIT
