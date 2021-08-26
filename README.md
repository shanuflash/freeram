# FreeRam
**You can download free ram? (maybe)**
# **JAVA REWRITE WIP**

Designed and coded by @shanuflash<br>
Thanks @Microsoft for their amazing RAMMap.exe

## How to run?<br>
Extract the .rar file and run freeram.exe as an admin, it'll create 
a startup profile and do its thing, silently in the background. 

## How it works.<br>
- Windows being the mess that it is uses a completely folly and obselete form of memory management. This program clears the working set memory every hour and frees up ram.
- Empty Working Sets removes memory from all user-mode and system working sets to the standby or modified page lists.<br>
- Note that by the time you refresh the data, processes that run any code will necessarily populate their working sets to do so.<br>
- Simply put it kinda is **FREE RAM**!

## How do I check if it works?
- Note down ram usage before using 'FreeRam' and then run the program. You'll notice visible
decrease in the usage.
