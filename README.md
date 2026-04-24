# 📦 Minecrement Utilities (Requires Minescript)
Minescript: https://modrinth.com/mod/minescript

A suite of **Minescript-powered automation scripts** for Minecraft 1.21.x.

Features include:

- Auto-slot cycling
- Automatic GG message trigger
- Unified controller system
- Background-safe threaded execution
- Pause/Resume hotkey support
- Crash recovery
- Full configuration support

---

# 📋 Prerequisites

| Requirement | Details |
|---|---|
| **Minecraft Java Edition** | `1.21.x` (Fabric or NeoForge) |
| **Minescript Mod** | Latest version matching your MC version & mod loader |
| **Python** | `3.10+` from [python.org](https://www.python.org/downloads/) (**NOT** Microsoft Store) |
| **File Explorer** | `View → Show → File name extensions` enabled |

---

# ⚙️ Installation

---

## 1️⃣ Install Minescript

1. Download the mod from [Modrinth](https://modrinth.com/mod/minescript) or [CurseForge](https://www.curseforge.com/)
2. Place the `.jar` inside:

```text
%appdata%\.minecraft\mods\
Launch Minecraft once
This creates the:
minescript/

folder automatically

2️⃣ Install Python Correctly
Download the official installer from:

https://www.python.org/downloads/

Run installer
IMPORTANT:

✅ CHECK:

Add Python to PATH

at the bottom of the installer

Click:
Install Now
Verify in Command Prompt:
python --version
⚠️ DO NOT use Microsoft Store Python

It runs inside a sandbox and causes:

ERROR 9009

with Minescript.

3️⃣ Configure Minescript

Open:

%appdata%\.minecraft\minescript\config.txt

Find the line:

python=

Replace it with:

python="python"
If scripts still fail:

Run this in CMD:

where python

Then paste the full path like:

python="C:\Path\To\python.exe"

inside config.txt

4️⃣ Disable Windows App Execution Aliases

Go to:

Settings → Apps → Advanced app settings → App execution aliases

Turn OFF:

python.exe
python3.exe
5️⃣ Fully Restart Minecraft

Minescript only reads config on launch.

You must fully close and reopen Minecraft.

📂 Install the Scripts

Save the 3 script files below inside:

%appdata%\.minecraft\minescript\

with these exact filenames:

mc.py
autoslot.py
autogg.py
Important
Ensure extensions are exactly:
.py

and NOT:

.py.txt
Final Verification
Fully restart Minecraft
Open chat
Run:
\ls

You should see all 3 scripts listed.

🎮 Usage & Commands
Unified Controller (\mc)
Command	Action
\mc enable	Launches autoslot + autogg
\mc disable	Stops both scripts cleanly
\mc reload	Stops → waits → restarts with latest edits
\mc status	Shows running state, job IDs, and status
In-Game Controls
🎵 J Key

Pause/Resume individual scripts

Requires Minecraft window focused

Additional Features
Scripts run in background threads
Survive world/dimension changes
Auto-recover from crashes
Full 1.21.x compatibility
Inline metadata output:
[ - HH:MM:SS | Toggle: J | Status: Active ]
⚙️ Configuration & Customization

All settings are located at the top of each script under:

# ================= CONFIGURATION =================
Setting	File	Description
TOGGLE_KEY = 74	autoslot.py, autogg.py	GLFW key code for pause/resume (default: J)
INTERVAL = 60.0	autoslot.py	Seconds between slot switches
TARGET_SLOT = 1	autoslot.py	Hotbar slot to lock to (0-indexed: 1 = second slot)
DEFAULT_RETRY_DELAY = 1.5	autoslot.py	Fallback cooldown retry time if server format changes
GG_RESPONSE = "gg"	autogg.py	Message sent when trigger is detected
GG_COOLDOWN = 3.0	autogg.py	Minimum seconds between auto-GG triggers
🔑 GLFW Key Reference

You can change:

TOGGLE_KEY

to any valid GLFW key code.

Common Key Codes
Key	Code	Key	Code
A - Z	65 - 90	0 - 9	48 - 57
J (default)	74	K	75
F1 - F12	290 - 301	ESC	256
ENTER	257	TAB	258
LEFT_SHIFT	340	LEFT_CONTROL	341
UP / DOWN	265 / 264	LEFT / RIGHT	263 / 262


---
```
# 📜 Script Files (Ready to Copy)

Save each block exactly as named inside:


%appdata%\.minecraft\minescript\
Mc.py
Autoslot.py
Autogg.py
Config.txt
The code is to be downloaded from the repo

📝 Notes & Compatibility

✅ Client-side only. No server mods or plugins required.

✅ Compatible with Minecraft 1.21.x, Minescript 4.0+, Python 3.10+

✅ All scripts run as independent background jobs. Safe to alt-tab or minimize.

✅ Built using official Minescript APIs. No reflection hacks or unstable thread overrides.

✅ Chat UI uses vanilla-safe JSON text. Fully compatible with modern chat security restrictions.
