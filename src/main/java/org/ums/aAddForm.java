package org.ums;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.*;

public class aAddForm extends JFrame  {
    private String selectedWeek;
    private final DatabaseConnector databaseConnector;
    private int selectedCourseId;
    private String selectedCourseName;
    public aAddForm(String profID) {
        initComponents();
        JFrame frame = new JFrame("Add Attendances Form");
        frame.setContentPane(panelAtt);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        databaseConnector = new SQLDBConnector();

        populateWeeks();
        selectCurrentWeek();
        cmbWeek.addActionListener(e -> {
            selectedWeek = (String) cmbWeek.getSelectedItem();
            System.out.println(selectedWeek);
        });
        try {
            fillCourseComboBox(profID);
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "Exception", e);
        }
        cmbCourse.addActionListener(e -> {
            selectedCourseName = (String) cmbCourse.getSelectedItem();
            if (selectedCourseName != null) {
                try {
                    selectedCourseId = getCourseIdByName(selectedCourseName);
                    if (selectedCourseId != -1) {
                        System.out.println("Selected Course ID: " + selectedCourseId);

                    } else {
                        System.out.println("Course ID not found for name: " + selectedCourseName);
                    }
                } catch (SQLException exception) {
                    handleSQLException(exception);
                }
            }
        });
        btAdd.addActionListener(e -> handleAddButtonClick());
    }

    private void handleAddButtonClick() {
        String studentsText = tbStudents.getText();
        StringBuilder errorMessages = new StringBuilder();

        try {
            String sql = "UPDATE Attendance SET " + selectedWeek + " = 1 WHERE StudentID = ? AND CourseID = ?";

            try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                for (String studentID : studentsText.split(",")) {
                    preparedStatement.setString(1, studentID.trim());
                    preparedStatement.setInt(2, selectedCourseId);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        showMessage("Data updated successfully for StudentID: " + studentID + ". Rows affected: " + rowsAffected);
                    } else {
                        errorMessages.append("No rows affected for StudentID: ").append(studentID).append("\n");
                    }
                }
            }

            if (!errorMessages.isEmpty()) {
                showMessage("Errors:\n" + errorMessages);
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
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
    private void populateWeeks() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

        for (int i = 1; i <= 14; i++) {
            model.addElement("W" + i);
        }
        cmbWeek.setModel(model);
    }

    private void selectCurrentWeek() {
        int currentWeek = getCurrentWeek();
        currentWeek = Math.max(1, Math.min(currentWeek, 14));
        cmbWeek.setSelectedIndex(currentWeek - 1);
        selectedWeek = (String) cmbWeek.getSelectedItem();
    }

    private int getCurrentWeek() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        try {
            Date startDate = dateFormat.parse("01-10-2023");
            Date currentDate = calendar.getTime();

            calendar.setTime(startDate);
            int week = 0;

            while (calendar.getTime().before(currentDate)) {
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                week++;
            }

            return week -2;
        } catch (ParseException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "ParseException", e);
            return -1;
        }
    }

    private void fillCourseComboBox(String profID) throws SQLException {
        String sql = populateCoursesProfessorFromDatabaseQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, profID);
            ResultSet resultSet = preparedStatement.executeQuery();

            cmbCourse.removeAllItems();

            while (resultSet.next()) {
                String courseName = resultSet.getString("CourseName");
                cmbCourse.addItem(courseName);
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    private void handleSQLException(SQLException e) {
        Logger logger = Logger.getLogger(SQLQueries.class.getName());
        logger.log(Level.SEVERE, "SQLException", e);
    }





    private void initComponents() {


        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        panelAtt = new JPanel();
        cmbWeek = new JComboBox();
        cmbCourse = new JComboBox();
        tbStudents = new JTextField();
        btAdd = new JButton();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();

        //======== panelAtt ========
        {
            panelAtt.setPreferredSize(new Dimension(700, 200));
            panelAtt.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing. border. EmptyBorder( 0
            , 0, 0, 0) , "JF\u006frmDes\u0069gner \u0045valua\u0074ion", javax. swing. border. TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM
            , new java .awt .Font ("D\u0069alog" ,java .awt .Font .BOLD ,12 ), java. awt. Color. red) ,
            panelAtt. getBorder( )) ); panelAtt. addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java .beans .PropertyChangeEvent e
            ) {if ("\u0062order" .equals (e .getPropertyName () )) throw new RuntimeException( ); }} );

            //---- btAdd ----
            btAdd.setText("Add");

            //---- label1 ----
            label1.setText("Week");

            //---- label2 ----
            label2.setText("Course");

            //---- label3 ----
            label3.setText("Enter Student IDs");

            GroupLayout panelAttLayout = new GroupLayout(panelAtt);
            panelAtt.setLayout(panelAttLayout);
            panelAttLayout.setHorizontalGroup(
                panelAttLayout.createParallelGroup()
                    .addGroup(panelAttLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelAttLayout.createParallelGroup()
                            .addComponent(btAdd, GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                            .addGroup(panelAttLayout.createSequentialGroup()
                                .addGroup(panelAttLayout.createParallelGroup()
                                    .addComponent(cmbWeek, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label1))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelAttLayout.createParallelGroup()
                                    .addComponent(cmbCourse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label2))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelAttLayout.createParallelGroup()
                                    .addGroup(panelAttLayout.createSequentialGroup()
                                        .addComponent(label3)
                                        .addGap(0, 312, Short.MAX_VALUE))
                                    .addComponent(tbStudents, GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))))
                        .addContainerGap())
            );
            panelAttLayout.setVerticalGroup(
                panelAttLayout.createParallelGroup()
                    .addGroup(panelAttLayout.createSequentialGroup()
                        .addGroup(panelAttLayout.createParallelGroup()
                            .addGroup(panelAttLayout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addGroup(panelAttLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addComponent(label2)
                                    .addComponent(label1)))
                            .addGroup(GroupLayout.Alignment.TRAILING, panelAttLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label3)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelAttLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(cmbCourse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbWeek, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbStudents, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btAdd)
                        .addContainerGap(14, Short.MAX_VALUE))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JPanel panelAtt;
    private JComboBox cmbWeek;
    private JComboBox cmbCourse;
    private JTextField tbStudents;
    private JButton btAdd;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

}
