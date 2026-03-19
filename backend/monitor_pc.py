#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import time
import psutil
from datetime import datetime

import win32gui
import win32process

INTERVAL = 5  # 采样间隔（秒）


def log(msg):
    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{now}] {msg}")


# ===== 获取前台进程 =====
def get_foreground_pid():
    try:
        hwnd = win32gui.GetForegroundWindow()
        _, pid = win32process.GetWindowThreadProcessId(hwnd)
        return pid
    except:
        return None


# ===== 核心处理函数（类似你 hi_api 结构） =====
def handle_pc_state(data):
    log(f"前台进程: {data['foreground_name']} | CPU: {data['cpu']}% | 内存: {data['memory']}%")
    log(f"总进程: {data['total']} | 后台进程: {data['background']} | 后台比例: {data['ratio']}")
    print("-" * 50)


# ===== 主采集逻辑 =====
def collect_pc_state():
    processes = list(psutil.process_iter(['pid', 'name']))

    total = len(processes)
    fg_pid = get_foreground_pid()

    background = 0
    fg_name = "unknown"

    for p in processes:
        try:
            if p.info['pid'] == fg_pid:
                fg_name = p.info['name']
            else:
                background += 1
        except:
            continue

    cpu = psutil.cpu_percent(interval=1)
    memory = psutil.virtual_memory().percent

    ratio = round(background / total, 2) if total > 0 else 0

    return {
        "foreground_name": fg_name,
        "total": total,
        "background": background,
        "ratio": ratio,
        "cpu": cpu,
        "memory": memory
    }


def main():
    log("PC monitor started...")

    while True:
        try:
            data = collect_pc_state()
            handle_pc_state(data)

            time.sleep(INTERVAL)

        except KeyboardInterrupt:
            log("用户退出")
            break
        except Exception as e:
            log(f"错误: {e}")
            time.sleep(3)


if __name__ == "__main__":
    main()