import java.net. *;
import java.nio.channels. *;
import java.io. *;
import java.util.zip. *;
import javax.swing. *;
import java.awt.event. *;
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

        //  Swing  UI
        //  Frame
        JFrame f = new JFrame("Free Ram");
        f.setSize(315, 200);
        f.setLayout(null);
        ImageIcon img = new ImageIcon("bin\\ram.png");
        f.setIconImage(img.getImage());
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        //  Label l1
        JLabel l1 = new JLabel("Change Timings: ");
        Font font1 = new Font("Roboto", Font.PLAIN, 20);
        l1.setFont(font1);
        //System.out.println("L1= " + l1.getPreferredSize());
        l1.setBounds(20,20, 152, 26);  
        f.add(l1); 

        //  TextField t1
        JTextField t1 = new JTextField(" 60");
        t1.setBounds(20, 55, 250, 35);
        Font font2 = new Font("Roboto", Font.PLAIN, 17);
        t1.setFont(font2);
        t1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String timestr = t1.getText();
                double time = Double.parseDouble(timestr);
                System.out.println(time);
            }
        });
        f.add(t1);

        //  Label l2
        JLabel l2 = new JLabel("Manual Clean:");
        //Font font2 = new Font("Roboto", Font.PLAIN, 20);
        //System.out.println("L2= " + l2.getPreferredSize());
        l2.setFont(font1);
        l2.setBounds(20,110, 125, 26);  
        f.add(l2);

        //  Button b1
        JButton b1 = new JButton("Click Here");
        b1.setBounds(150, 110, 100, 30);
        b1.setFocusPainted(false);
        b1.setContentAreaFilled(false);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Runtime
                        .getRuntime()
                        .exec("cmd /c C:\\FreeRam\\rammap.exe -ew", null);
                } catch (IOException a) {
                    a.printStackTrace();
                }
            }
        });
        f.add(b1);

        
        //  System  Tray
        if (!SystemTray.isSupported()) {
            System
                .out
                .println("System tray is not supported !!! ");
            return;
        }
        SystemTray systemTray = SystemTray.getSystemTray();
        Image image = Toolkit
            .getDefaultToolkit()
            .getImage("bin/ram.png");
        PopupMenu trayPopupMenu = new PopupMenu();
        MenuItem action = new MenuItem("Show");
        action.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                f.setVisible(true);
            }
        });
        trayPopupMenu.add(action);
        MenuItem close = new MenuItem("Close");
        close.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayPopupMenu.add(close);
        TrayIcon trayIcon = new TrayIcon(image, "FreeRam", trayPopupMenu);
        trayIcon.setImageAutoSize(true);

        try {
            systemTray.add(trayIcon);
        } catch (AWTException awtException) {
            awtException.printStackTrace();
        }

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
