package org.ums;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("theme.properties"));
            String theme = properties.getProperty("theme", "com.formdev.flatlaf.FlatIntelliJLaf");
            setLookAndFeel(theme);

        } catch (IOException | UnsupportedLookAndFeelException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "IOException", e);
        }

        SwingUtilities.invokeLater(() -> {
            logIn frame = new logIn();
            setMainFrameIcon(frame);
            frame.setVisible(true);
        });
    }

    public static void setLookAndFeel(String theme) throws UnsupportedLookAndFeelException {
        switch (theme) {
            case "com.formdev.flatlaf.FlatDarkLaf":
                UIManager.setLookAndFeel(new FlatDarkLaf());
                break;
            case "com.formdev.flatlaf.FlatIntelliJLaf":
            default:
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
                break;
        }
    }

    private static void setMainFrameIcon(JFrame frame) {
        try {
            Image icon = new ImageIcon(Main.class.getResource("/iconUMS.png")).getImage();

            frame.setIconImage(icon);
        } catch (Exception e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "Error setting icon", e);
        }
    }
}
