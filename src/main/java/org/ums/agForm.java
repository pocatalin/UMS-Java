package org.ums;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.*;

public class agForm extends JFrame{
    private final DatabaseConnector databaseConnector;
    private int selectedStudentId;
    private int selectedCourseId;
    private String selectedCourseName;
    public agForm(String profID) {
        initComponents();
        databaseConnector = new SQLDBConnector();

        cmbCourseSelect.addItemListener(e -> {
            selectedCourseName = (String) cmbCourseSelect.getSelectedItem();
            if (selectedCourseName != null) {
                try {
                    selectedCourseId = getCourseIdByName(selectedCourseName);
                    if (selectedCourseId != -1) {
                        fillStudentComboBox(profID, selectedCourseId);
                    }
                } catch (SQLException ex) {
                    Logger logger = Logger.getLogger(SQLQueries.class.getName());
                    logger.log(Level.SEVERE, "SQLException", e);
                }
            }
        });



        cmbStudent.addActionListener(e -> {
            String selectedFullName = (String) cmbStudent.getSelectedItem();
            try {
                selectedStudentId = getStudentIdByName(selectedFullName);
            } catch (SQLException ex) {
                Logger logger = Logger.getLogger(SQLQueries.class.getName());
                logger.log(Level.SEVERE, "SQLException", e);
            }
        });


        tbLab.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFinalGrade(tbLab);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFinalGrade(tbLab);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFinalGrade(tbLab);
            }
        });

        tbCourse.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFinalGrade(tbCourse);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFinalGrade(tbCourse);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFinalGrade(tbCourse);
            }
        });
        btAdd.addActionListener(e -> new Thread(this::addGradeToDatabase).start());

        JFrame frame = new JFrame("Add Grade Form");
        frame.setContentPane(panelAddGrade);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        try {
            fillCourseComboBox(profID);
        } catch (SQLException ex) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", ex);
        }
    }

    private void updateFinalGrade(JTextField textField) {
        String text = textField.getText();

        if (!isValidNumber(text)) {
            Toolkit.getDefaultToolkit().beep();
            SwingUtilities.invokeLater(() -> {
                textField.setText(text.substring(0, text.length() - 1));
                updateFinalGrade();
            });
        } else {
            updateFinalGrade();
        }
    }

    private void updateFinalGrade() {
        String labGradeText = tbLab.getText();
        String courseGradeText = tbCourse.getText();

        boolean isLabValid = isValidNumber(labGradeText);
        boolean isCourseValid = isValidNumber(courseGradeText);

        if (isLabValid && isCourseValid) {
            double labGrade = Double.parseDouble(labGradeText);
            double courseGrade = Double.parseDouble(courseGradeText);

            double finalGrade = (0.7 * labGrade) + (0.3 * courseGrade);

            finalGrade = Math.ceil(finalGrade);

            tbFinal.setText(String.format("%.0f", finalGrade));
        } else if (isLabValid) {
            tbFinal.setText(labGradeText);
        } else if (isCourseValid) {
            tbFinal.setText(courseGradeText);
        } else {
            tbFinal.setText("");
        }
    }

    private boolean isValidNumber(String text) {
        try {
            double value = Double.parseDouble(text);

            return value >= 1 && value <= 10 && countDecimals(value) <= 2;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int countDecimals(double value) {
        String valueStr = Double.toString(value);
        int indexOfDecimal = valueStr.indexOf('.');

        return indexOfDecimal < 0 ? 0 : valueStr.length() - indexOfDecimal - 1;
    }

    private void addGradeToDatabase() {
        String labGradeText = tbLab.getText();
        String courseGradeText = tbCourse.getText();

        if (isValidGrade() && isValidGrade()) {
            double labGrade = Double.parseDouble(labGradeText);
            double courseGrade = Double.parseDouble(courseGradeText);

            if (selectedStudentId != -1 && selectedCourseId != -1) {
                if (isValidRange(labGrade) && isValidRange(courseGrade)) {
                    String sql = "INSERT INTO Grades (StudentID, CourseID, LabGrade, CourseGrade) VALUES (?, ?, ?, ?)";

                    try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                        preparedStatement.setInt(1, selectedStudentId);
                        preparedStatement.setInt(2, selectedCourseId);
                        preparedStatement.setDouble(3, labGrade);
                        preparedStatement.setDouble(4, courseGrade);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            showMessage("Grade added successfully", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            showMessage("Failed to add grade", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        if (ex.getSQLState().equals("23000") && ex.getErrorCode() == 2627) {
                            showMessage("Grade already exists for the selected student and course", JOptionPane.WARNING_MESSAGE);
                        } else {
                            Logger logger = Logger.getLogger(SQLQueries.class.getName());
                            logger.log(Level.SEVERE, "SQLException", ex);
                        }
                    }
                } else {
                    showMessage("Invalid grade range (must be between 1 and 10)", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showMessage("Invalid student or course information", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            showMessage("Invalid grade format", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, int messageType) {
        JOptionPane.showMessageDialog(agForm.this, message, "Message", messageType);
    }


    private boolean isValidGrade() {
        try {
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidRange(double grade) {
        return grade >= 1 && grade <= 10;
    }





    private int getCourseIdByName(String courseName) throws SQLException {
        String sql = getCourseIdByNameQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, courseName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("CourseID");
            } else {
                return -1;
            }
        }
    }
    private int getStudentIdByName(String fullName) throws SQLException {
        String sql = getStudentIdByNameQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, fullName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("StudentID");
            } else {
                return -1;
            }
        }
    }

    private void fillCourseComboBox(String profID) throws SQLException {
        String sql = populateCoursesProfessorFromDatabaseQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, profID);
            ResultSet resultSet = preparedStatement.executeQuery();

            cmbCourseSelect.removeAllItems();
            cmbCourseSelect.addItem("");
            while (resultSet.next()) {
                String courseName = resultSet.getString("CourseName");
                cmbCourseSelect.addItem(courseName);
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }

    private void fillStudentComboBox(String profID, int courseID) throws SQLException {
        String sql = fillStudentComboBoxQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, profID);
            preparedStatement.setInt(2, courseID);

            ResultSet resultSet = preparedStatement.executeQuery();

            cmbStudent.removeAllItems();

            while (resultSet.next()) {
                String fullName = resultSet.getString("FullName");
                cmbStudent.addItem(fullName);
            }
        }
    }





    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        panelAddGrade = new JPanel();
        cmbStudent = new JComboBox();
        tbLab = new JTextField();
        tbCourse = new JTextField();
        tbFinal = new JTextField();
        btAdd = new JButton();
        JLabel lbSStudent = new JLabel();
        JLabel lbGrade = new JLabel();
        JLabel lbCourse = new JLabel();
        JLabel lbFinal = new JLabel();
        cmbCourseSelect = new JComboBox();
        JLabel lbSelectCourse = new JLabel();

        //======== panelAddGrade ========
        {
            panelAddGrade.setPreferredSize(new Dimension(500, 200));
            panelAddGrade.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(
                    0,0,0,0), "",javax.swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder
                    .BOTTOM,new java.awt.Font("Dia\u006cog",java.awt.Font.BOLD,12),java.awt.Color.
                    red),panelAddGrade. getBorder()));panelAddGrade. addPropertyChangeListener(e-> {if("bord\u0065r".equals(e.getPropertyName()))throw new RuntimeException();});

            //---- btAdd ----
            btAdd.setText("Add");

            //---- lbSStudent ----
            lbSStudent.setText("Select Student");

            //---- lbGrade ----
            lbGrade.setText("Lab Grade");

            //---- lbCourse ----
            lbCourse.setText("Course Grade");

            //---- lbFinal ----
            lbFinal.setText("Final Grade");

            //---- lbSelectCourse ----
            lbSelectCourse.setText("Select Course");

            GroupLayout panelAddGradeLayout = new GroupLayout(panelAddGrade);
            panelAddGrade.setLayout(panelAddGradeLayout);
            panelAddGradeLayout.setHorizontalGroup(
                    panelAddGradeLayout.createParallelGroup()
                            .addGroup(panelAddGradeLayout.createSequentialGroup()
                                    .addGap(17, 17, 17)
                                    .addGroup(panelAddGradeLayout.createParallelGroup()
                                            .addGroup(GroupLayout.Alignment.TRAILING, panelAddGradeLayout.createSequentialGroup()
                                                    .addComponent(lbSelectCourse, GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                                                    .addContainerGap())
                                            .addGroup(GroupLayout.Alignment.TRAILING, panelAddGradeLayout.createSequentialGroup()
                                                    .addGroup(panelAddGradeLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                            .addComponent(cmbCourseSelect, GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                                                            .addGroup(panelAddGradeLayout.createSequentialGroup()
                                                                    .addGroup(panelAddGradeLayout.createParallelGroup()
                                                                            .addComponent(lbSStudent, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                                                            .addComponent(cmbStudent, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE))
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panelAddGradeLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                            .addComponent(tbLab)
                                                                            .addComponent(lbGrade, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE))
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panelAddGradeLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                            .addComponent(lbCourse, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                            .addComponent(tbCourse))
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(panelAddGradeLayout.createParallelGroup()
                                                                            .addComponent(lbFinal, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                                                                            .addGroup(panelAddGradeLayout.createSequentialGroup()
                                                                                    .addComponent(tbFinal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                    .addComponent(btAdd)))))
                                                    .addGap(73, 73, 73))))
            );
            panelAddGradeLayout.setVerticalGroup(
                    panelAddGradeLayout.createParallelGroup()
                            .addGroup(panelAddGradeLayout.createSequentialGroup()
                                    .addGap(17, 17, 17)
                                    .addComponent(lbSelectCourse)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cmbCourseSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panelAddGradeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lbSStudent)
                                            .addComponent(lbGrade)
                                            .addComponent(lbCourse)
                                            .addComponent(lbFinal))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelAddGradeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(tbLab, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmbStudent, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tbCourse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tbFinal, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btAdd))
                                    .addContainerGap(29, Short.MAX_VALUE))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JPanel panelAddGrade;
    private JComboBox cmbStudent;
    private JTextField tbLab;
    private JTextField tbCourse;
    private JTextField tbFinal;
    private JButton btAdd;
    private JComboBox cmbCourseSelect;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
