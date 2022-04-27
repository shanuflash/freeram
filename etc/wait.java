package etc;
import java.lang.Thread;

public class wait {
    public void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread
                .currentThread()
                .interrupt();
        }
    }
}
