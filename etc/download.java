package etc;
import java.io. *;
import java.net. *;
import java.nio.channels. *;

public class download {
    public void download()throws MalformedURLException, IOException {
        File zdir = new File("C:\\FreeRam");
        if (!zdir.exists()) 
            zdir.mkdirs();
        System
            .out
            .println("Downloading prerequisites...");

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
        } catch (IOException a) {
            a.printStackTrace();
        }
    }
}
