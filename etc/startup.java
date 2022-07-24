package etc;

import java.io. *;
import java.net. *;

public class startup {
    public static String s;
    public static String Url = "https://raw.githubusercontent.com/shanuflash/freeram/java/freeram.xml";
    public static String Path = "C:\\FreeRam\\config\\freeram.xml";
    public static String Dir = "C:\\FreeRam\\config\\";
    public startup()throws MalformedURLException, IOException {
        new download(Url, Dir, Path);
        Runtime
            .getRuntime()
            .exec("SCHTASKS.exe /Delete /tn FreeRam /f");
        Runtime
            .getRuntime()
            .exec("SCHTASKS.exe /Create /tn FreeRam /XML C:/FreeRam/config/freeram.xml");
    }
}
