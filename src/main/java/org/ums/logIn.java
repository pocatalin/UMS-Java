package org.ums;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class logIn extends JFrame {
    public logIn() {
        initComponents();
        if (testDatabaseConnection()) {
            initializeLoginForm();

        } else {
            JOptionPane.showMessageDialog(null, "Error connecting to the database. Exiting.");
            System.exit(0);
        }
    }

    private void initializeLoginForm() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panelLogIn);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


        tbUser.addActionListener(e -> loginAction());

        pfPassword.addActionListener(e -> loginAction());

        btLogIn.addActionListener(e -> loginAction());

        optionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openNewWindow();
            }
        });
    }


    void loginAction() {
        String enteredUser = tbUser.getText();
        char[] enteredPassword = pfPassword.getPassword();
        String passwordString = new String(enteredPassword);

        if (!enteredUser.isEmpty() && enteredPassword.length > 0) {
            SwingWorker<Void, Void> loginWorker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    if (enteredUser.startsWith("P")) {
                        String professorId = enteredUser;
                        Professor professor = Professor.getProfessorById(professorId);

                        if (professor != null) {
                            String databasePassword = professor.password();

                            if (passwordString.equals(databasePassword)) {
                                openProfForm(professor.firstName(), professor.lastName(), professor.professorId());
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid password.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid professor ID.");
                        }
                    } else if (enteredUser.startsWith("A")) {
                        String adminID = enteredUser;
                        Admin admin = Admin.getAdminById(adminID);

                        if (admin != null) {
                            String databasePassword = admin.password();

                            if (passwordString.equals(databasePassword)) {
                                openAdminForm();
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid password.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid admin ID.");
                        }
                    } else {
                        try {
                            int studentId = Integer.parseInt(enteredUser);
                            String firstName = Student.getFirstNameById(studentId);

                            if (firstName != null) {
                                Student student = Student.getStudentById(studentId);
                                assert student != null;
                                String databasePassword = student.password();

                                if (passwordString.equals(databasePassword)) {
                                    openUserForm(firstName, studentId);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Invalid password.");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid student ID.");
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Invalid ID format.");
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            loginWorker.execute();
        } else {
            JOptionPane.showMessageDialog(null, "Please enter both ID and password.");
        }
    }

    private void openNewWindow() {
        JFrame newFrame = new JFrame("Theme Configuration");
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setSize(415, 175);
        newFrame.setLocationRelativeTo(null);

        JPanel themePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JRadioButton lightThemeButton = new JRadioButton("Light Theme");
        JRadioButton darkThemeButton = new JRadioButton("Dark Theme");
        JButton applyButton = new JButton("Apply");
        JButton changePasswordButton = new JButton("Change Password");

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightThemeButton);
        themeGroup.add(darkThemeButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        themePanel.add(lightThemeButton, gbc);

        gbc.gridy++;
        themePanel.add(darkThemeButton, gbc);

        gbc.gridy++;
        themePanel.add(applyButton, gbc);

        gbc.gridy++;
        themePanel.add(changePasswordButton, gbc);

        newFrame.add(themePanel);

        applyButton.addActionListener(e -> {
            if (lightThemeButton.isSelected()) {
                updateTheme("com.formdev.flatlaf.FlatIntelliJLaf");
            } else if (darkThemeButton.isSelected()) {
                updateTheme("com.formdev.flatlaf.FlatDarkLaf");
            }

            newFrame.dispose();
        });

        changePasswordButton.addActionListener(e -> openPasswordChangeForm());

        newFrame.setVisible(true);
    }


    private void openPasswordChangeForm() {
        JFrame passwordChangeFrame = new JFrame("Password Change");
        passwordChangeFrame.setSize(415, 175);
        JPanel passwordChangePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel userIdLabel = new JLabel("User ID:");
        JTextField userIdTextField = new JTextField();
        JLabel oldPasswordLabel = new JLabel("Old Password:");
        JPasswordField oldPasswordTextField = new JPasswordField();
        JLabel newPasswordLabel = new JLabel("New Password:");
        JPasswordField newPasswordTextField = new JPasswordField();

        gbc.gridy = 0;
        gbc.gridx = 0;
        passwordChangePanel.add(userIdLabel, gbc);

        gbc.gridy++;
        passwordChangePanel.add(oldPasswordLabel, gbc);

        gbc.gridy++;
        passwordChangePanel.add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordChangePanel.add(userIdTextField, gbc);

        gbc.gridy++;
        passwordChangePanel.add(oldPasswordTextField, gbc);

        gbc.gridy++;
        passwordChangePanel.add(newPasswordTextField, gbc);

        JButton submitButton = new JButton("Save");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        passwordChangePanel.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            String enteredUserId = userIdTextField.getText();
            String oldPassword = new String(oldPasswordTextField.getPassword());
            String newPassword = new String(newPasswordTextField.getPassword());

            if (enteredUserId.startsWith("P")) {
                if (validateOldPassword(enteredUserId, oldPassword)) {
                    updatePassword(enteredUserId, newPassword);
                    passwordChangeFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid old password for professor.");
                }
            } else {
                if (validateOldPassword(enteredUserId, oldPassword)) {
                    updatePassword(enteredUserId, newPassword);
                    passwordChangeFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid old password for student.");
                }
            }
        });

        passwordChangeFrame.add(passwordChangePanel);

        passwordChangeFrame.setSize(400, 200);
        passwordChangeFrame.setLocationRelativeTo(null);
        passwordChangeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        passwordChangeFrame.setVisible(true);
    }


    private boolean validateOldPassword(String userId, String enteredOldPassword) {
        if (userId.startsWith("P")) {
            Professor professor = Professor.getProfessorById(userId);
            return professor != null && professor.password().equals(enteredOldPassword);
        } else {
            Student student = Student.getStudentById(Integer.parseInt(userId));
            return student != null && student.password().equals(enteredOldPassword);
        }
    }

    private void updatePassword(String userId, String newPassword) {
        String updateQuery;
        boolean isProfessor = userId.startsWith("P");

        if (isProfessor) {
            updateQuery = SQLQueries.getUpdateProfessorPasswordQuery();
        } else {
            updateQuery = SQLQueries.getUpdateStudentPasswordQuery();
        }


        if (updateQuery != null) {
            if (isValidPassword(newPassword)) {
                try (Connection connection = new SQLDBConnector().getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setString(1, newPassword);
                    if (isProfessor) {
                        preparedStatement.setString(2, userId);
                    } else {
                        preparedStatement.setInt(2, Integer.parseInt(userId));
                    }
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Password updated successfully.");
                } catch (SQLException e) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "An SQL exception occurred", e);
                }

            } else {
                JOptionPane.showMessageDialog(null,
                        "Invalid password. Password must be maximum 12 characters and contain both lowercase and uppercase letters, and numbers.");
            }
        }
    }




    private boolean isValidPassword(String password) {
        return password.length() <= 12 &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*");
    }

    private void updateTheme(String theme) {
        String filePath = "src/main/resources/theme.properties";

        try {
            Properties properties = new Properties();
            try (InputStream input = new FileInputStream(filePath)) {
                properties.load(input);
            } catch (FileNotFoundException e) {
                return;
            }

            String themeWithoutDot = theme.startsWith(".") ? theme.substring(1) : theme;
            properties.setProperty("theme", themeWithoutDot);
            try (OutputStream output = new FileOutputStream(filePath)) {
                properties.store(output, null);
            }
            applyTheme(themeWithoutDot);

        } catch (IOException io) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "IO exception", io);
        }
    }


    private void applyTheme(String theme) {
        try {
            UIManager.setLookAndFeel(theme);
            SwingUtilities.updateComponentTreeUI(this);
            this.repaint();
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Theme error", e);
        }
    }


    public void openUserForm(String username, int studentId) {
        SwingUtilities.invokeLater(() -> new menuForm(username, studentId).setVisible(true));
        this.dispose();
    }
    private void openProfForm(String fName, String lName, String profID) {
        SwingUtilities.invokeLater(() -> new profForm(fName, lName,profID).setVisible(true));
        this.dispose();
    }
        private void openAdminForm() {
        SwingUtilities.invokeLater(() -> new adminform().setVisible(true));
        this.dispose();
    }

    boolean testDatabaseConnection() {
        try {
            DriverManager.setLoginTimeout(2);

            SQLDBConnector dbConnector = new SQLDBConnector();
            dbConnector.getConnection();

            return true;
        } catch (SQLException e) {
            return false;
        }
    }




    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        panelLogIn = new JPanel();
        tbUser = new JTextField();
        lbUser = new JLabel();
        pfPassword = new JPasswordField();
        label1 = new JLabel();
        optionLabel = new JLabel();
        btLogIn = new JButton();
        label2 = new JLabel();

        //======== panelLogIn ========
        {
            panelLogIn.setPreferredSize(new Dimension(300, 135));
            panelLogIn.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border
            . EmptyBorder( 0, 0, 0, 0) , "JF\u006frmD\u0065sig\u006eer \u0045val\u0075ati\u006fn", javax. swing. border. TitledBorder. CENTER, javax
            . swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font .BOLD ,
            12 ), java. awt. Color. red) ,panelLogIn. getBorder( )) ); panelLogIn. addPropertyChangeListener (new java. beans
            . PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e) {if ("\u0062ord\u0065r" .equals (e .
            getPropertyName () )) throw new RuntimeException( ); }} );

            //---- lbUser ----
            lbUser.setText("User ID");
            lbUser.setFont(new Font("JetBrains Mono", lbUser.getFont().getStyle(), lbUser.getFont().getSize() + 4));

            //---- label1 ----
            label1.setText("Password");
            label1.setFont(new Font("JetBrains Mono", label1.getFont().getStyle(), label1.getFont().getSize() + 4));

            //---- optionLabel ----
            optionLabel.setText("Options");

            //---- btLogIn ----
            btLogIn.setText("Log In");

            //---- label2 ----
            label2.setText("Log In");
            label2.setFont(new Font("JetBrains Mono ExtraBold", label2.getFont().getStyle(), label2.getFont().getSize() + 10));

            GroupLayout panelLogInLayout = new GroupLayout(panelLogIn);
            panelLogIn.setLayout(panelLogInLayout);
            panelLogInLayout.setHorizontalGroup(
                panelLogInLayout.createParallelGroup()
                    .addGroup(panelLogInLayout.createSequentialGroup()
                        .addGroup(panelLogInLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(label2)
                            .addGroup(panelLogInLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLogInLayout.createParallelGroup()
                                    .addComponent(lbUser)
                                    .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelLogInLayout.createParallelGroup()
                                    .addComponent(tbUser, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pfPassword, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)))
                            .addGroup(GroupLayout.Alignment.TRAILING, panelLogInLayout.createSequentialGroup()
                                .addComponent(optionLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btLogIn)))
                        .addGap(0, 0, Short.MAX_VALUE))
            );
            panelLogInLayout.setVerticalGroup(
                panelLogInLayout.createParallelGroup()
                    .addGroup(panelLogInLayout.createSequentialGroup()
                        .addComponent(label2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelLogInLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(lbUser)
                            .addComponent(tbUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelLogInLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(label1)
                            .addComponent(pfPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelLogInLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panelLogInLayout.createSequentialGroup()
                                .addComponent(btLogIn)
                                .addGap(34, 34, 34))
                            .addGroup(GroupLayout.Alignment.TRAILING, panelLogInLayout.createSequentialGroup()
                                .addComponent(optionLabel)
                                .addGap(20, 20, 20))))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JPanel panelLogIn;
    JTextField tbUser;
    private JLabel lbUser;
    JPasswordField pfPassword;
    private JLabel label1;
    private JLabel optionLabel;
    private JButton btLogIn;
    private JLabel label2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}


