import websocket
import json

# ======== 配置 ========
HA_URL = "74.48.62.239:443"
HA_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiIwOWU2YmNjMWRmMTI0ZTUyODA2YTJhYTdiNjYzNDkzNyIsImlhdCI6MTc2NDkyMTkwMiwiZXhwIjoyMDgwMjgxOTAyfQ.DOoCIV6TWKNBgSHWNBOHREMLfcnFBQYhS2r0ud1qhT4"


ws = websocket.create_connection(f"ws://{HA_URL}/api/websocket")

# 等待 auth_required
ws.recv()

# 认证
ws.send(json.dumps({
    "type": "auth",
    "access_token": HA_TOKEN
}))
ws.recv()

# 请求所有状态
ws.send(json.dumps({
    "id": 1,
    "type": "get_states"
}))

resp = json.loads(ws.recv())

entities = resp["result"]

for e in entities:
    if e["entity_id"].startswith("sensor."):
        name = e["attributes"].get("friendly_name", e["entity_id"])
        print(e["entity_id"], "->", name)
