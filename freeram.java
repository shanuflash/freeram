import etc.*;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;

public class freeram {
    public static boolean changed = false;
    public static boolean pause = false;
    public static String timestring;
    public static String version;
    public static String update;
    public static int time;
    public static int min = 0;
    public static int sec = 60;
    public static FileWriter fw;
    public static Scanner sc;

    public static void main(String[] args) throws IOException {
        // LAF
        try {
            UIManager.setLookAndFeel(new FlatCarbonIJTheme());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // configs
        File fi = new File("C:/FreeRam/config/");
        if (!fi.exists()) {
            fi.mkdirs();
        }

        fi = new File("C:/FreeRam/config/version.ini");
        if (!fi.exists() || fi.length() == 0) {
            fi.createNewFile();
            fw = new FileWriter(fi);
            fw.write("2.5");
            fw.close();
            new startup();
        } else {
            sc = new Scanner(fi);
            version = sc.nextLine();
            sc.close();
            if (!version.equals("2.5")) {
                fw = new FileWriter(fi);
                fw.write("2.5");
                fw.close();
                version = "2.5";
            }
            URL url = new URL("https://shanuflash.github.io/freeram/version.ini");
            try {
                URLConnection connection = url.openConnection();
                connection.connect();
                sc = new Scanner(url.openStream());
                update = sc.nextLine();
                sc.close();
                if (!version.equals(update)) {
                    JOptionPane.showMessageDialog(
                            null,
                            "There is a new update available!",
                            "FreeRam",
                            JOptionPane.INFORMATION_MESSAGE);
                    Runtime
                            .getRuntime()
                            .exec("C:/FreeRam/updater.exe");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "No internet connection, Failed to check for updates!",
                        "FreeRam",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        fi = new File("C:/FreeRam/config/freeram.ini");
        if (!fi.exists()) {
            fi.createNewFile();
            fw = new FileWriter(fi);
            fw.write("60");
            fw.close();
        }
        sc = new Scanner(fi);
        timestring = sc.nextLine();
        sc.close();

        // Frame
        System.setProperty("sun.java2d.uiScale", "1.0");
        JFrame f = new JFrame("Free Ram");
        f.setResizable(false);
        f.setLocationByPlatform(true);
        f.setSize(315, 220);
        f.setLayout(null);
        ImageIcon img = new ImageIcon("C:/FreeRam/src/ram.png");
        f.setIconImage(img.getImage());
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        // Label
        JLabel l1 = new JLabel("Change Timings: ");
        l1.setFont(new Font("Roboto", Font.PLAIN, 20));
        l1.setBounds(20, 10, 315, 26);

        JLabel l2 = new JLabel("Manual Clean:");
        l2.setFont(new Font("Roboto", Font.PLAIN, 20));
        l2.setBounds(20, 115, 315, 26);

        JLabel l3 = new JLabel("⚠️ Press the close button to minimize to tray");
        l3.setFont(new Font("Roboto", Font.PLAIN, 12));
        l3.setBounds(20, 145, 315, 26);

        JLabel l4 = new JLabel("Next clean in: " + timestring + " mins");
        l4.setFont(new Font("Roboto", Font.PLAIN, 11));
        l4.setBounds(150, 82, 315, 30);

        // TextField
        JTextField t1 = new JTextField(timestring);
        t1.setBounds(20, 45, 260, 35);
        t1.setFont(new Font("Roboto", Font.PLAIN, 17));

        // CheckBox
        JCheckBox c1 = new JCheckBox("Pause auto clean   |", pause);
        c1.setBounds(20, 82, 315, 30);

        // Button
        JButton b1 = new JButton("Click Here");
        b1.putClientProperty("JButton.buttonType", "roundRect");
        b1.setBounds(150, 115, 100, 30);

        // JFrame events
        t1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String jtime = t1.getText();
                boolean number = true,
                        valid = true;

                try {
                    time = Integer.valueOf(jtime);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(f, "Please enter only numeric values!");
                    number = false;
                }

                if (!((time > 0) && (time <= 720)) && number == true) {
                    JOptionPane.showMessageDialog(f, "Time has to be > 0 and < 720!");
                    valid = false;
                }

                try {
                    if (!timestring.equals(jtime) && valid == true && number == true) {
                        fw = new FileWriter("C:/FreeRam/config/freeram.ini");
                        fw.write(jtime);
                        fw.close();
                        timestring = jtime;
                        changed = true;
                    }
                } catch (IOException f) {
                    f.printStackTrace();
                }
            }
        });

        c1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pause = c1.isSelected();
            }
        });

        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Runtime
                            .getRuntime()
                            .exec("C:/FreeRam/bin/rammap.exe -ew");
                } catch (IOException a) {
                    a.printStackTrace();
                }
            }
        });

        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                f.setVisible(false);
                JOptionPane.showMessageDialog(f, "Freeram has been minimized to tray!    ");
            }
        });

        // Add components
        f.add(l1);
        f.add(l2);
        f.add(l3);
        f.add(l4);
        f.add(t1);
        f.add(c1);
        f.add(b1);
        f.repaint();

        // System Tray
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
        TrayIcon trayIcon = new TrayIcon(image, "FreeRam");
        JPopupMenu popup = new JPopupMenu();
        trayIcon.setImageAutoSize(true);
        try {
            systemTray.add(trayIcon);
        } catch (AWTException awtException) {
            awtException.printStackTrace();
        }

        // Menu items
        JMenuItem show = new JMenuItem("Show");
        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.setVisible(true);
                f.repaint();
            }
        });
        popup.add(show);

        JMenuItem clean = new JMenuItem("Clean");
        clean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Runtime
                            .getRuntime()
                            .exec("C:/FreeRam/bin/rammap.exe -ew");
                } catch (IOException c) {
                    c.printStackTrace();
                }
            }
        });
        popup.add(clean);

        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        popup.add(close);

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

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popup.setLocation(e.getX() - 80, e.getY() - 50);
                    popup.setInvoker(popup);
                    popup.setVisible(true);
                }
                Timer t = new Timer(2500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        popup.setVisible(false);
                    }
                });
                t.setRepeats(false);
                t.start();
            }
        });

        // RAMMAP exec
        Runtime
                .getRuntime()
                .exec("C:/FreeRam/bin/rammap.exe -ew");
        try {
            for (;;) { // infinite
                sc = new Scanner(fi);
                timestring = sc.nextLine();
                sc.close();

                int time = Integer.valueOf(timestring);
                min = time;
                time = time * 60000;
                int divtime = time / 1000;
                int flag = 0;

                while (changed == false) {
                    new wait(1000);
                    if (flag % 60 == 0) {
                        min--;
                        sec = 59;
                    } else {
                        sec--;
                    }
                    l4.setText("Next clean in: " + min + "m " + sec + "s");

                    flag++;
                    if (changed == true) {
                        break;
                    }
                    if (flag == divtime) {
                        break;
                    }
                    while (pause == true) {
                        System.out
                                .print("");
                        if (pause == false) {
                            break;
                        }
                    }
                }

                Runtime
                        .getRuntime()
                        .exec("C:/FreeRam/bin/rammap.exe -ew");
                changed = false;
            }
        } catch (IOException c) {
            c.printStackTrace();
        }
    }
}
