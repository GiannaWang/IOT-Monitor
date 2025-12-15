# 物联网监控平台

一个基于 Vue 3 + Spring Boot 的物联网数据监控平台，支持传感器数据采集、实时监控、数据分析和告警管理。

## 快速开始

### 一键启动

```bash
./start.sh
```

启动完成后访问：**http://localhost:5173**

### 停止服务

```bash
./stop.sh
```

### 检查状态

```bash
./status.sh
```

## 技术栈

### 前端
- Vue 3.5.18 (Composition API)
- Vite 7.1.2
- Element Plus 2.11.1
- ECharts 6.0.0
- Axios

### 后端
- Spring Boot 3.5.7
- MyBatis-Plus 3.5.7
- MySQL 9.5
- Druid 连接池

## 功能模块

- 🏠 **首页仪表盘** - 实时监控系统状态、设备运行情况
- 📱 **设备管理** - 管理传感器设备
- 📊 **数据分析** - 可视化展示传感器数据趋势
- ⚠️ **告警中心** - 异常告警处理和历史记录
- 👤 **管理员** - 用户信息和系统设置

## 详细文档

查看 [START_PROJECT.md](START_PROJECT.md) 获取完整的安装和配置指南。

## 服务端口

- 后端 API：http://localhost:8080
- 前端界面：http://localhost:5173

## 日志位置

- 后端日志：`/tmp/iot-backend.log`
- 前端日志：`/tmp/iot-frontend.log`

## 常用命令

```bash
# 查看后端日志
tail -f /tmp/iot-backend.log

# 查看前端日志
tail -f /tmp/iot-frontend.log

# 检查服务状态
./status.sh

# 手动启动后端（用于开发）
cd backend/IOT-Backend && mvn spring-boot:run

# 手动启动前端（用于开发）
npm run dev
```

## 数据库

数据库名称：`monitor`

主要数据表：
- `users` - 用户信息
- `sensor_devices` - 传感器设备
- `sensor_datas` - 传感器数据
- `locations` - 位置信息
- `alerts` - 告警记录
- `alert_rules` - 告警规则

## 开发环境

- Java 21
- Maven 3.9.11
- Node.js 22.21.1
- MySQL 9.5

## 许可

本项目仅供学习和研究使用。
