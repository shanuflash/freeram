import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;
import java.util.concurrent.*;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;

public class freeram {
    private static final String CONFIG_PATH = "C:/FreeRam/config/";
    private static final String VERSION_FILE = CONFIG_PATH + "version.ini";
    private static final String FREERAM_INI_FILE = CONFIG_PATH + "freeram.ini";
    private static final String UPDATER_PATH = "C:/FreeRam/updater.exe";
    private static final String VERSION_URL = "https://shanuflash.github.io/freeram/version.ini";
    private static final String CURRENT_VERSION = "2.5";

    private static JFrame f;
    private static JTextField t1;
    private static JLabel l1, l2, l3, l4;
    private static JButton b1, b2;
    private static JCheckBox c1;
    private static String timestring = "";
    private static boolean pause = false;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> scheduledTask;
    static ProcessBuilder pb = new ProcessBuilder(
            "powershell.exe",
            "-Command",
            "Start-Process",
            "C:/FreeRam/bin/rammap.exe",
            "-ArgumentList '-ew'",
            "-Verb RunAs");
    // static ProcessBuilder pb = new ProcessBuilder("C:/FreeRam/bin/rammap.exe",
    // "-ew");

    private static void initialSetup() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        try {
            processBuilder.command("reg.exe", "ADD", "HKCU\\Software\\Sysinternals\\RamMap", "/v", "EulaAccepted",
                    "/t", "REG_DWORD", "/d", "1", "/f").start();

            processBuilder.command("reg.exe", "ADD", "HKCU\\Software\\Sysinternals\\RamMap", "/v", "OriginalPath",
                    "/t", "REG_SZ", "/d", "C:\\FreeRam\\bin\\RAMMap.exe", "/f").start();

            processBuilder.command("SCHTASKS.exe", "/Delete", "/tn", "FreeRam", "/f").start();

            processBuilder.command("SCHTASKS.exe", "/Create", "/tn", "FreeRam", "/XML",
                    "C:/FreeRam/bin/freeram.xml").start();
        } catch (IOException e) {
        }
    }

    private static void scheduleClean() {
        int minutes = 60;
        if (!timestring.isEmpty()) {
            try {
                minutes = Integer.parseInt(timestring);
            } catch (NumberFormatException e) {
            }
        }

        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
        scheduledTask = scheduler.schedule(() -> {
            try {
                pb.start();
                scheduleClean();
            } catch (IOException e) {
            }
        }, minutes, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws IOException {
        // LAF

        try {
            UIManager.setLookAndFeel(new FlatCarbonIJTheme());
        } catch (UnsupportedLookAndFeelException ex) {
        }

        createDirectory(CONFIG_PATH);
        handleVersionFile();
        handleFreeRamIniFile();

        SwingUtilities.invokeLater(() -> initializeGUI());

        initializeSystemTray();

        // RAMMAP exec
        pb.start();
        scheduleClean();

    }

    private static void initializeGUI() {
        ImageIcon img = new ImageIcon("C:/FreeRam/src/ram.png");
        f = new JFrame("Free Ram");

        // FRAME PROPERTIES
        f.setResizable(false);
        f.setLocationByPlatform(true);
        f.setSize(315, 220);
        f.setLayout(null);
        f.setIconImage(img.getImage());
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        // FRAME COMPONENTS
        l1 = new JLabel("Change Timings: ");
        l2 = new JLabel("Manual Clean:");
        l3 = new JLabel("⚠️ Press the close button to minimize to tray");
        // l4 = new JLabel("Next clean in: " + timestring + " mins");
        t1 = new JTextField(timestring);
        b1 = new JButton("Click Here");
        b2 = new JButton("Save");
        // c1 = new JCheckBox("Pause auto clean |", pause);

        l1.setFont(new Font("Roboto", Font.PLAIN, 20));
        l2.setFont(new Font("Roboto", Font.PLAIN, 20));
        l3.setFont(new Font("Roboto", Font.PLAIN, 12));
        // l4.setFont(new Font("Roboto", Font.PLAIN, 11));
        t1.setFont(new Font("Roboto", Font.PLAIN, 17));

        l1.setBounds(20, 10, 315, 26);
        l2.setBounds(20, 115, 315, 26);
        l3.setBounds(20, 145, 315, 26);
        // l4.setBounds(150, 82, 315, 30);
        t1.setBounds(20, 45, 200, 35);
        b1.setBounds(150, 115, 100, 30);
        b2.setBounds(220, 45, 60, 35);
        // c1.setBounds(20, 82, 315, 30);

        // FRAME EVENTS
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                f.setVisible(false);
                JOptionPane.showMessageDialog(f, "Freeram has been minimized to tray!    ");
            }
        });

        b1.addActionListener((ActionEvent e) -> {
            try {
                pb.start();
            } catch (IOException e1) {
            }
        });

        b2.addActionListener((var e) -> {
            String jtime = t1.getText();
            int time = 0;
            boolean number = true,
                    valid = true;
            try {
                time = Integer.parseInt(jtime);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(f, "Please enter only numeric values!");
                number = false;
            }
            if (!((time > 0) && (time <= 720)) && number == true) {
                JOptionPane.showMessageDialog(f, "Time has to be > 0 and < 720!");
                valid = false;
            }
            try (FileWriter fw = new FileWriter(FREERAM_INI_FILE)) {
                if (!timestring.equals(jtime) && valid == true && number == true) {
                    fw.write(jtime);
                    timestring = jtime;
                    scheduleClean();
                }
            } catch (IOException f1) {
            }
        });

        // c1.addActionListener((ActionEvent e) -> {
        // pause = c1.isSelected();
        // });

        f.add(l1);
        f.add(l2);
        f.add(l3);
        // f.add(l4);
        f.add(t1);
        // f.add(c1);
        f.add(b1);
        f.add(b2);
        f.repaint();
    }

    private static void initializeSystemTray() {
        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(
                    f,
                    "System tray not supported, please report to developer.");
        }
        SystemTray systemTray = SystemTray.getSystemTray();

        // Tray icon
        Image image = Toolkit
                .getDefaultToolkit()
                .getImage("C:/FreeRam/src/ram.png");
        PopupMenu trayPopupMenu = new PopupMenu();
        TrayIcon trayIcon = new TrayIcon(image, "FreeRam", trayPopupMenu);

        trayIcon.setImageAutoSize(true);
        try {
            systemTray.add(trayIcon);
        } catch (AWTException awtException) {
        }

        // Menu items
        MenuItem show = new MenuItem("Show");
        show.addActionListener((ActionEvent e) -> {
            f.setVisible(true);
            f.repaint();
        });
        trayPopupMenu.add(show);

        MenuItem clean = new MenuItem("Clean");
        clean.addActionListener((ActionEvent e) -> {
            try {
                pb.start();
            } catch (IOException e1) {
            }
        });
        trayPopupMenu.add(clean);

        MenuItem close = new MenuItem("Close");
        close.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        trayPopupMenu.add(close);

        // Tray events
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getClickCount() >= 2) {
                        f.setVisible(true);
                        f.repaint();
                    }
                }
            }
        });
    }

    private static void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private static void handleVersionFile() {
        try {
            File versionFile = new File(VERSION_FILE);
            if (!versionFile.exists() || versionFile.length() == 0) {
                writeToFile(versionFile, CURRENT_VERSION);
                initialSetup();
            } else {
                String version = readFile(versionFile);
                if (!CURRENT_VERSION.equals(version)) {
                    writeToFile(versionFile, CURRENT_VERSION);
                }
                checkForUpdates(version);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error handling version file.", "FreeRam", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void checkForUpdates(String currentVersion) {
        try {
            // URL url = new URL(VERSION_URL);
            URI uri = new URI(VERSION_URL);
            URL url = uri.toURL();
            URLConnection connection = url.openConnection();
            connection.connect();
            try (Scanner sc = new Scanner(url.openStream())) {
                String update = sc.nextLine();
                if (!currentVersion.equals(update)) {
                    JOptionPane.showMessageDialog(null, "There is a new update available!", "FreeRam",
                            JOptionPane.INFORMATION_MESSAGE);
                    new ProcessBuilder(UPDATER_PATH).start();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No internet connection, Failed to check for updates!", "FreeRam",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (URISyntaxException ex) {
        }
    }

    private static void handleFreeRamIniFile() {
        try {
            File iniFile = new File(FREERAM_INI_FILE);
            if (!iniFile.exists()) {
                writeToFile(iniFile, "60");
            }
            timestring = readFile(iniFile);
            // Use timestring as needed
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error handling FreeRam INI file.", "FreeRam",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void writeToFile(File file, String content) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
    }

    private static String readFile(File file) throws IOException {
        try (Scanner sc = new Scanner(file)) {
            return sc.nextLine();
        }
    }

}
