# FreeRam
**You can download free ram? (maybe)**

Designed and developed by @shanuflash, This is a tool that helps in lowering RAM usage when you need it the most.

## Instructions for installation:
- If you are using a older version of FreeRam
  - 2.0 and above:
    - Delete `C:\FreeRam`
  - 1.0 and above (Till April 15 2022)
    - Delete `C:\Program Files (x86)\FreeRam`
    - Delete freeram.exe from `shell:startup` [press Win+R and type]
  - Do the above steps before installing the newer version [These steps will be automated in future release]
- If you are using FreeRam for the first-time or a new PC
  - Download [JDK](https://download.oracle.com/java/18/latest/jdk-18_windows-x64_bin.exe) if you don't have it yet
  - Download [FreeRam.rar](https://github.com/shanuflash/freeram/releases/download/2.3/FreeRam.rar)
  - Extract to `C:\FreeRam`
  - Run `freeram.exe`
- Folder structure after extraction: 
  - Make sure the following image matches your structure after extracting
 
![image](https://user-images.githubusercontent.com/39374797/182327174-eb728e48-482f-4595-b198-292cc2f75437.png)

## How it works.<br>
- Windows being the mess that it is uses a completely folly and obselete form of memory management. This program clears the working set memory every hour and frees up ram.
- Empty Working Sets removes memory from all user-mode and system working sets to the standby or modified page lists.<br>
- Note that by the time you refresh the data, processes that run any code will necessarily populate their working sets to do so.<br>
- Simply put it kinda is **FREE RAM**!

## How do I check if it works?
- Note down ram usage before running 'FreeRam' and after running it. You'll notice visible decrease in the RAM usage.

## Screenshots:
![image](https://user-images.githubusercontent.com/39374797/182327897-18c2a72d-57cf-46bc-ad61-3a25fa82640c.png) ![image](https://user-images.githubusercontent.com/39374797/182328198-94e5a3f2-9bf0-4c68-a740-23ef5d9ba9b1.png) 

Thanks @Microsoft for their amazing tool RAMMap
