import etc.download;

import java.io. *;
import javax.swing. *;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;

public class updater {
    public static String Url = "https://github.com/shanuflash/freeram/releases/latest/download/update.exe";
    public static String Path = "C:/FreeRam/freeram.exe";
    public static String Dir = "C:/FreeRam/";
    public static void main(String[] args)throws IOException {
        //  LAF
        try {
            UIManager.setLookAndFeel(new FlatCarbonIJTheme());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JFrame update = new JFrame("FreeRam");
        update.setAlwaysOnTop(true);

        Runtime
            .getRuntime()
            .exec("taskkill /F /FI \"WindowTitle eq Free Ram\" /T");

        try {
            new download(Url, Dir, Path);
            JOptionPane.showMessageDialog(
                update,
                "Update successful!",
                "FreeRam",
                JOptionPane.INFORMATION_MESSAGE
            );
            Runtime
                .getRuntime()
                .exec("C:/FreeRam/freeram.exe");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                update,
                "Update failed, please contact developer!",
                "FreeRam",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
