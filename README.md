# FreeRam
**You can download free ram? (maybe)**

A lightweight tool that helps lower RAM usage when you need it the most. Rewritten from scratch in C# / WPF. Automatically frees up RAM by trimming background processes. Your active window (such as the game you're playing or the video editor) is always left alone.

Built for systems with less than 16GB RAM where memory gets eaten up by apps running in the background.

## Installation
Download `FreeRam.exe` from the [latest release](https://github.com/shanuflash/freeram/releases/latest) and run it.

> Requires [.NET 10 Desktop Runtime](https://dotnet.microsoft.com/en-us/download/dotnet/10.0)

## How it works
- Performs soft memory trim on background processes (SSD friendly)
- Skips your active window, system processes, and small apps
- Monitors RAM every 2 seconds, auto-cleans when threshold is hit

## Features
- Configurable threshold, recovery, and cooldown
- Engine toggle and manual purge
- Runs in system tray
- Single 187 KB executable

## Notes
- Requires admin privileges
- Windows x64 only

## Screenshots
<img width="430" height="712" alt="image" src="https://github.com/user-attachments/assets/d3115072-fc4b-4423-9af7-3b84b5932e5c" />
