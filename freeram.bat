move data\freeram.vbs "%USERPROFILE%\Start Menu\Programs\Startup"
move data\freeram.exe "%USERPROFILE%\Start Menu\Programs\Startup"
move RAMMap.exe C:\Windows
@echo off
for /l %%i in (1, 1, 11) do (
	rammap -ew
	timeout /t 3600 /nobreak
)
