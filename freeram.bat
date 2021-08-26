cd %~dp0
move RAMMap.exe C:\Windows
@echo off
for /l %%i in (1, 1, 11) do (
	test!
	timeout /t 3600 /nobreak
)
