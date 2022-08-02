package etc;

import java.io. *;

public class startup {
    public startup()throws IOException {
        Runtime
            .getRuntime()
            .exec("SCHTASKS.exe /Delete /tn FreeRam /f");
        Runtime
            .getRuntime()
            .exec("SCHTASKS.exe /Create /tn FreeRam /XML C:/FreeRam/bin/freeram.xml");
    }
}