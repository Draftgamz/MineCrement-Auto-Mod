📜 MineCrement Utilities – Setup & Usage Guide
A suite of Minescript-powered automation scripts for Minecraft 1.21.x. Includes auto-slot cycling, auto-GG chat responder, and a unified controller. Fully client-side, background-safe, crash-resistant, and optimized for modern Minecraft chat security.
📦 Prerequisites
Requirement
Details
Minecraft Java Edition
1.21.x (Fabric or NeoForge)
Minescript Mod
Latest version matching your MC version & mod loader
Python
3.10+ from python.org (NOT Microsoft Store)
File Explorer
Show file extensions enabled (View → Show → File name extensions)
🛠️ Installation
1️⃣ Install Minescript
Download the mod from Modrinth or CurseForge
Place the .jar file in %appdata%\.minecraft\mods\
Launch Minecraft once to generate the minescript/ folder
2️⃣ Install Python Correctly
Download the official installer from python.org
Run installer → ✅ CHECK Add Python to PATH at the bottom
Click Install Now
Open Command Prompt and verify: python --version
⚠️ Do NOT use the Microsoft Store Python. It runs in a sandbox and causes ERROR 9009 with Minescript.
3️⃣ Configure Minescript
Open %appdata%\.minecraft\minescript\config.txt
Find the python= line and replace it with:
txt
1
If scripts still fail to launch:
Run where python in CMD
Paste the full path into config.txt: python="C:\Path\To\python.exe"
Disable Windows App Execution Aliases:
Settings → Apps → Advanced app settings → App execution aliases
Turn OFF python.exe and python3.exe
Fully restart Minecraft (Minescript only reads config on launch)
4️⃣ Install the Scripts
Create/save these 3 files in %appdata%\.minecraft\minescript\:
mc.py (Unified controller)
autoslot.py (Hotbar cycler + smart cooldown retry)
autogg.py (Server broadcast auto-responder)
Ensure extensions are exactly .py (not .py.txt)
Fully restart Minecraft to index them
Verify installation: Open chat → \ls → all 3 should appear
🎮 Usage & Commands
Unified Controller (\mc)
Command
Action
\mc enable
Launches autoslot + autogg
\mc disable
Stops both scripts cleanly
\mc reload
Stops → waits → restarts with latest file edits
\mc status
Shows running state, job IDs, and status
In-Game Controls
J Key → Pause/Resume individual scripts (requires MC window focused)
Scripts run in background threads, survive world/dimension changes, and auto-recover from crashes
All chat output uses inline metadata (└─ · HH:MM:SS │ Toggle: J │ Status: Active) for 1.21.x compatibility
⚙️ How It Works
Feature
Implementation
Background-safe slot switching
Uses player_inventory_select_slot() instead of OS mouse input
Smart cooldown retry
Parses server messages with regex (for Xs) and retries precisely
Chat filtering
Ignores player chat (», <) and only triggers on server broadcasts
Crash recovery
Outer/inner try/except loops + register_world_listener() keep scripts alive
1.21.x chat security
No hover/click events. Uses vanilla-safe JSON text with inline metadata
🆘 Troubleshooting
Issue
Fix
PYTHON WAS NOT FOUND / ERROR 9009
Python not in PATH or Store stub interfering. Disable aliases & update config.txt
Cannot run program ... WindowsApps\python3.exe
config.txt still points to Store path. Replace with python="python" or absolute path
Scripts missing from \ls
Wrong folder, hidden .txt extension, or Minecraft not fully restarted
J key does nothing
Minecraft window must be focused. Use \suspend <ID> / \resume <ID> for unfocused control
Hover tooltips not showing
Minecraft 1.21.x strips client-side hover events for security. UI uses inline metadata instead
Cooldown retry fails
Server message format changed. Edit regex in autoslot.py chat handler: r"for\s+([\d.]+)\s*s"
📖 Quick Reference
bash
123456789101112131415
🌐 How to Host This on GitHub Pages
Create a new GitHub repository
Add this content as index.md or README.md
Go to Settings → Pages
Under Source, select Deploy from a branch → choose main → / (root)
Click Save. Your docs will be live at https://<username>.github.io/<repo-name>/ within 2 minutes
(Optional) Add a _config.yml with theme: jekyll-theme-cayman for a clean documentation look
📜 License & Notes
Client-side only. No server mods or plugins required.
Compatible with Minecraft 1.21.x, Minescript 4.0+, Python 3.10+
All scripts run as independent background jobs. Safe to alt-tab or minimize.
Built using official Minescript APIs. No reflection hacks or unstable thread overrides.
