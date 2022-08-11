import etc.download;

import java.io. *;
import javax.swing. *;

public class updater {
    public static String Url = "https://github.com/shanuflash/freeram/releases/latest/download/update.exe";
    public static String Path = "C:/FreeRam/freeram.exe";
    public static String Dir = "C:/FreeRam/";
    public static void main(String[] args) throws IOException {
        Runtime
            .getRuntime()
            .exec("taskkill /F /IM freeram.exe");
        try {
            new download(Url, Dir, Path);
            JOptionPane.showMessageDialog(
                null,
                "Update successful!",
                "FreeRam",
                JOptionPane.INFORMATION_MESSAGE
            );
            Runtime
                .getRuntime()
                .exec("C:/FreeRam/freeram.exe");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Update failed, please contact developer!",
                "FreeRam",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
