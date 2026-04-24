import minescript
import time
import queue
import json
import re

# ================= CONFIGURATION =================
INTERVAL = 60.0
TOGGLE_KEY = 74  # GLFW_KEY_J
TARGET_SLOT = 1  # 2nd slot (0-indexed)
HEARTBEAT_INTERVAL = 300.0
DEFAULT_RETRY_DELAY = 1.5
paused = False
last_scroll = time.monotonic()
last_heartbeat = time.monotonic()
retry_pending = False
retry_start_time = 0.0
retry_delay = DEFAULT_RETRY_DELAY
# =================================================

def log_pretty(message, msg_type="success", prefix="AutoSlot", info_line=None):
    cfg = {
        "info":    ("[*]", ((0x3B, 0x82, 0xF6), (0x60, 0xA5, 0xFA))),
        "success": ("[+]", ((0x10, 0xB9, 0x81), (0x34, 0xD3, 0x99))),
        "warning": ("[!]", ((0xF5, 0x9E, 0x0B), (0xFB, 0xB4, 0x24))),
        "error":   ("[x]", ((0xEF, 0x44, 0x44), (0xF8, 0x71, 0x71)))
    }
    sym, (start_rgb, end_rgb) = cfg.get(msg_type, cfg["success"])
    def ease(t): return t * t * (3 - 2 * t)

    ts = time.strftime("%H:%M:%S")
    prefix_text = f"[{prefix}] "
    full_text = prefix_text + message
    prefix_len = len(prefix_text)
    components = [{"text": f"{sym} ", "color": "#AAAAAA"}]

    for i, char in enumerate(full_text):
        t = ease(i / max(len(full_text) - 1, 1))
        r = int(start_rgb[0] + (end_rgb[0] - start_rgb[0]) * t)
        g = int(start_rgb[1] + (end_rgb[1] - start_rgb[1]) * t)
        b = int(start_rgb[2] + (end_rgb[2] - start_rgb[2]) * t)
        is_prefix = i < prefix_len
        components.append({"text": char, "color": f"#{r:02X}{g:02X}{b:02X}", "bold": is_prefix})

    minescript.echo_json(json.dumps(components, separators=(',', ':')))
    
    if info_line:
        info_comp = [
            {"text": "  └─ · ", "color": "#3A3A3A"},
            {"text": f"{ts} ", "color": "#555555", "italic": True},
            {"text": "│ ", "color": "#3A3A3A"},
            {"text": info_line, "color": "#666666", "italic": True}
        ]
        minescript.echo_json(json.dumps(info_comp, separators=(',', ':')))

log_pretty("Initializing...", "info")

while True:
    try:
        with minescript.EventQueue() as eq:
            eq.register_key_listener()
            eq.register_chat_listener()
            eq.register_world_listener()
            log_pretty(f"Running. Selecting slot {TARGET_SLOT + 1} every {INTERVAL}s.", "info", info_line="Toggle: J Key │ Status: Active")

            while True:
                try:
                    event = eq.get(block=True, timeout=0.5)

                    if event.type == "key" and event.key == TOGGLE_KEY and event.action == 1:
                        paused = not paused
                        status = "PAUSED" if paused else "RESUMED"
                        log_pretty(status, "warning" if paused else "success", info_line=f"Toggle: J Key │ Status: {status}")
                        if not paused:
                            last_scroll = time.monotonic()
                            retry_pending = False

                    elif event.type == "chat":
                        msg_lower = event.message.lower()
                        if "cooldown" in msg_lower and "ability" in msg_lower:
                            if not retry_pending and not paused:
                                match = re.search(r"for\s+([\d.]+)\s*s", msg_lower)
                                retry_delay = float(match.group(1)) if match else DEFAULT_RETRY_DELAY
                                retry_pending = True
                                retry_start_time = time.monotonic()
                                log_pretty(f"Server cooldown detected ({retry_delay}s). Retrying...", "warning", info_line="Action: Cooldown retry pending")

                    if retry_pending and not paused and (time.monotonic() - retry_start_time >= retry_delay):
                        minescript.player_inventory_select_slot(TARGET_SLOT)
                        last_scroll = time.monotonic()
                        retry_pending = False
                        retry_delay = DEFAULT_RETRY_DELAY
                        log_pretty(f"Retry successful. Switched to slot {TARGET_SLOT + 1}", "success", info_line="Action: Slot cycle (retry)")

                    if not paused and not retry_pending and (time.monotonic() - last_scroll >= INTERVAL):
                        minescript.player_inventory_select_slot(TARGET_SLOT)
                        last_scroll = time.monotonic()
                        log_pretty(f"Switched to slot {TARGET_SLOT + 1}", "success", info_line="Action: Slot cycle")

                    if time.monotonic() - last_heartbeat >= HEARTBEAT_INTERVAL:
                        minescript.log("[AutoSlot] Heartbeat: script alive")
                        last_heartbeat = time.monotonic()

                except queue.Empty:
                    pass
                except Exception as inner_e:
                    minescript.log(f"[AutoSlot] Inner loop error: {inner_e}")
                    time.sleep(1)
    except Exception as e:
        minescript.log(f"[AutoSlot] Outer loop crashed, restarting in 3s: {e}")
        time.sleep(3)
        continue