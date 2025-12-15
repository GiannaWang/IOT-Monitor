#!/bin/bash

# 物联网监控平台 - 一键启动脚本

# 获取脚本所在目录的绝对路径
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "======================================"
echo "  物联网监控平台 - 启动中..."
echo "======================================"

# 检查 MySQL 是否运行
echo ""
echo "[1/4] 检查 MySQL 服务..."
if ! pgrep -x mysqld > /dev/null; then
    echo "MySQL 未运行，正在启动..."
    brew services start mysql
    sleep 3
else
    echo "✓ MySQL 已运行"
fi

# 启动后端
echo ""
echo "[2/4] 启动后端服务..."
cd "$SCRIPT_DIR/backend/IOT-Backend"
nohup mvn spring-boot:run > /tmp/iot-backend.log 2>&1 &
BACKEND_PID=$!
echo "✓ 后端启动中... (PID: $BACKEND_PID)"
echo "  日志文件: /tmp/iot-backend.log"

# 保存后端 PID
echo "$BACKEND_PID" > /tmp/iot-backend.pid

# 等待后端启动
echo "  等待后端完全启动（需要15-20秒）..."
for i in {1..20}; do
    if curl -s http://localhost:8080/countOnlineDevices > /dev/null 2>&1; then
        echo "✓ 后端启动成功！(http://localhost:8080)"
        break
    fi
    if [ $i -eq 20 ]; then
        echo "⚠ 后端启动超时，请检查日志: tail -f /tmp/iot-backend.log"
    else
        sleep 1
    fi
done

# 启动前端
echo ""
echo "[3/4] 启动前端服务..."
cd "$SCRIPT_DIR"

# 确保使用正确的 Node 版本
if [ -f "$HOME/.nvm/nvm.sh" ]; then
    source "$HOME/.nvm/nvm.sh"
    nvm use 22 > /dev/null 2>&1
fi

nohup npm run dev > /tmp/iot-frontend.log 2>&1 &
FRONTEND_PID=$!
echo "✓ 前端启动中... (PID: $FRONTEND_PID)"
echo "  日志文件: /tmp/iot-frontend.log"

# 保存前端 PID
echo "$FRONTEND_PID" > /tmp/iot-frontend.pid

# 等待前端启动
echo "  等待前端完全启动..."
sleep 5

echo ""
echo "[4/4] 启动完成！"
echo "======================================"
echo "  服务运行状态："
echo "  - 后端 PID: $BACKEND_PID"
echo "  - 前端 PID: $FRONTEND_PID"
echo ""
echo "  访问地址："
echo "  - 后端 API: http://localhost:8080"
echo "  - 前端界面: http://localhost:5173"
echo ""
echo "  查看日志："
echo "  - 后端: tail -f /tmp/iot-backend.log"
echo "  - 前端: tail -f /tmp/iot-frontend.log"
echo ""
echo "  停止服务: ./stop.sh"
echo "  检查状态: ./status.sh"
echo "======================================"
echo ""
echo "✓ 所有服务已在后台启动！"
