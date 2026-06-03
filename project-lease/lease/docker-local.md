# Docker 本地开发环境

通过 Docker Compose 在本地启动 MySQL、Redis、MinIO，代替原本虚拟机（`192.168.109.128`）中的服务。后端和前端仍然在本机启动，只需切换 Spring Profile 即可连接 Docker 内容器。

---

## 服务概览

| 服务 | 镜像 | Docker 端口 | 访问地址 |
|------|------|-------------|----------|
| MySQL | `mysql:8.0` | `3307:3306`（本机→容器内） | `localhost:3307` |
| Redis | `redis:7` | `6379:6379` | `localhost:6379` |
| MinIO | `minio/minio` | `9000:9000`（API）<br>`9001:9001`（控制台） | API: `http://localhost:9000`<br>控制台: `http://localhost:9001` |

---

## 账号密码

### MySQL
- 用户名: `root`
- 密码: `Dcw.0415`
- 数据库: `lease`
- 字符集: `utf8mb4`
- 本机访问端口: `3307`（映射到容器内 `3306`）
- 容器内访问端口: `3306`（`docker compose exec` 等内部命令使用）

### Redis
- 密码: `dchw0415`
- Database: `0`
- AOF 持久化已开启（`appendonly yes`）

### MinIO
- 用户名: `minioadmin`
- 密码: `minioadmin`
- Bucket: `lease`

---

## 文件说明

所有 Docker 相关文件统一放在 `project-lease/lease/` 下：

| 文件 | 说明 |
|------|------|
| `docker-compose.yml` | Compose 编排文件，定义 MySQL、Redis、MinIO 三个服务 |
| `docker-local.md` | **本文档** — 使用说明与数据迁移指南 |
| `web/web-admin/src/main/resources/application-local.yml` | web-admin 的本地配置（覆盖 MySQL、Redis、MinIO 地址为 localhost） |
| `web/web-app/src/main/resources/application-local.yml` | web-app 的本地配置（覆盖 MySQL、Redis、MinIO 地址为 localhost） |

---

## 启动与停止

### 启动所有服务

```bash
# 在 docker-compose.yml 所在目录下执行
cd project-lease/lease
docker compose up -d
```

首次启动会自动拉取镜像，创建并挂载 Docker volume：
- `lease-mysql-data` — MySQL 数据持久化
- `lease-redis-data` — Redis AOF 持久化
- `lease-minio-data` — MinIO 文件数据

启动后检查服务状态：

```bash
docker compose ps
```

预期输出三个服务均为 `Up`（healthy）。也可单独检查：

```bash
docker compose ps mysql
docker compose ps redis
docker compose ps minio
```

### 查看服务日志

```bash
docker compose logs -f mysql
docker compose logs -f redis
docker compose logs -f minio
```

### 停止服务（保留数据）

```bash
docker compose down
```

Docker volume **默认保留**，下次 `docker compose up -d` 时数据仍然存在。

### 停止服务并删除数据卷（⚠️ 谨慎操作）

如果希望彻底清除数据，重新开始：

```bash
docker compose down -v
```

> **注意**：`-v` 会同时删除 `lease-mysql-data`、`lease-redis-data`、`lease-minio-data` 三个 volume，数据将永久丢失。

---

## 后端启动方式

### web-admin（端口 8080）

在 IDE 或命令行中，激活 `local` profile：

```bash
# Maven 启动
cd web/web-admin
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 或 java -jar（需先打成 jar）
java -jar web-admin.jar --spring.profiles.active=local
```

在 IDEA 中：
1. 打开 `AdminWebApplication` 入口类
2. 右上角 → "Edit Configurations..."
3. **VM options**（推荐）添加：`-Dspring.profiles.active=local`
4. 或 **Program arguments** 添加：`--spring.profiles.active=local`
5. 点击运行

### web-app（端口 8081）

```bash
# Maven 启动
cd web/web-app
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 或 java -jar
java -jar web-app.jar --spring.profiles.active=local
```

IDEA 配置方式同上，入口类为 `AppWebApplication`。

### 验证后端连接

启动后检查控制台日志，无 `DataSource` / `Redis` / `MinIO` 连接错误即表示成功。

---

## 备用方案：虚拟机方案

原有 `application.yml` 中仍保留 `192.168.109.128` 虚拟机的配置，未做任何修改。需要回退到虚拟机方案时，直接不使用 `local` profile 启动即可（默认读取 `application.yml`）。

---

## 数据迁移

以下步骤将虚拟机 `192.168.109.128` 中的 MySQL 数据库和 MinIO 文件迁移到本地 Docker 容器。

### 1. MySQL 迁移

#### 1.1 从虚拟机导出

在 **本机** 执行（需本机安装 `mysqldump`，或通过 SSH 在虚拟机上执行后将文件 scp 回来）：

```bash
mysqldump -h 192.168.109.128 -u root -pDcw.0415 --databases lease > lease_dump.sql
```

或在虚拟机内部执行：

```bash
# 登录虚拟机 192.168.109.128 后执行
mysqldump -u root -pDcw.0415 --databases lease > /tmp/lease_dump.sql
# 然后将文件复制到本机
scp root@192.168.109.128:/tmp/lease_dump.sql ./
```

#### 1.2 导入到 Docker MySQL

```bash
# 确保 Docker MySQL 容器已启动
docker compose up -d mysql

# 导入
docker compose exec -T mysql mysql -u root -pDcw.0415 < lease_dump.sql
```

#### 1.3 验证导入

```bash
docker compose exec mysql mysql -u root -pDcw.0415 -e "USE lease; SHOW TABLES;"
```

### 2. MinIO 文件迁移

使用 MinIO 客户端 `mc` 进行 bucket 同步。

#### 2.1 安装 mc（如未安装）

**Windows（使用 Chocolatey）：**

```bash
choco install minio-client
```

**macOS：**

```bash
brew install minio-client
```

**Linux：**

```bash
wget https://dl.min.io/client/mc/release/linux-amd64/mc
chmod +x mc
sudo mv mc /usr/local/bin/
```

#### 2.2 配置别名

```bash
# 配置旧（虚拟机）MinIO
mc alias set old-minio http://192.168.109.128:9000 minioadmin minioadmin

# 配置新（Docker）MinIO
mc alias set new-minio http://localhost:9000 minioadmin minioadmin
```

#### 2.3 创建目标 bucket 并同步

```bash
# 先在 Docker MinIO 中创建 lease bucket（如果不存在）
mc mb --ignore-existing new-minio/lease

# 将旧 minio 的 lease bucket 同步到新 minio 的 lease bucket
mc mirror old-minio/lease new-minio/lease
```

#### 2.4 验证同步

```bash
mc ls new-minio/lease
```

也可以在浏览器中打开 MinIO 控制台 `http://localhost:9001`，用 `minioadmin / minioadmin` 登录查看。

### 3. 数据库 URL 替换

迁移后，数据库中已有的文件 URL 仍指向 `http://192.168.109.128:9000/lease/...`，需要替换为 `http://localhost:9000/lease/...`。

在 Docker MySQL 中执行以下 SQL：

```sql
-- 替换 graph_info 表中的图片 URL
UPDATE graph_info
SET url = REPLACE(url, 'http://192.168.109.128:9000/lease', 'http://localhost:9000/lease')
WHERE url LIKE 'http://192.168.109.128:9000/lease%';

-- 替换 user_info 表中的头像 URL
UPDATE user_info
SET avatar_url = REPLACE(avatar_url, 'http://192.168.109.128:9000/lease', 'http://localhost:9000/lease')
WHERE avatar_url LIKE 'http://192.168.109.128:9000/lease%';

-- 替换 system_user 表中的头像 URL
UPDATE system_user
SET avatar_url = REPLACE(avatar_url, 'http://192.168.109.128:9000/lease', 'http://localhost:9000/lease')
WHERE avatar_url LIKE 'http://192.168.109.128:9000/lease%';
```

> **提示**：如果还有其它字段存储文件 URL，可先查找确认：
> ```sql
> SELECT TABLE_NAME, COLUMN_NAME
> FROM information_schema.COLUMNS
> WHERE TABLE_SCHEMA = 'lease'
>   AND (COLUMN_NAME LIKE '%url%' OR COLUMN_NAME LIKE '%image%' OR COLUMN_NAME LIKE '%pic%');
> ```

执行后验证替换效果：

```sql
SELECT url FROM graph_info WHERE url LIKE 'http://localhost:9000/lease%' LIMIT 5;
SELECT avatar_url FROM user_info WHERE avatar_url LIKE 'http://localhost:9000/lease%' LIMIT 5;
SELECT avatar_url FROM system_user WHERE avatar_url LIKE 'http://localhost:9000/lease%' LIMIT 5;
```

---

## 验证测试

迁移完成后，执行以下验证：

1. **检查容器状态**
   ```bash
   docker compose ps
   # 确认 mysql、redis、minio 均为 healthy 或 running
   ```

2. **验证 MySQL 数据**
   ```bash
   docker compose exec mysql mysql -u root -pDcw.0415 -e "USE lease; SELECT COUNT(*) FROM apartment_info;"
   ```

3. **验证 MinIO 文件**
   - 打开 `http://localhost:9001`，登录后查看 `lease` bucket 中的文件列表

4. **验证后端启动**
   - 用 `local` profile 启动 web-admin 和 web-app
   - 检查启动日志无连接错误

5. **验证文件上传**
   - 访问后台上传接口或页面上传图片
   - 确认新文件落到 Docker MinIO，返回 URL 为 `http://localhost:9000/lease/...`

6. **验证历史图片显示**
   - 打开已有房间/公寓详情页
   - 确认迁移前的图片能正常显示

---

## 注意事项

- Docker Desktop 需提前安装并确保可以正常运行容器。
- 三个 Docker volume（`lease-mysql-data`、`lease-redis-data`、`lease-minio-data`）统一以 `lease-` 前缀命名，方便识别与管理。
- SQL dump 文件和 MinIO 迁移出的数据**不提交进 Git**，只提交 Compose 文件、local Profile 配置文件及本文档。
- 如果在使用 Docker 时遇到 `.docker/config.json` 权限问题，只在 `docker compose` 实际失败时再处理。
- 本文档编码为 **UTF-8**。如果在 PowerShell 中查看出现中文乱码，请使用以下命令读取：
  ```powershell
  Get-Content -Encoding UTF8 .\docker-local.md
  ```
