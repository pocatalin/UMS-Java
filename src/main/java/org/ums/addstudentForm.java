package org.ums;

import javax.swing.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.*;

public class addstudentForm extends JFrame{
    private final DatabaseConnector databaseConnector;
    private int selectedFacultyId;
    private String selectedFacultyName;
    private int selectedDepartmentId;
    private String selectedDepartmentName;
    private String selectedStudentYear="";
    private int selectedGroupNumber;
    private final adminform parentForm;
    public addstudentForm(adminform parentForm) {
        initComponents();
        databaseConnector = new SQLDBConnector();
        populateCmbDateofBirth();
        populateCmbYear();
        this.parentForm=parentForm;
        cmbFaculty.addActionListener(e -> {
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
        });
        cmbGroup.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object selectedItem = cmbGroup.getSelectedItem();
                if (selectedItem instanceof Integer) {
                    selectedGroupNumber = (Integer) selectedItem;
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

        cmbStudentYear.addActionListener(e -> selectedStudentYear = (String) cmbStudentYear.getSelectedItem());
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
        btAdd.addActionListener(e -> insertProfessorData());

        setContentPane(panelasf);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            fillFacultyComboBox();
            fillGroupComboBox();
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }

    private void insertProfessorData() {

        String firstName = tfFirstName.getText();
        String lastName = tfLastName.getText();
        String cnp = tfCNP.getText();
        String email = tfEmail.getText();
        String phoneNumber = tfPhoneNumber.getText();
        String address = tfAddress.getText();
        String year = (String) cmbYear.getSelectedItem();
        String month = (String) cmbMonth.getSelectedItem();
        String day = (String) cmbDay.getSelectedItem();
        String yearR = (String) cmbYear2.getSelectedItem();
        String monthR = (String) cmbMonth2.getSelectedItem();
        String dayR = (String) cmbDay2.getSelectedItem();

        assert month != null;
        assert day != null;
        String formattedDate = String.format("%s-%02d-%02d", year, Integer.parseInt(month), Integer.parseInt(day));
        assert monthR != null;
        assert dayR != null;
        String formattedDateR = String.format("%s-%02d-%02d", yearR, Integer.parseInt(monthR), Integer.parseInt(dayR));

        try {
            String sql = "INSERT INTO Students (FirstName, LastName, CNP, DateOfBirth, Email, PhoneNumber, Address, RegistrationDate, FacultyID, DepartmentID, YearID, GroupNumber) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, cnp);
                preparedStatement.setString(4, formattedDate);
                preparedStatement.setString(5, email);
                preparedStatement.setString(6, phoneNumber);
                preparedStatement.setString(7, address);
                preparedStatement.setString(8, formattedDateR);
                preparedStatement.setInt(9, selectedFacultyId);
                preparedStatement.setInt(10, selectedDepartmentId);
                preparedStatement.setString(11, selectedStudentYear);
                preparedStatement.setInt(12, selectedGroupNumber);


                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data inserted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    parentForm.refreshAllTables();
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




    private void fillFacultyComboBox() throws SQLException {
        String sql = "SELECT FacultyID, FacultyName FROM Faculties";

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
    private void fillGroupComboBox() throws SQLException {
        String sql = fillGroupComboBoxQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            cmbGroup.removeAllItems();
            cmbGroup.addItem("");
            while (resultSet.next()) {
                int FacultyName = resultSet.getInt("GroupNumber");
                cmbGroup.addItem(FacultyName);
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
                cmbDepartment.addItem(resultSet.getString("DepartmentName"));
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
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

        cmbYear2.addItem("");
        for (int year = 2024; year >= 1900; year--) {
            cmbYear2.addItem(String.valueOf(year));
        }

        cmbMonth2.addItem("");
        for (int month = 1; month <= 12; month++) {
            cmbMonth2.addItem(String.valueOf(month));
        }

        cmbDay2.addItem("");
        for (int day = 1; day <= 31; day++) {
            cmbDay2.addItem(String.valueOf(day));
        }
    }
    private void populateCmbYear() {
        cmbStudentYear.addItem("");
        for (int year = 1; year <= 3; year++) {
            cmbStudentYear.addItem(String.valueOf(year));
        }
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        panelasf = new JPanel();
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
        cmbYear2 = new JComboBox();
        cmbMonth2 = new JComboBox();
        cmbDay2 = new JComboBox();
        JLabel label10 = new JLabel();
        cmbStudentYear = new JComboBox();
        JLabel label11 = new JLabel();
        cmbGroup = new JComboBox();
        JLabel label13 = new JLabel();

        //======== panelasf ========
        {
            panelasf.setBorder ( new javax . swing. border .CompoundBorder ( new javax . swing. border .TitledBorder (
                    new javax . swing. border .EmptyBorder ( 0, 0 ,0 , 0) ,  ""
                    , javax. swing .border . TitledBorder. CENTER ,javax . swing. border .TitledBorder . BOTTOM
                    , new java. awt .Font ( "D\u0069al\u006fg", java .awt . Font. BOLD ,12 )
                    ,java . awt. Color .red ) ,panelasf. getBorder () ) ); panelasf. addPropertyChangeListener(
                e-> { if( "\u0062or\u0064er" .equals ( e. getPropertyName () ) )throw new RuntimeException( )
                        ;} );

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

            //---- label10 ----
            label10.setText("Registration Date");

            //---- label11 ----
            label11.setText("Year");

            //---- label13 ----
            label13.setText("Group");

            GroupLayout panelasfLayout = new GroupLayout(panelasf);
            panelasf.setLayout(panelasfLayout);
            panelasfLayout.setHorizontalGroup(
                    panelasfLayout.createParallelGroup()
                            .addGroup(panelasfLayout.createSequentialGroup()
                                    .addGroup(panelasfLayout.createParallelGroup()
                                            .addGroup(panelasfLayout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                            .addGroup(panelasfLayout.createSequentialGroup()
                                                                    .addComponent(label6)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                                            .addComponent(tfAddress, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
                                                                            .addComponent(label7))
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                                            .addGroup(panelasfLayout.createSequentialGroup()
                                                                                    .addComponent(cmbYear2, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                    .addComponent(cmbMonth2, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                    .addComponent(cmbDay2, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                    .addComponent(cmbFaculty, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                                                                            .addComponent(label10)))
                                                            .addGroup(GroupLayout.Alignment.TRAILING, panelasfLayout.createSequentialGroup()
                                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                                            .addComponent(label1)
                                                                            .addComponent(tfFirstName, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                                            .addComponent(tfLastName, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                                                            .addComponent(label2))
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                                            .addComponent(label3)
                                                                            .addComponent(tfCNP, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panelasfLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                            .addComponent(label4)
                                                                            .addComponent(cmbYear, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(cmbMonth, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(cmbDay, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                                            .addComponent(tfEmail, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
                                                                            .addComponent(label5)
                                                                            .addComponent(label8)))
                                                            .addComponent(tfPhoneNumber, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(panelasfLayout.createSequentialGroup()
                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                            .addGroup(panelasfLayout.createSequentialGroup()
                                                                    .addGap(6, 6, 6)
                                                                    .addComponent(label9)
                                                                    .addGap(92, 92, 92))
                                                            .addGroup(GroupLayout.Alignment.TRAILING, panelasfLayout.createSequentialGroup()
                                                                    .addContainerGap()
                                                                    .addComponent(cmbDepartment, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)))
                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                            .addComponent(cmbStudentYear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(label11))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelasfLayout.createParallelGroup()
                                                            .addGroup(panelasfLayout.createSequentialGroup()
                                                                    .addComponent(cmbGroup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(btAdd))
                                                            .addComponent(label13))))
                                    .addContainerGap(19, Short.MAX_VALUE))
            );
            panelasfLayout.setVerticalGroup(
                    panelasfLayout.createParallelGroup()
                            .addGroup(panelasfLayout.createSequentialGroup()
                                    .addGap(30, 30, 30)
                                    .addGroup(panelasfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label1)
                                            .addComponent(label2)
                                            .addComponent(label3)
                                            .addComponent(label4)
                                            .addComponent(label5))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelasfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(tfFirstName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tfLastName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tfCNP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbYear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbMonth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbDay, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tfEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panelasfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label6)
                                            .addComponent(label7)
                                            .addComponent(label10)
                                            .addComponent(label8))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelasfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(tfPhoneNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tfAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbYear2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbMonth2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbDay2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbFaculty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelasfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label9, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label11)
                                            .addComponent(label13))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelasfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(cmbDepartment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbStudentYear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbGroup, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btAdd))
                                    .addContainerGap(76, Short.MAX_VALUE))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JPanel panelasf;
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
    private JComboBox cmbYear2;
    private JComboBox cmbMonth2;
    private JComboBox cmbDay2;
    private JComboBox cmbStudentYear;
    private JComboBox cmbGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
