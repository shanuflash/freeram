package etc;

import java.io. *;

public class startup {
    public startup()throws IOException {
        Runtime
            .getRuntime()
            .exec(
                "reg.exe ADD HKCU\\Software\\Sysinternals\\RamMap /v EulaAccepted /t REG_DWORD " +
                        "/d 1 /f",
                null
            );
        Runtime
            .getRuntime()
            .exec(
                "reg.exe ADD HKCU\\Software\\Sysinternals\\RamMap /v OriginalPath /t REG_SZ /d " +
                        "C:\\FreeRam\\bin\\RAMMap.exe /f",
                null
            );
        Runtime
            .getRuntime()
            .exec("SCHTASKS.exe /Delete /tn FreeRam /f");
        Runtime
            .getRuntime()
            .exec("SCHTASKS.exe /Create /tn FreeRam /XML C:/FreeRam/bin/freeram.xml");
    }
}