package org.ums;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.*;

public class DataFunctionsStudent implements DataGetter {
    public DefaultListModel<String> listModel;
    public DefaultTableModel tableModel;
    public DatabaseConnector databaseConnector;
    public JPanel panelMenu;
    public JScrollPane listMenu;
    public JTable table;
    public JPanel panelStudentInfo;
    public JPanel panelGrades;
    public JPanel panelAtta;


    public DataFunctionsStudent(DefaultListModel<String> listModel, DefaultTableModel tableModel, DatabaseConnector databaseConnector,
                                JPanel panelMenu, JScrollPane listMenu, JTable table, JPanel panelStudentInfo, JPanel panelGrades, JPanel panelAtta) {
        this.listModel = listModel;
        this.tableModel = tableModel;
        this.databaseConnector = databaseConnector;
        this.panelMenu = panelMenu;
        this.listMenu = listMenu;
        this.table = table;
        this.panelStudentInfo = panelStudentInfo;
        this.panelGrades = panelGrades;
        this.panelAtta = panelAtta;
    }


    @Override//student info
    public void populateTableFromDatabase(Integer studentID) {
        String sql = populateTableFromDatabaseQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, studentID);
            ResultSet resultSet = preparedStatement.executeQuery();

            panelStudentInfo.removeAll();
            panelStudentInfo.setLayout(new GridLayout(0, 2));

            if (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(i);

                    JLabel label = new JLabel(columnName);
                    panelStudentInfo.add(label);

                    JTextField textField = new JTextField(columnValue);
                    textField.setEditable(false);
                    textField.setEnabled(false);
                    panelStudentInfo.add(textField);
                }
            }

            panelStudentInfo.revalidate();
            panelStudentInfo.repaint();

            resultSet.close();
        } catch (SQLException e) {
            handleSQLException(e);
        }

        listMenu.setVisible(false);
    }

    @Override//att
    public void populateTabledFromDatabase(Integer studentID) throws SQLException {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = populateTabledFromDatabaseQuery();
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setInt(1, studentID);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        panelAtta.removeAll();
                        panelAtta.setLayout(new BoxLayout(panelAtta, BoxLayout.Y_AXIS));

                        while (resultSet.next()) {
                            final int courseId = resultSet.getInt("CourseID");
                            final String courseName = resultSet.getString("CourseName");

                            JButton courseButton = createButton(courseName, 150, 30);

                            courseButton.addActionListener(e -> {
                                if (courseButton.getText().contains(courseName)) {
                                    try {
                                        displayAttendanceForCourse(studentID, courseId);
                                        removeAttaCourseButtons();
                                    } catch (SQLException exception) {
                                        handleSQLException(exception);
                                    }
                                    courseButton.setText("Back");
                                    removeGradesCourseButtons();
                                    panelAtta.add(courseButton);
                                } else {
                                    showAllAttaButtons(studentID);
                                }
                            });

                            panelAtta.add(courseButton);
                        }

                        panelAtta.revalidate();
                        panelAtta.repaint();
                    } catch (SQLException e) {
                        handleSQLException(e);
                    }
                } catch (SQLException e) {
                    handleSQLException(e);
                }
                return null;
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                });
            }
        };

        worker.execute();
    }

    public void displayAttendanceForCourse(int studentID, int courseID) throws SQLException {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = displayAttendanceForCourseQuery();

                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setInt(1, studentID);
                    preparedStatement.setInt(2, courseID);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            StringBuilder attendanceInfo = new StringBuilder("<html>");
                            for (int i = 1; i <= 14; i++) {
                                attendanceInfo.append("<b style='font-family: JetBrains, Arial, sans-serif; font-size: 10px;'>W").append(i).append(":</b> ")
                                        .append(resultSet.getInt("W" + i)).append("<br>");
                            }
                            attendanceInfo.append("<b style='font-family: JetBrains, Arial, sans-serif; font-size: 10px;'>TotalAttendances:</b> ")
                                    .append(resultSet.getInt("TotalAttendance")).append("</html>");

                            JLabel attendanceLabel = new JLabel(attendanceInfo.toString());
                            panelAtta.add(attendanceLabel);
                            panelAtta.revalidate();
                            panelAtta.repaint();
                        }
                    }
                } catch (SQLException e) {
                    handleSQLException(e);
                }
                return null;
            }

            @Override
            protected void done() {
                super.done();
            }
        };

        worker.execute();
    }



    @Override//grades
    public void populateGradesFromDatabase(Integer studentID) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = populateGradesFromDatabaseQuery();
                panelGrades.removeAll();

                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setInt(1, studentID);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    panelGrades.removeAll();
                    panelGrades.setLayout(new BoxLayout(panelGrades, BoxLayout.Y_AXIS));

                    while (resultSet.next()) {
                        final int courseId = resultSet.getInt("CourseID");
                        final String courseName = resultSet.getString("CourseName");

                        JButton courseButton = createButton(courseName, 150, 30);
                        courseButton.addActionListener(e -> {
                            if (courseButton.getText().contains(courseName)) {
                                displayGradesForCourse(studentID, courseId);
                                removeGradesCourseButtons();
                                panelGrades.add(courseButton);
                            } else {
                                showAllGradesButtons(studentID);

                            }
                        });

                        panelGrades.add(courseButton);
                    }

                    panelGrades.revalidate();
                    panelGrades.repaint();
                } catch (SQLException e) {
                    handleSQLException(e);
                }
                return null;
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {

                });
            }
        };

        worker.execute();
    }

    public void displayGradesForCourse(int studentID, int courseId) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                removeAttaCourseButtons();

                String sql = displayGradesForCourseQuery();

                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setInt(1, studentID);
                    preparedStatement.setInt(2, courseId);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    try {
                        panelGrades.removeAll();
                        panelGrades.setLayout(new BoxLayout(panelGrades, BoxLayout.Y_AXIS));

                        while (resultSet.next()) {
                            int labGrade = resultSet.getInt("LabGrade");
                            int courseGrade = resultSet.getInt("CourseGrade");
                            int credits = resultSet.getInt("Credits");
                            int finalGrade = resultSet.getInt("FinalGrade");
                            int creditPoints = resultSet.getInt("CreditPoints");

                            String gradeDetails = "<html><b>Lab Grade:</b> " + labGrade +
                                    "<br><b>Course Grade:</b> " + courseGrade +
                                    "<br><b>Credits:</b> " + credits +
                                    "<br><b>Final Grade:</b> " + finalGrade +
                                    "<br><b>Credit Points:</b> " + creditPoints + "</html>";

                            JLabel gradesLabel = new JLabel(gradeDetails);

                            gradesLabel.setVerticalAlignment(JLabel.TOP);
                            JPanel containerPanel = new JPanel();
                            containerPanel.setLayout(new BorderLayout());

                            JButton newBackButton = createButton("Back", 60, 15);
                            newBackButton.addActionListener(e -> showAllGradesButtons(studentID));

                            containerPanel.setLayout(null);

                            containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

                            containerPanel.add(newBackButton);
                            containerPanel.add(gradesLabel);

                            panelGrades.add(containerPanel);

                            panelGrades.revalidate();
                            panelGrades.repaint();
                        }

                    } catch (SQLException e) {
                        Logger logger = Logger.getLogger(SQLQueries.class.getName());
                        logger.log(Level.SEVERE, "SQLException", e);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                });
            }
        };

        worker.execute();
    }






    public JButton createButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }

    private void removeGradesCourseButtons() {
        panelGrades.removeAll();
        panelGrades.revalidate();
        panelGrades.repaint();

    }
    private void removeAttaCourseButtons() {
        panelAtta.removeAll();
        panelAtta.revalidate();
        panelAtta.repaint();
    }


    private void showAllAttaButtons(int studentID) {
        panelAtta.removeAll();
        try {
            populateTabledFromDatabase(studentID);
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
            handleSQLException(e);
        }
    }
    private void showAllGradesButtons(int studentID) {
        panelGrades.removeAll();
        populateGradesFromDatabase(studentID);
    }
    private void handleSQLException(SQLException e) {
        Logger logger = Logger.getLogger(SQLQueries.class.getName());
        logger.log(Level.SEVERE, "SQLException", e);
    }



    @Override
    public void populateCoursesProfessorFromDatabase(String profID) {

    }

    @Override
    public void populateAttendancesProfessorFromDatabase(String profID) {
    }

    @Override
    public void populateStudentsFromDatabase(String profID) {

    }
    @Override
    public void populateInfoFromDatabase(String profID) {

    }
}