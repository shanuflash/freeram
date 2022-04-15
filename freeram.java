import java.net. *;
import java.nio.channels. *;
import java.io. *;
import java.util.zip. *;
import javax.swing. *;
import java.awt.event. *;
import java.awt.SystemTray;
import java.awt. *;
import java.lang.Thread;

public class freeram {
    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread
                .currentThread()
                .interrupt();
        }
    }

    private static void unzip(String Path, String Dir) {
        File dir = new File(Dir);
        if (!dir.exists()) 
            dir.mkdirs();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(Path);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(Dir + File.separator + fileName);
                System
                    .out
                    .println("Unzipping to " + newFile.getAbsolutePath());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args)throws IOException {

        File zdir = new File("C:\\FreeRam");
        if (!zdir.exists()) 
            zdir.mkdirs();
        System
            .out
            .println("Downloading prerequisites...");

        /*URL fetchWebsite = new URL("https://download.oracle.com/java/18/latest/jdk-18_windows-x64_bin.exe");
        ReadableByteChannel readableByteChannel = Channels.newChannel(fetchWebsite.openStream());
        try (FileOutputStream fos = new FileOutputStream("C:\\FreeRam\\jdk-18_windows-x64_bin.exe")) {
            fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        Process process = Runtime.getRuntime().exec("cmd /c jdk-18_windows-x64_bin.exe");
        */

        URL fetchWebsite = new URL(
            "https://download.sysinternals.com/files/RAMMap.zip"
        );
        ReadableByteChannel readableByteChannel = Channels.newChannel(
            fetchWebsite.openStream()
        );

        try(FileOutputStream fos = new FileOutputStream("C:\\FreeRam\\RamMap.zip")) {
            fos
                .getChannel()
                .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        String Path = "C:\\FreeRam\\RamMap.zip";
        String Dir = "C:\\FreeRam";
        unzip(Path, Dir);

        //  RAMMAP  exec
        Runtime
            .getRuntime()
            .exec("cmd /c C:\\FreeRam\\rammap.exe -ew", null);
        try {
            for (int i = 0; i < 50; i++) {
                wait(3600000);
                Runtime
                    .getRuntime()
                    .exec("cmd /c C:\\FreeRam\\rammap.exe -ew", null);
            }
        } catch (IOException c) {
            c.printStackTrace();
        }

    }
}
