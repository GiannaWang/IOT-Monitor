#!/bin/bash

# 物联网监控平台 - 停止脚本

echo "======================================"
echo "  物联网监控平台 - 停止中..."
echo "======================================"

# 从 PID 文件读取并停止
if [ -f /tmp/iot-backend.pid ]; then
    BACKEND_PID=$(cat /tmp/iot-backend.pid)
    if kill -0 $BACKEND_PID 2>/dev/null; then
        echo "停止后端服务 (PID: $BACKEND_PID)..."
        kill $BACKEND_PID
        rm /tmp/iot-backend.pid
    else
        echo "后端服务未运行"
        rm -f /tmp/iot-backend.pid
    fi
else
    # 尝试通过端口查找并停止
    echo "查找后端进程..."
    BACKEND_PIDS=$(lsof -ti:8080)
    if [ -n "$BACKEND_PIDS" ]; then
        echo "停止后端服务..."
        echo $BACKEND_PIDS | xargs kill 2>/dev/null
    else
        echo "未找到后端服务"
    fi
fi

if [ -f /tmp/iot-frontend.pid ]; then
    FRONTEND_PID=$(cat /tmp/iot-frontend.pid)
    if kill -0 $FRONTEND_PID 2>/dev/null; then
        echo "停止前端服务 (PID: $FRONTEND_PID)..."
        kill $FRONTEND_PID
        rm /tmp/iot-frontend.pid
    else
        echo "前端服务未运行"
        rm -f /tmp/iot-frontend.pid
    fi
else
    # 尝试通过端口查找并停止
    echo "查找前端进程..."
    FRONTEND_PIDS=$(lsof -ti:5173)
    if [ -n "$FRONTEND_PIDS" ]; then
        echo "停止前端服务..."
        echo $FRONTEND_PIDS | xargs kill 2>/dev/null
    else
        echo "未找到前端服务"
    fi
fi

# 清理相关的 Maven 和 Node 进程
echo "清理相关进程..."
pkill -f "mvn spring-boot:run" 2>/dev/null
pkill -f "IOT-Backend" 2>/dev/null

echo ""
echo "======================================"
echo "  所有服务已停止"
echo "======================================"
