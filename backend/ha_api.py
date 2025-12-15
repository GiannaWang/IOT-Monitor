
#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import json
import time
from datetime import datetime
import websocket   # pip install websocket-client
from typing import Optional

# ======== 配置 ========
HA_URL = "74.48.62.239:443"
HA_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiIwOWU2YmNjMWRmMTI0ZTUyODA2YTJhYTdiNjYzNDkzNyIsImlhdCI6MTc2NDkyMTkwMiwiZXhwIjoyMDgwMjgxOTAyfQ.DOoCIV6TWKNBgSHWNBOHREMLfcnFBQYhS2r0ud1qhT4"

MONITORED_SENSORS = {
    "sensor.cabinet_1_a",
    "sensor.cabinet_1_b",
    "sensor.cabinet_2_a",
    "sensor.cabinet_2_b",
}

CURRENT_LIMIT = 16.0
RECONNECT_DELAY = 5


def log(msg):
    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{now}] {msg}")


def to_float(value):
    try:
        return float(value)
    except:
        return None


# ===== 核心回调（以后这里写 MySQL） =====
def handle_new_value(entity_id, friendly_name, current, raw_state):
    """
    current: Optional[float]
    raw_state: 原始字符串状态
    """
    if current is None:
        log(f"{friendly_name} ({entity_id}) -> state = {raw_state}")
        return

    if current > CURRENT_LIMIT:
        log(f"!!! 超限 !!! {friendly_name} ({entity_id}) = {current} A (> {CURRENT_LIMIT} A)")
    else:
        log(f"{friendly_name} ({entity_id}) = {current} A")


# ===== WebSocket 主流程 =====
def connect_and_listen():
    ws_url = "ws://{}/api/websocket".format(HA_URL)
    log("Connecting to {} ...".format(ws_url))

    ws = websocket.create_connection(ws_url, timeout=10)

    # 1. 等 auth_required
    msg = ws.recv()
    data = json.loads(msg)
    if data.get("type") != "auth_required":
        raise RuntimeError("Unexpected first message: {}".format(data))

    # 2. 发 token
    ws.send(json.dumps({
        "type": "auth",
        "access_token": HA_TOKEN
    }))

    # 3. 看是否认证成功
    msg = ws.recv()
    data = json.loads(msg)
    if data.get("type") != "auth_ok":
        raise RuntimeError("Auth failed: {}".format(data))

    log("Auth OK.")

    # 4. 订阅 state_changed
    ws.send(json.dumps({
        "id": 1,
        "type": "subscribe_events",
        "event_type": "state_changed"
    }))

    log("Subscribed to state_changed.")

    # 5. 主循环
    while True:
        raw = ws.recv()
        event = json.loads(raw)

        if event.get("type") != "event":
            continue

        inner = event.get("event", {})
        data = inner.get("data", {})
        entity_id = data.get("entity_id")

        if entity_id not in MONITORED_SENSORS:
            continue

        new_state = data.get("new_state") or {}
        state_str = new_state.get("state", "")
        attrs = new_state.get("attributes", {})
        friendly_name = attrs.get("friendly_name", entity_id)

        current = to_float(state_str)
        handle_new_value(entity_id, friendly_name, current, state_str)


def main():
    while True:
        try:
            connect_and_listen()
        except KeyboardInterrupt:
            log("用户退出")
            break
        except Exception as e:
            log("错误: {}".format(e))
            log("将在 {} 秒后重连...".format(RECONNECT_DELAY))
            time.sleep(RECONNECT_DELAY)


if __name__ == "__main__":
    main()
