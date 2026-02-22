# FreeRam
**You can download free ram? (maybe)**

A lightweight tool that helps lower RAM usage when you need it the most. Rewritten from scratch in C# / WPF.

## Installation
1. Download `FreeRam.exe` from the [latest release](https://github.com/shanuflash/freeram/releases/latest)
2. Run it — that's it

> Requires [.NET 10 Desktop Runtime](https://dotnet.microsoft.com/en-us/download/dotnet/10.0) (Windows will prompt you to install it if missing)

### Upgrading from older versions
- v2.0+: Delete `C:\FreeRam`
- v1.x: Delete `C:\Program Files (x86)\FreeRam` and remove freeram.exe from `shell:startup`
- The new version is a standalone `.exe` — no installation or extraction needed

## How it works
- Iterates over all running processes and performs a **soft working set trim** using `SetProcessWorkingSetSizeEx`
- This tells the OS it's *allowed* to reclaim memory pages from each process, without forcing a hard flush to the pagefile — **SSD/HDD friendly**
- Smart filtering skips:
  - Your **active foreground window** (no stutter)
  - **Critical system processes** (`dwm`, `csrss`, `svchost`, `lsass`, etc.)
  - **Small processes** under 50 MB (not worth trimming)
- The engine monitors RAM usage and auto-triggers when it crosses your configured threshold

## Features
- Configurable **threshold**, **recovery**, and **cooldown** triggers
- **Engine ON/OFF toggle** — disable auto-clean without closing the app
- **Manual purge button** — clean on demand
- **System tray** — minimize to tray, runs silently in the background
- **Single-instance lock** — prevents duplicate instances
- **Async trimming** — runs off the UI thread, zero freezing

## How do I check if it works?
- Note down RAM usage before running FreeRam and after triggering a clean. You'll notice a visible decrease in RAM usage.

## Notes
- Requires **administrator privileges** to trim process memory
- Windows x64 only

## Screenshots
<img width="430" height="712" alt="image" src="https://github.com/user-attachments/assets/d3115072-fc4b-4423-9af7-3b84b5932e5c" />
