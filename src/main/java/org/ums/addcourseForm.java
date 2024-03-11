package org.ums;

import javax.swing.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.*;

public class addcourseForm extends JFrame {
    private final DatabaseConnector databaseConnector;
    private String selectedDepartmentName;
    private int selectedDepartmentId;
    private String selectedProfessorId;
    private String selectedProfessorName;
    private adminform parentForm;

    public addcourseForm(adminform parentForm) {
        initComponents();
        databaseConnector = new SQLDBConnector();
        this.parentForm=parentForm;
        cmbDepartment.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedDepartmentName = (String) cmbDepartment.getSelectedItem();
                if (selectedDepartmentName != null) {
                    try {
                        selectedDepartmentId = getDepartmentIdByName(selectedDepartmentName);
                        if (selectedDepartmentId != -1) {
                            fillProfessorComboBox(selectedDepartmentId);
                        }
                    } catch (SQLException ex) {
                        Logger logger = Logger.getLogger(SQLQueries.class.getName());
                        logger.log(Level.SEVERE, "SQLException", e);
                    }
                }
            }
        });

        cmbPorfessor.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedProfessorName = (String) cmbPorfessor.getSelectedItem();
                if (selectedProfessorName != null) {
                    try {
                        selectedProfessorId = getProfessorIdByName(selectedProfessorName);
                        if (selectedProfessorId  != null) {
                        }
                    } catch (SQLException ex) {
                        Logger logger = Logger.getLogger(SQLQueries.class.getName());
                        logger.log(Level.SEVERE, "SQLException", e);
                    }
                }
            }
        });


        btAdd.addActionListener(e -> {
            handleCourseAdd();
            addcourseForm.this.setVisible(false);
            addcourseForm.this.dispose();
        });


        tfCredits.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

        setContentPane(panelacf);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            fillDepartmentComboBox();
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }


    private void handleCourseAdd() {
        String courseName = tfCourseName.getText();
        String departmentId = String.valueOf(selectedDepartmentId);
        String professorId = selectedProfessorId;
        String credits = tfCredits.getText();

        String sql = "INSERT INTO Courses (CourseName, DepartmentID, ProfessorID, Credits) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, courseName);
            preparedStatement.setString(2, departmentId);
            preparedStatement.setString(3, professorId);
            preparedStatement.setString(4, credits);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Course added successfully.");
                parentForm.refreshAllTables();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add the course.");
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }


    private void fillProfessorComboBox(int departmentId) {
        String sql = fillProfessorComboBoxQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, departmentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            cmbPorfessor.removeAllItems();
            cmbPorfessor.addItem("");
            while (resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String professorName = firstName + " " + lastName;
                cmbPorfessor.addItem(professorName);
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
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
    private String getProfessorIdByName(String professorName) throws SQLException {
        String sql = getProfessorIdByNameQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, professorName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("ProfessorID");
            } else {
                return null;
            }
        }
    }


    private void fillDepartmentComboBox() throws SQLException {
        String sql = fillDepartmentComboBoxQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            cmbDepartment.removeAllItems();
            cmbDepartment.addItem("");
            while (resultSet.next()) {
                String CourseName = resultSet.getString("DepartmentName");
                cmbDepartment.addItem(CourseName);
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        panelacf = new JPanel();
        tfCourseName = new JTextField();
        cmbDepartment = new JComboBox();
        cmbPorfessor = new JComboBox();
        tfCredits = new JTextField();
        btAdd = new JButton();
        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();
        JLabel label4 = new JLabel();

        //======== panelacf ========
        {
            panelacf.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.
                    EmptyBorder(0,0,0,0), "",javax.swing.border.TitledBorder.CENTER,javax.swing
                    .border.TitledBorder.BOTTOM,new java.awt.Font("Dia\u006cog",java.awt.Font.BOLD,12),
                    java.awt.Color.red),panelacf. getBorder()));panelacf. addPropertyChangeListener(e-> {if("bord\u0065r".equals(e.getPropertyName()))
                        throw new RuntimeException();});

            //---- btAdd ----
            btAdd.setText("Add");

            //---- label1 ----
            label1.setText("Course Name");

            //---- label2 ----
            label2.setText("Department");

            //---- label3 ----
            label3.setText("Professor");

            //---- label4 ----
            label4.setText("Credits");

            GroupLayout panelacfLayout = new GroupLayout(panelacf);
            panelacf.setLayout(panelacfLayout);
            panelacfLayout.setHorizontalGroup(
                    panelacfLayout.createParallelGroup()
                            .addGroup(panelacfLayout.createSequentialGroup()
                                    .addGap(16, 16, 16)
                                    .addGroup(panelacfLayout.createParallelGroup()
                                            .addComponent(tfCourseName, GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                                            .addGroup(panelacfLayout.createSequentialGroup()
                                                    .addComponent(label1)
                                                    .addGap(0, 127, Short.MAX_VALUE)))
                                    .addGap(18, 18, 18)
                                    .addGroup(panelacfLayout.createParallelGroup()
                                            .addComponent(cmbDepartment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label2))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panelacfLayout.createParallelGroup()
                                            .addComponent(cmbPorfessor, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(label3))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panelacfLayout.createParallelGroup()
                                            .addGroup(panelacfLayout.createSequentialGroup()
                                                    .addComponent(tfCredits, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(btAdd))
                                            .addComponent(label4))
                                    .addContainerGap())
            );
            panelacfLayout.setVerticalGroup(
                    panelacfLayout.createParallelGroup()
                            .addGroup(panelacfLayout.createSequentialGroup()
                                    .addGap(25, 25, 25)
                                    .addGroup(panelacfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(label1)
                                            .addComponent(label2)
                                            .addComponent(label3)
                                            .addComponent(label4))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelacfLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(btAdd)
                                            .addComponent(tfCredits, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbPorfessor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbDepartment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tfCourseName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(89, Short.MAX_VALUE))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JPanel panelacf;
    private JTextField tfCourseName;
    private JComboBox cmbDepartment;
    private JComboBox cmbPorfessor;
    private JTextField tfCredits;
    private JButton btAdd;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
