#!/bin/bash

# 物联网监控平台 - 状态检查脚本

echo "======================================"
echo "  物联网监控平台 - 服务状态"
echo "======================================"
echo ""

# 检查后端
echo "【后端服务】"
if [ -f /tmp/iot-backend.pid ]; then
    BACKEND_PID=$(cat /tmp/iot-backend.pid)
    if ps -p $BACKEND_PID > /dev/null 2>&1; then
        echo "✓ 后端正在运行 (PID: $BACKEND_PID)"
        # 测试 API
        if curl -s http://localhost:8080/countOnlineDevices > /dev/null 2>&1; then
            echo "✓ 后端 API 响应正常 (http://localhost:8080)"
        else
            echo "⚠ 后端进程存在但 API 未响应"
        fi
    else
        echo "✗ 后端已停止 (PID 文件存在但进程不存在)"
        rm -f /tmp/iot-backend.pid
    fi
else
    # 通过端口检查
    if lsof -i:8080 > /dev/null 2>&1; then
        PID=$(lsof -ti:8080)
        echo "✓ 后端正在运行 (PID: $PID, 无 PID 文件)"
    else
        echo "✗ 后端未运行"
    fi
fi

echo ""

# 检查前端
echo "【前端服务】"
if [ -f /tmp/iot-frontend.pid ]; then
    FRONTEND_PID=$(cat /tmp/iot-frontend.pid)
    if ps -p $FRONTEND_PID > /dev/null 2>&1; then
        echo "✓ 前端正在运行 (PID: $FRONTEND_PID)"
        # 测试前端
        if curl -s http://localhost:5173 > /dev/null 2>&1; then
            echo "✓ 前端页面响应正常 (http://localhost:5173)"
        else
            echo "⚠ 前端进程存在但页面未响应"
        fi
    else
        echo "✗ 前端已停止 (PID 文件存在但进程不存在)"
        rm -f /tmp/iot-frontend.pid
    fi
else
    # 通过端口检查
    if lsof -i:5173 > /dev/null 2>&1; then
        PID=$(lsof -ti:5173)
        echo "✓ 前端正在运行 (PID: $PID, 无 PID 文件)"
    else
        echo "✗ 前端未运行"
    fi
fi

echo ""

# 检查 MySQL
echo "【MySQL 服务】"
if pgrep -x mysqld > /dev/null; then
    echo "✓ MySQL 正在运行"
else
    echo "✗ MySQL 未运行"
fi

echo ""
echo "======================================"
echo "提示："
echo "  - 启动服务: ./start.sh"
echo "  - 停止服务: ./stop.sh"
echo "  - 查看日志: tail -f /tmp/iot-backend.log"
echo "======================================"
