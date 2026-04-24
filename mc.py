import minescript
import sys
import time
import json

def log_pretty(message, msg_type="info", prefix="MC", info_line=None):
    cfg = {
        "info":    ("[*]", ((0x3B, 0x82, 0xF6), (0x60, 0xA5, 0xFA))),
        "success": ("[+]", ((0x10, 0xB9, 0x81), (0x34, 0xD3, 0x99))),
        "warning": ("[!]", ((0xF5, 0x9E, 0x0B), (0xFB, 0xB4, 0x24))),
        "error":   ("[x]", ((0xEF, 0x44, 0x44), (0xF8, 0x71, 0x71)))
    }
    sym, (start_rgb, end_rgb) = cfg.get(msg_type, cfg["info"])
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

def find_job(target_name):
    for job in minescript.job_info():
        cmd_str = " ".join(job.command).lower()
        if target_name.lower() in cmd_str:
            return job
    return None

def stop_if_running(target_name):
    job = find_job(target_name)
    if job:
        minescript.execute(f"\\killjob {job.job_id}")
        return True
    return False

# ================= ROUTING =================
cmd = sys.argv[1].lower() if len(sys.argv) > 1 else "help"
targets = ["autoslot", "autogg"]

if cmd in ("start", "enable"):
    log_pretty("Enabling utilities...", "info", info_line="Action: Launching scripts")
    for t in targets:
        if not find_job(t):
            minescript.execute(f"\\{t}")
            log_pretty(f"Enabled {t}", "success", info_line=f"Status: {t} launched")
        else:
            log_pretty(f"{t} already enabled", "warning", info_line=f"Status: {t} active")
    log_pretty("Done.", "info", info_line="Status: Complete")

elif cmd in ("stop", "disable"):
    log_pretty("Disabling utilities...", "info", info_line="Action: Terminating scripts")
    stopped = 0
    for t in targets:
        if stop_if_running(t):
            log_pretty(f"Disabled {t}", "warning", info_line=f"Status: {t} terminated")
            stopped += 1
        else:
            log_pretty(f"{t} was not running", "info", info_line=f"Status: {t} inactive")
    if stopped > 0:
        time.sleep(0.5)
    log_pretty("Done.", "info", info_line="Status: Complete")

elif cmd == "reload":
    log_pretty("Reloading utilities...", "info", info_line="Action: Stopping & restarting")
    for t in targets:
        stop_if_running(t)
    log_pretty("Waiting for thread cleanup...", "info", info_line="Action: Draining queues")
    time.sleep(1.0)
    log_pretty("Launching updated instances...", "info", info_line="Action: Starting fresh scripts")
    for t in targets:
        minescript.execute(f"\\{t}")
        log_pretty(f"Enabled {t}", "success", info_line=f"Status: {t} launched")
    log_pretty("Reload complete.", "success", info_line="Status: All scripts updated & running")

elif cmd == "status":
    log_pretty("Checking utility status...", "info", info_line="Action: Scanning jobs")
    for t in targets:
        job = find_job(t)
        if job:
            log_pretty(f"{t} is running", "success", info_line=f"Job ID: {job.job_id} │ Status: {job.status}")
        else:
            log_pretty(f"{t} is stopped", "warning", info_line="Status: Inactive")
    log_pretty("Status check complete.", "info", info_line="Action: Done")

else:
    log_pretty("Usage: \\mc [enable|disable|reload|status]", "warning", info_line="Action: Help")