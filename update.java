import etc. *;
import etc.deprecated. *;

import java.io. *;
import java.net. *;
import java.util. *;
import javax.swing. *;

public class update {
    public static String version;
    public static String update;
    public static FileWriter fw;
    public static Scanner sc;
    public static void main(String[] args)throws IOException {
        File fi = new File("C:\\FreeRam\\config\\version.ini");
        if (!fi.exists() || fi.length() == 0) {
            fi.createNewFile();
            fw = new FileWriter(fi);
            fw.write("2.4");
            fw.close();
            new startup();
        } else {
            sc = new Scanner(fi);
            version = sc.nextLine();
            sc.close();
            if (!version.equals("2.4")) {
                fw = new FileWriter(fi);
                fw.write("2.4");
                fw.close();
            }
            URL url = new URL("https://shanuflash.github.io/freeram/version.ini");
            sc = new Scanner(url.openStream());
            update = sc.nextLine();
            sc.close();
            if (!version.equals(update)) {
                JOptionPane.showMessageDialog(
                    null,
                    "There is a new update available!",
                    "FreeRam",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }

        
    }
}
