If WScript.Arguments.length = 0 Then
   Set objShell = CreateObject("Shell.Application")
   objShell.ShellExecute "wscript.exe", Chr(34) & _
      WScript.ScriptFullName & Chr(34) & " uac", "", "runas", 1
Else
   Set WshShell = CreateObject("WScript.Shell") 
   WshShell.Run chr(34) & "C:\Users\shanu\Downloads\Tweaks\freeram.bat" & Chr(34), 0
   Set WshShell = Nothing
End If
