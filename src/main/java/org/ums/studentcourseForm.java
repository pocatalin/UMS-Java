package org.ums;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.fillCourseComboBoxQuery;
import static org.ums.SQLQueries.getCourseIdByNameQuery;

public class studentcourseForm extends JFrame {
    private final DatabaseConnector databaseConnector;
    private String selectedCourseName;
    private int selectedCourseId;
    private adminform parentForm;
    public studentcourseForm(adminform parentForm) {
        initComponents();
        databaseConnector = new SQLDBConnector();
        this.parentForm = parentForm;
        cmbCourse.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selectedCourseName = (String) cmbCourse.getSelectedItem();
                if (selectedCourseName != null) {
                    try {
                        selectedCourseId = getCourseIdByName(selectedCourseName);
                    } catch (SQLException ex) {
                        Logger logger = Logger.getLogger(SQLQueries.class.getName());
                        logger.log(Level.SEVERE, "SQLException", e);
                    }
                }
            }
        });
        buttonAdd.addActionListener(e -> addStudentToCourse());

        setContentPane(panelsd);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            fillCourseComboBox();
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }

    private void addStudentToCourse() {
        try {
            String studentsText = tbStudents.getText();
            int courseId = selectedCourseId;

            String[] studentIDs = studentsText.split(",");
            StringBuilder successMessage = new StringBuilder();
            StringBuilder failureMessage = new StringBuilder();

            try {
                String insertSql = "INSERT INTO StudentCourses (StudentID, CourseID) VALUES (?, ?)";

                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(insertSql)) {
                    for (String studentID : studentIDs) {
                        int studentIDValue = Integer.parseInt(studentID);
                        preparedStatement.setInt(1, studentIDValue);
                        preparedStatement.setInt(2, courseId);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            successMessage.append("Data updated successfully for StudentID: ").append(studentIDValue).append("\n");
                            parentForm.refreshAllTables();
                        } else {
                            failureMessage.append("No rows affected for StudentID: ").append(studentIDValue).append("\n");
                        }
                    }
                }

                if (successMessage.length() > 0) {
                    JOptionPane.showMessageDialog(null, successMessage.toString());
                    parentForm.refreshAllTables();
                }

                if (failureMessage.length() > 0) {
                    JOptionPane.showMessageDialog(null, failureMessage.toString());
                }

            } catch (SQLException | NumberFormatException e) {
                Logger logger = Logger.getLogger(SQLQueries.class.getName());
                logger.log(Level.SEVERE, "SQLException", e);
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }








    private int getCourseIdByName(String dapartmentName) throws SQLException {
        String sql = getCourseIdByNameQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, dapartmentName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("CourseID");
            } else {
                return -1;
            }
        }
    }

    private void fillCourseComboBox() throws SQLException {
        String sql = fillCourseComboBoxQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            cmbCourse.removeAllItems();
            cmbCourse.addItem(""); // add an empty item

            while (resultSet.next()) {
                String CourseName = resultSet.getString("CourseName");
                cmbCourse.addItem(CourseName);
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        panelsd = new JPanel();
        cmbCourse = new JComboBox();
        tbStudents = new JTextField();
        buttonAdd = new JButton();
        label1 = new JLabel();
        label2 = new JLabel();

        //======== panelsd ========
        {
            panelsd.setPreferredSize(new Dimension(700, 105));
            panelsd.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border. EmptyBorder(
            0, 0, 0, 0) , "", javax. swing. border. TitledBorder. CENTER, javax. swing. border. TitledBorder
            . BOTTOM, new java .awt .Font ("D\u0069al\u006fg" ,java .awt .Font .BOLD ,12 ), java. awt. Color.
            red) ,panelsd. getBorder( )) ); panelsd. addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .
            beans .PropertyChangeEvent e) {if ("\u0062or\u0064er" .equals (e .getPropertyName () )) throw new RuntimeException( ); }} );

            //---- buttonAdd ----
            buttonAdd.setText("Add");

            //---- label1 ----
            label1.setText("Course");

            //---- label2 ----
            label2.setText("Enter Student IDs");

            GroupLayout panelsdLayout = new GroupLayout(panelsd);
            panelsd.setLayout(panelsdLayout);
            panelsdLayout.setHorizontalGroup(
                panelsdLayout.createParallelGroup()
                    .addGroup(panelsdLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelsdLayout.createParallelGroup()
                            .addComponent(cmbCourse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(label1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelsdLayout.createParallelGroup()
                            .addGroup(panelsdLayout.createSequentialGroup()
                                .addComponent(tbStudents, GroupLayout.PREFERRED_SIZE, 385, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buttonAdd))
                            .addComponent(label2))
                        .addGap(7, 7, 7))
            );
            panelsdLayout.setVerticalGroup(
                panelsdLayout.createParallelGroup()
                    .addGroup(panelsdLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(panelsdLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(label2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label1))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelsdLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(panelsdLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(tbStudents, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(buttonAdd))
                            .addComponent(cmbCourse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(22, Short.MAX_VALUE))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JPanel panelsd;
    private JComboBox cmbCourse;
    private JTextField tbStudents;
    private JButton buttonAdd;
    private JLabel label1;
    private JLabel label2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}

