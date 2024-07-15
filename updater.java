import java.io.*;
import java.net.URISyntaxException;
import javax.swing.*;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;
import java.awt.HeadlessException;

import java.net.*;
import java.nio.channels.*;

public class updater {
    public static String Url = "https://github.com/shanuflash/freeram/releases/latest/download/update.exe";
    public static String Path = "C:/FreeRam/freeram.exe";
    public static String Dir = "C:/FreeRam/";

    public static void download(String Url, String Dir, String Path)
            throws MalformedURLException, IOException, URISyntaxException {
        File zdir = new File(Dir);
        if (!zdir.exists())
            zdir.mkdirs();
        URI uri = new URI(Url);
        URL fetchWebsite = uri.toURL();
        ReadableByteChannel readableByteChannel = Channels.newChannel(
                fetchWebsite.openStream());
        try (FileOutputStream fos = new FileOutputStream(Path)) {
            fos
                    .getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException a) {
        }
    }

    public static void main(String[] args) throws IOException {
        // LAF
        try {
            UIManager.setLookAndFeel(new FlatCarbonIJTheme());
        } catch (UnsupportedLookAndFeelException ex) {
        }
        JFrame update = new JFrame("FreeRam");
        update.setAlwaysOnTop(true);

        new ProcessBuilder("taskkill", "/F", "/FI", "WindowTitle eq Free Ram", "/T").start();

        try {
            download(Url, Dir, Path);
            JOptionPane.showMessageDialog(
                    update,
                    "Update successful!",
                    "FreeRam",
                    JOptionPane.INFORMATION_MESSAGE);
            new ProcessBuilder("C:/FreeRam/freeram.exe").start();
        } catch (HeadlessException | IOException | URISyntaxException e) {
            JOptionPane.showMessageDialog(
                    update,
                    "Update failed, please contact developer!",
                    "FreeRam",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
