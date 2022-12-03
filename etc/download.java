package etc;

import java.io.*;
import java.net.*;
import java.nio.channels.*;

public class download {
    public download(String Url, String Dir, String Path) throws MalformedURLException, IOException {
        File zdir = new File(Dir);
        if (!zdir.exists())
            zdir.mkdirs();
        URL fetchWebsite = new URL(Url);
        ReadableByteChannel readableByteChannel = Channels.newChannel(
                fetchWebsite.openStream());
        try (FileOutputStream fos = new FileOutputStream(Path)) {
            fos
                    .getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException a) {
            a.printStackTrace();
        }
    }
}
