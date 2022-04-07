import java.net.*;
import java.nio.channels.*;
import java.io.*;

public class freeram 
{    public static void main(String[] args) throws IOException {

        URL fetchWebsite = new URL("https://download.sysinternals.com/files/RAMMap.zip");
        ReadableByteChannel readableByteChannel = Channels.newChannel(fetchWebsite.openStream());

        File zdir = new File("C:\\FreeRam");
        if(!zdir.exists()) zdir.mkdirs();

        try (FileOutputStream fos = new FileOutputStream("C:\\FreeRam\\RamMap.zip")) {
            fos.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
}
