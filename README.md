# FreeRam
**You can download free ram? (maybe)**

>The software is pretty much stale right now, but it should work as intended. Am planning on making a updated version in a different language (fuck java) sometime soon.

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
  - Download [JDK](https://download.oracle.com/java/22/latest/jdk-22_windows-x64_bin.exe) if you don't have it yet
  - Download [FreeRam.zip](https://github.com/shanuflash/freeram/releases/latest/download/freeram.zip)
  - Extract to `C:\FreeRam`
  - Run `freeram.exe`
- Folder structure after extraction: 
  - Make sure the following image matches your structure after extracting
 
![image](https://user-images.githubusercontent.com/39374797/230544481-9097f029-7f9e-474e-bf4b-e57d83861873.png)

## How it works.<br>
- Windows being the mess that it is uses a completely folly and obselete form of memory management. This program clears the working set memory every hour and frees up ram.
- Empty Working Sets removes memory from all user-mode and system working sets to the standby or modified page lists.<br>
- Note that by the time you refresh the data, processes that run any code will necessarily populate their working sets to do so.<br>
- Simply put it kinda is **FREE RAM**!

## How do I check if it works?
- Note down ram usage before running 'FreeRam' and after running it. You'll notice visible decrease in the RAM usage.

## Screenshots:
![image](https://user-images.githubusercontent.com/39374797/230544261-64023720-7b82-44a9-9e8d-41bd7b397575.png) ![image](https://user-images.githubusercontent.com/39374797/182328198-94e5a3f2-9bf0-4c68-a740-23ef5d9ba9b1.png) 

Thanks @Microsoft for their amazing tool RAMMap
