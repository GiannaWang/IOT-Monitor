# 物联网监控平台 - 启动指南

## 📦 一、前置软件安装

### 1. 安装 Java 21
```bash
# macOS (使用 Homebrew)
brew install openjdk@21
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 验证安装
java -version
```

### 2. 安装 Maven
```bash
brew install maven
mvn -version
```

### 3. 安装 MySQL 8
```bash
brew install mysql
brew services start mysql

# 设置 root 密码
mysql_secure_installation
```

### 4. Node.js 和 npm
✅ 已安装（Node v18.20.1, npm 10.5.0）

---

## 🗄️ 二、配置数据库

### 1. 登录 MySQL
```bash
mysql -u root -p
# 输入密码：Wang20050520 (如果这是你的MySQL密码)
```

### 2. 创建数据库
```sql
CREATE DATABASE monitor CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE monitor;
```

### 3. 导入数据库表结构
```sql
-- 在 MySQL 命令行中执行
SOURCE /Users/wangmengting2/program/IOT-Monitor/database/monitor/table.sql;

-- 或者在终端执行
mysql -u root -p monitor < database/monitor/table.sql
```

### 4. 导入测试数据（可选）
```sql
SOURCE /Users/wangmengting2/program/IOT-Monitor/database/monitor/test_datas.sql;
```

### 5. 验证数据库
```sql
-- 查看所有表
SHOW TABLES;

-- 应该看到以下表：
-- alert_rules
-- alerts
-- locations
-- sensor_datas
-- sensor_devices
-- users
```

---

## ⚙️ 三、配置后端

### 1. 修改数据库密码配置

编辑文件：`backend/IOT-Backend/src/main/resources/application.properties`

```properties
spring.application.name=IOT-Backend
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/monitor?useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的MySQL密码  # 修改这里！
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

### 2. 编译后端项目
```bash
cd backend/IOT-Backend
mvn clean install
```

---

## 🚀 四、启动项目

### 方式一：分别启动（推荐用于开发）

#### 1️⃣ 启动后端
```bash
# 在项目根目录
cd backend/IOT-Backend

# 方式A：使用 Maven
mvn spring-boot:run

# 方式B：使用编译后的 jar
mvn clean package
java -jar target/IOT-Backend-0.0.1-SNAPSHOT.jar
```

**启动成功标志：**
```
Started IotBackendApplication in X.XXX seconds
```

后端地址：http://localhost:8080

#### 2️⃣ 启动前端（新开一个终端）
```bash
# 在项目根目录
npm install  # 首次运行需要安装依赖
npm run dev
```

**启动成功标志：**
```
  VITE v7.1.2  ready in XXX ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

前端地址：http://localhost:5173

---

### 方式二：使用 IDE 启动（IDEA 用户）

#### 后端：
1. 用 IntelliJ IDEA 打开 `backend/IOT-Backend` 文件夹
2. 等待 Maven 依赖下载完成
3. 找到 `IotBackendApplication.java`
4. 右键 → Run 'IotBackendApplication'

#### 前端：
1. 在 VS Code 中打开项目根目录
2. 打开终端
3. 运行 `npm run dev`

---

## ✅ 五、验证启动

### 1. 测试后端 API
```bash
# 测试传感器数据接口
curl http://localhost:8080/getAllSensorData

# 测试告警接口
curl http://localhost:8080/getLatest5Alerts

# 测试设备统计接口
curl http://localhost:8080/countOnlineDevices
```

### 2. 访问前端
浏览器打开：http://localhost:5173

应该能看到登录界面或主页面

---

## 🐛 六、常见问题

### 问题 1：后端启动报错 "连接数据库失败"
**解决方案：**
- 检查 MySQL 是否启动：`brew services list`
- 检查数据库是否创建：`mysql -u root -p` 然后 `SHOW DATABASES;`
- 检查密码是否正确：`application.properties` 中的密码

### 问题 2：前端启动报错 "端口被占用"
**解决方案：**
```bash
# 查找占用端口的进程
lsof -i :5173

# 结束进程
kill -9 <PID>
```

### 问题 3：前端访问后端 404
**解决方案：**
- 确认后端已启动（访问 http://localhost:8080）
- 检查跨域配置是否正确
- 查看浏览器控制台错误信息

### 问题 4：Maven 依赖下载慢
**解决方案：**
编辑 `~/.m2/settings.xml`，添加阿里云镜像：
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

---

## 📂 七、项目结构

```
IOT-Monitor/
├── backend/
│   └── IOT-Backend/          # Spring Boot 后端
│       ├── src/
│       │   └── main/
│       │       ├── java/     # Java 源代码
│       │       └── resources/
│       │           └── application.properties  # 配置文件
│       └── pom.xml           # Maven 配置
├── database/
│   └── monitor/              # 数据库脚本
│       ├── table.sql         # 表结构
│       └── test_datas.sql    # 测试数据
├── src/                      # Vue 前端源代码
│   ├── components/           # Vue 组件
│   ├── utils/                # 工具类
│   └── assets/               # 静态资源
├── package.json              # npm 配置
└── vite.config.js            # Vite 配置
```

---

## 🎯 八、一键启动和管理脚本

项目已包含三个管理脚本，无需手动创建：

### 📦 可用脚本：

1. **`start.sh`** - 一键启动所有服务
2. **`stop.sh`** - 一键停止所有服务
3. **`status.sh`** - 检查服务运行状态

### 🚀 启动项目：

```bash
./start.sh
```

脚本会自动：
- ✅ 检查并启动 MySQL 服务
- ✅ 启动后端服务（Spring Boot）
- ✅ 启动前端服务（Vue + Vite）
- ✅ 等待服务完全启动
- ✅ 显示访问地址和日志位置

启动完成后，直接访问：**http://localhost:5173**

### 🛑 停止项目：

```bash
./stop.sh
```

### 📊 检查状态：

```bash
./status.sh
```

显示所有服务的运行状态和健康检查结果。

### 📋 查看日志：

```bash
# 查看后端日志
tail -f /tmp/iot-backend.log

# 查看前端日志
tail -f /tmp/iot-frontend.log
```

---

## 📞 需要帮助？

如果遇到问题，请检查：
1. 所有软件版本是否正确
2. MySQL 是否正常运行
3. 数据库是否正确创建和导入
4. 后端和前端日志的错误信息

祝你使用愉快！🎉
