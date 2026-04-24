import minescript
import time
import queue
import json

# ================= CONFIGURATION =================
TOGGLE_KEY = 74  # GLFW_KEY_J
GG_RESPONSE = "gg"
GG_COOLDOWN = 3.0
HEARTBEAT_INTERVAL = 300.0
paused = False
last_gg = 0.0
last_heartbeat = time.monotonic()
# =================================================

def log_pretty(message, msg_type="warning", prefix="AutoGG", info_line=None):
    cfg = {
        "info":    ("[*]", ((0x3B, 0x82, 0xF6), (0x60, 0xA5, 0xFA))),
        "success": ("[+]", ((0x10, 0xB9, 0x81), (0x34, 0xD3, 0x99))),
        "warning": ("[!]", ((0xF5, 0x9E, 0x0B), (0xFB, 0xB4, 0x24))),
        "error":   ("[x]", ((0xEF, 0x44, 0x44), (0xF8, 0x71, 0x71)))
    }
    sym, (start_rgb, end_rgb) = cfg.get(msg_type, cfg["warning"])
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
            log_pretty("Running. Auto-replying to server GG broadcasts.", "info", info_line="Toggle: J Key │ Status: Active")

            while True:
                try:
                    event = eq.get(block=True, timeout=0.5)

                    if event.type == "key" and event.key == TOGGLE_KEY and event.action == 1:
                        paused = not paused
                        status = "PAUSED" if paused else "RESUMED"
                        log_pretty(status, "warning" if paused else "success", info_line=f"Toggle: J Key │ Status: {status}")

                    elif event.type == "chat":
                        if paused: continue
                        msg = event.message.strip()
                        now = time.monotonic()
                        if "»" in msg or msg.startswith("<"): continue
                        msg_lower = msg.lower()
                        if ("gg wave has started!" in msg_lower or msg_lower.startswith("gg!")) and (now - last_gg > GG_COOLDOWN):
                            minescript.chat(GG_RESPONSE)
                            last_gg = now
                            log_pretty(f"Server broadcast detected. Sent '{GG_RESPONSE}'", "success", info_line="Action: Auto-reply triggered")

                    if time.monotonic() - last_heartbeat >= HEARTBEAT_INTERVAL:
                        minescript.log("[AutoGG] Heartbeat: script alive")
                        last_heartbeat = time.monotonic()

                except queue.Empty:
                    pass
                except Exception as inner_e:
                    minescript.log(f"[AutoGG] Inner loop error: {inner_e}")
                    time.sleep(1)
    except Exception as e:
        minescript.log(f"[AutoGG] Outer loop crashed, restarting in 3s: {e}")
        time.sleep(3)
        continue