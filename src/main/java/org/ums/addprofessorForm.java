package org.ums;

import javax.swing.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.*;

public class addprofessorForm extends JFrame{
    private final DatabaseConnector databaseConnector;
    private int selectedFacultyId;
    private String selectedFacultyName;
    private int selectedDepartmentId;
    private String selectedDepartmentName;
    private adminform parentForm;
    public addprofessorForm(adminform parentForm) {
        initComponents();
        databaseConnector = new SQLDBConnector();
        this.parentForm=parentForm;
        populateCmbDateofBirth();


        cmbFaculty.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedFacultyName = (String) cmbFaculty.getSelectedItem();
                if (selectedFacultyName != null) {
                    try {
                        selectedFacultyId = getFacultyIdByName(selectedFacultyName);
                        if (selectedFacultyId != -1) {
                            fillDepartmentComboBox(selectedFacultyId);
                        }
                    } catch (SQLException ex) {
                        Logger logger = Logger.getLogger(SQLQueries.class.getName());
                        logger.log(Level.SEVERE, "SQLException", e);
                    }
                }
            }
        });
        cmbDepartment.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedDepartmentName = (String) cmbDepartment.getSelectedItem();
                if (selectedDepartmentName != null) {
                    try {
                        selectedDepartmentId = getDepartmentIdByName(selectedDepartmentName);
                        if (selectedDepartmentId  != -1) {
                        }
                    } catch (SQLException ex) {
                        Logger logger = Logger.getLogger(SQLQueries.class.getName());
                        logger.log(Level.SEVERE, "SQLException", e);
                    }
                }
            }
        });

        btAdd.addActionListener(e -> {
            insertProfessorData();
            addprofessorForm.this.dispose();
        });
        tfFirstName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c)) {
                    e.consume();
                }
            }
        });

        tfLastName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c)) {
                    e.consume();
                }
            }
        });

        tfCNP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || tfCNP.getText().length() >= 13) {
                    e.consume();
                }
            }
        });

        tfPhoneNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

        setContentPane(panelapf);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            fillFacultyComboBox();
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }
    private String getNextProfessorID() {
        try {
            String sql = getNextProfessorIDQuery();
            try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String maxID = resultSet.getString("MaxID");

                    int lastNumber = Integer.parseInt(maxID.substring(1));

                    int nextNumber = lastNumber + 1;

                    return "P" + nextNumber;
                }
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }

        return "";
    }

    private void insertProfessorData() {
        String professorId = getNextProfessorID();
        String firstName = tfFirstName.getText();
        String lastName = tfLastName.getText();
        String cnp = tfCNP.getText();
        String email = tfEmail.getText();
        String phoneNumber = tfPhoneNumber.getText();
        String address = tfAddress.getText();
        String year = (String) cmbYear.getSelectedItem();
        String month = (String) cmbMonth.getSelectedItem();
        String day = (String) cmbDay.getSelectedItem();


        assert month != null;
        assert day != null;
        String formattedDate = String.format("%s-%02d-%02d", year, Integer.parseInt(month), Integer.parseInt(day));

        try {
            String sql = insertProfessorDataQuery();
            try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                preparedStatement.setString(1, professorId);
                preparedStatement.setString(2, firstName);
                preparedStatement.setString(3, lastName);
                preparedStatement.setString(4, cnp);
                preparedStatement.setString(5, formattedDate);
                preparedStatement.setString(6, email);
                preparedStatement.setString(7, phoneNumber);
                preparedStatement.setString(8, address);
                preparedStatement.setInt(9, selectedFacultyId);
                preparedStatement.setInt(10, selectedDepartmentId);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data inserted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    parentForm.refreshAllTables();
                    addprofessorForm.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to insert data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
            JOptionPane.showMessageDialog(this, "An error occurred while inserting data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void populateCmbDateofBirth()
    {
        cmbYear.addItem("");
        for (int year = 2024; year >= 1900; year--) {
            cmbYear.addItem(String.valueOf(year));
        }

        cmbMonth.addItem("");
        for (int month = 1; month <= 12; month++) {
            cmbMonth.addItem(String.valueOf(month));
        }

        cmbDay.addItem("");
        for (int day = 1; day <= 31; day++) {
            cmbDay.addItem(String.valueOf(day));
        }
    }

    private void fillFacultyComboBox() throws SQLException {
        String sql = fillFacultyComboBoxQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            cmbFaculty.removeAllItems();
            cmbFaculty.addItem("");
            while (resultSet.next()) {
                String FacultyName = resultSet.getString("FacultyName");
                cmbFaculty.addItem(FacultyName);
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }

    private int getFacultyIdByName(String selectedFacultyName) throws SQLException {
        String sql = getFacultyIdByNameQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, selectedFacultyName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("FacultyID");
            } else {
                return -1;
            }
        }
    }

    private int getDepartmentIdByName(String dapartmentName) throws SQLException {
        String sql = getDepartmentIdByNameQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, dapartmentName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("DepartmentID");
            } else {
                return -1;
            }
        }
    }

    private void fillDepartmentComboBox(int facultyId) {
        String sql = fillDepartmentComboBoxBossQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, facultyId);
            ResultSet resultSet = preparedStatement.executeQuery();

            cmbDepartment.removeAllItems();

            while (resultSet.next()) {
                String professorName = resultSet.getString("DepartmentName");
                cmbDepartment.addItem(professorName);
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        panelapf = new JPanel();
        btAdd = new JButton();
        tfFirstName = new JTextField();
        tfLastName = new JTextField();
        tfCNP = new JTextField();
        tfEmail = new JTextField();
        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();
        JLabel label4 = new JLabel();
        JLabel label5 = new JLabel();
        tfPhoneNumber = new JTextField();
        JLabel label6 = new JLabel();
        tfAddress = new JTextField();
        JLabel label7 = new JLabel();
        cmbFaculty = new JComboBox();
        JLabel label8 = new JLabel();
        cmbDepartment = new JComboBox();
        JLabel label9 = new JLabel();
        cmbYear = new JComboBox();
        cmbMonth = new JComboBox();
        cmbDay = new JComboBox();

        //======== panelapf ========
        {
            panelapf.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.
                    swing.border.EmptyBorder(0,0,0,0), "",javax.swing.border
                    .TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM,new java.awt.Font("D\u0069al\u006fg"
                    ,java.awt.Font.BOLD,12),java.awt.Color.red),panelapf. getBorder
                    ()));panelapf. addPropertyChangeListener(e-> {if("\u0062or\u0064er".equals(e.getPropertyName()))throw new RuntimeException
();});

            //---- btAdd ----
            btAdd.setText("Add");

            //---- label1 ----
            label1.setText("First Name");

            //---- label2 ----
            label2.setText("Last Name");

            //---- label3 ----
            label3.setText("CNP");

            //---- label4 ----
            label4.setText("Date of Birth");

            //---- label5 ----
            label5.setText("Email");

            //---- label6 ----
            label6.setText("Phone Number");

            //---- label7 ----
            label7.setText("Address");

            //---- label8 ----
            label8.setText("Faculty");

            //---- label9 ----
            label9.setText("Department");

            GroupLayout panelapfLayout = new GroupLayout(panelapf);
            panelapf.setLayout(panelapfLayout);
            panelapfLayout.setHorizontalGroup(
                    panelapfLayout.createParallelGroup()
                            .addGroup(panelapfLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panelapfLayout.createParallelGroup()
                                            .addGroup(panelapfLayout.createSequentialGroup()
                                                    .addGroup(panelapfLayout.createParallelGroup()
                                                            .addComponent(tfPhoneNumber, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(label6))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelapfLayout.createParallelGroup()
                                                            .addGroup(panelapfLayout.createSequentialGroup()
                                                                    .addComponent(tfAddress, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(cmbFaculty, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                                                            .addComponent(label7)
                                                            .addGroup(panelapfLayout.createSequentialGroup()
                                                                    .addGap(266, 266, 266)
                                                                    .addComponent(label8)))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelapfLayout.createParallelGroup()
                                                            .addGroup(panelapfLayout.createSequentialGroup()
                                                                    .addComponent(cmbDepartment, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                    .addComponent(btAdd)
                                                                    .addGap(18, 18, 18))
                                                            .addGroup(panelapfLayout.createSequentialGroup()
                                                                    .addComponent(label9)
                                                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                            .addGroup(panelapfLayout.createSequentialGroup()
                                                    .addGroup(panelapfLayout.createParallelGroup()
                                                            .addComponent(label1)
                                                            .addComponent(tfFirstName, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelapfLayout.createParallelGroup()
                                                            .addComponent(tfLastName, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(label2))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelapfLayout.createParallelGroup()
                                                            .addComponent(label3)
                                                            .addComponent(tfCNP, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelapfLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                            .addComponent(label4)
                                                            .addComponent(cmbYear, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(cmbMonth, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(cmbDay, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelapfLayout.createParallelGroup()
                                                            .addComponent(tfEmail, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(label5))
                                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            );
            panelapfLayout.setVerticalGroup(
                    panelapfLayout.createParallelGroup()
                            .addGroup(panelapfLayout.createSequentialGroup()
                                    .addGap(30, 30, 30)
                                    .addGroup(panelapfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label1)
                                            .addComponent(label2)
                                            .addComponent(label3)
                                            .addComponent(label4)
                                            .addComponent(label5))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelapfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(tfFirstName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tfLastName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tfCNP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbYear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbMonth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbDay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tfEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panelapfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label6)
                                            .addComponent(label7)
                                            .addComponent(label8)
                                            .addComponent(label9, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelapfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(tfPhoneNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tfAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbFaculty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btAdd)
                                            .addComponent(cmbDepartment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(51, Short.MAX_VALUE))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JPanel panelapf;
    private JButton btAdd;
    private JTextField tfFirstName;
    private JTextField tfLastName;
    private JTextField tfCNP;
    private JTextField tfEmail;
    private JTextField tfPhoneNumber;
    private JTextField tfAddress;
    private JComboBox cmbFaculty;
    private JComboBox cmbDepartment;
    private JComboBox cmbYear;
    private JComboBox cmbMonth;
    private JComboBox cmbDay;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
