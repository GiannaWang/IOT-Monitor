"""
Windows Agent - IOT Monitor 系统的 Windows 机器采集脚本
功能：定期上报 CPU/内存/磁盘使用率，并在开关机时上报电源事件

部署方法：
  1. 安装依赖：pip install psutil requests
  2. 修改下方配置（SERVER_URL、LOCATION_ID 等）
  3. 运行：python windows_agent.py
  4. 开机自启：将 windows_agent.py 添加到 Windows 任务计划程序，
     触发器设为"计算机启动时"即可。

电源事件钩子（需要 Windows 任务计划程序配合）：
  - 开机：触发器选"启动时"，操作为 python windows_agent.py --event startup
  - 关机：触发器选"注销时"，操作为 python windows_agent.py --event shutdown
"""

import sys
import os
import time
import socket
import uuid
import argparse
import requests
import psutil

# ============ 配置区 ============
SERVER_URL = "http://localhost:8080"   # 后端地址，部署后改为实际 IP
REPORT_INTERVAL = 60                   # 心跳上报间隔（秒）
LOCATION_ID = 1                        # 该机器所在房间 ID（按实际填写）
# ================================


def get_machine_id() -> str:
    """生成唯一机器 ID：主机名 + MAC 地址"""
    hostname = socket.gethostname()
    mac = uuid.getnode()
    mac_str = ':'.join(f'{(mac >> (i * 8)) & 0xff:02x}' for i in range(5, -1, -1))
    return f"{hostname}-{mac_str}"


def get_current_user() -> str:
    try:
        return os.environ.get("USERNAME") or os.environ.get("USER") or "unknown"
    except Exception:
        return "unknown"


def get_os_version() -> str:
    try:
        import platform
        return platform.version()
    except Exception:
        return "unknown"


def collect_metrics() -> list:
    """采集 CPU、内存、磁盘使用率"""
    metrics = []

    # CPU 使用率（非阻塞，interval=1 秒采样一次）
    cpu = psutil.cpu_percent(interval=1)
    metrics.append({"type": "CPU使用率", "value": round(cpu, 1), "unit": "%"})

    # 内存使用率
    mem = psutil.virtual_memory()
    metrics.append({"type": "内存使用率", "value": round(mem.percent, 1), "unit": "%"})

    # 磁盘使用率（主分区 C:\）
    try:
        disk_path = "C:\\" if sys.platform == "win32" else "/"
        disk = psutil.disk_usage(disk_path)
        metrics.append({"type": "磁盘使用率", "value": round(disk.percent, 1), "unit": "%"})
    except Exception:
        pass

    return metrics


def send_heartbeat():
    """发送心跳（含当前指标）到后端"""
    machine_id = get_machine_id()
    hostname = socket.gethostname()
    ip = socket.gethostbyname(hostname)

    payload = {
        "machineId": machine_id,
        "hostname": hostname,
        "ipAddress": ip,
        "username": get_current_user(),
        "osVersion": get_os_version(),
        "locationId": LOCATION_ID,
        "reportInterval": REPORT_INTERVAL,
        "metrics": collect_metrics()
    }

    resp = requests.post(f"{SERVER_URL}/windows/heartbeat", json=payload, timeout=10)
    resp.raise_for_status()
    print(f"[{time.strftime('%Y-%m-%d %H:%M:%S')}] 心跳上报成功: {payload['metrics']}")


def send_event(event_type: str):
    """上报电源事件（startup / shutdown / sleep / wake）"""
    machine_id = get_machine_id()
    payload = {"machineId": machine_id, "eventType": event_type}

    resp = requests.post(f"{SERVER_URL}/windows/event", json=payload, timeout=10)
    resp.raise_for_status()
    print(f"[{time.strftime('%Y-%m-%d %H:%M:%S')}] 事件上报成功: {event_type}")


def main():
    parser = argparse.ArgumentParser(description="IOT Monitor - Windows Agent")
    parser.add_argument("--event", choices=["startup", "shutdown", "sleep", "wake"],
                        help="上报单次电源事件后退出")
    args = parser.parse_args()

    if args.event:
        # 单次事件模式（由任务计划程序触发）
        try:
            send_event(args.event)
        except Exception as e:
            print(f"事件上报失败: {e}", file=sys.stderr)
            sys.exit(1)
        sys.exit(0)

    # 持续心跳模式
    print(f"Windows Agent 启动 | 机器 ID: {get_machine_id()} | 间隔: {REPORT_INTERVAL}s")

    # 启动时先上报一次 startup 事件
    try:
        send_event("startup")
    except Exception as e:
        print(f"启动事件上报失败（忽略）: {e}", file=sys.stderr)

    while True:
        try:
            send_heartbeat()
        except Exception as e:
            print(f"[{time.strftime('%Y-%m-%d %H:%M:%S')}] 心跳上报失败: {e}", file=sys.stderr)
        time.sleep(REPORT_INTERVAL)


if __name__ == "__main__":
    main()
