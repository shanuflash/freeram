cd %~dp0
move RAMMap.exe C:\Windows
@echo off
for %i in (1, 1, 12) do (
	test!
	timeout /t 3600 /nobreak
)
