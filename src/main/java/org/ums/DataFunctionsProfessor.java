package org.ums;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.*;

public class DataFunctionsProfessor implements DataGetter {
    private final DatabaseConnector databaseConnector;
    private final JPanel panelCourses;

    public DataFunctionsProfessor(
            DatabaseConnector databaseConnector,
            JPanel panelCourses) {
        this.databaseConnector = databaseConnector;
        this.panelCourses = panelCourses;
    }
    public void handleButtonClick(String profID, String buttonType) {
        removeOtherCourseButtons();
        switch (buttonType) {
            case "Grades":
                populateCoursesProfessorFromDatabase(profID);
                break;
            case "Attendance":
                populateAttendancesProfessorFromDatabase(profID);
                break;
        }
    }
    @Override
    public void populateTableFromDatabase(Integer studentID) {

    }

    @Override
    public void populateTabledFromDatabase(Integer studentID) {
    }

    @Override
    public void populateGradesFromDatabase(Integer profID) {

    }

    @Override
    public void populateCoursesProfessorFromDatabase(String profID) {
        removeOtherCourseButtons();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = "SELECT CourseID, CourseName FROM Courses WHERE ProfessorID = ?";
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setString(1, profID);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        panelCourses.removeAll();
                        panelCourses.setLayout(new BoxLayout(panelCourses, BoxLayout.Y_AXIS));

                        while (resultSet.next()) {
                            resultSet.getInt("CourseID");
                            final String courseName = resultSet.getString("CourseName");

                            JButton courseButton = createButton(courseName, 150, 30);
                            courseButton.addActionListener(e -> {
                                if (courseButton.getText().contains(courseName)) {
                                    displayGradesForCourse(profID, courseName);
                                } else {
                                    showAllCoursesButtons(profID);
                                }
                            });

                            panelCourses.add(courseButton);
                        }

                        panelCourses.revalidate();
                        panelCourses.repaint();
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
                SwingUtilities.invokeLater(super::done);
            }
        };

        worker.execute();
    }
    public void displayGradesForCourse(String profID, String courseName) {
        removeOtherCourseButtons();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = displayGradesForCourseTeacherQuery();
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setString(1, profID);
                    preparedStatement.setString(2, courseName);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        DefaultTableModel tableModel = new DefaultTableModel();
                        tableModel.addColumn("Name");
                        tableModel.addColumn("Lab Grade");
                        tableModel.addColumn("Course Grade");
                        tableModel.addColumn("Final Grade");
                        tableModel.addColumn("Credits");
                        tableModel.addColumn("Total Credits");

                        while (resultSet.next()) {
                            String name = resultSet.getString("Student Name");
                            int labGrade = resultSet.getInt("LabGrade");
                            int courseGrade = resultSet.getInt("CourseGrade");
                            int finalGrade = resultSet.getInt("FinalGrade");
                            int credits = resultSet.getInt("Credits");
                            int creditPoints = resultSet.getInt("CreditPoints");
                            tableModel.addRow(new Object[]{name, labGrade, courseGrade, finalGrade, credits, creditPoints});
                        }

                        JTable newTable = new JTable(tableModel);
                        newTable.setShowGrid(true);
                        newTable.setShowVerticalLines(true);
                        newTable.setGridColor(Color.GRAY);
                        JScrollPane newScrollPane = new JScrollPane(newTable);

                        int minColumnSize = 10;
                        int maxColumnSize = 200;

                        for (int i = 0; i < newTable.getColumnCount(); i++) {
                            TableColumn column = newTable.getColumnModel().getColumn(i);
                            column.setMinWidth(minColumnSize);
                            column.setMaxWidth(maxColumnSize);
                        }

                        JButton newBackButton = createButton("Back", 10, 15);
                        newBackButton.addActionListener(e -> showAllCoursesButtons(profID));

                        panelCourses.removeAll();
                        panelCourses.setLayout(new GridBagLayout());

                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.gridx = 0;
                        gbc.gridy = 0;
                        gbc.anchor = GridBagConstraints.NORTHWEST;

                        panelCourses.add(newBackButton, gbc);

                        gbc.gridx = 0;
                        gbc.gridy = 1;
                        gbc.fill = GridBagConstraints.BOTH;
                        gbc.weightx = 1.0;
                        gbc.weighty = 1.0;

                        panelCourses.add(newScrollPane, gbc);

                        panelCourses.revalidate();
                        panelCourses.repaint();
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
                SwingUtilities.invokeLater(super::done);
            }
        };

        worker.execute();
    }
    @Override
    public void populateAttendancesProfessorFromDatabase(String profID) {
        removeOtherCourseButtons();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = populateAttendancesProfessorFromDatabaseQuery();
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setString(1, profID);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        panelCourses.removeAll();
                        panelCourses.setLayout(new BoxLayout(panelCourses, BoxLayout.Y_AXIS));

                        while (resultSet.next()) {
                            final int courseId = resultSet.getInt("CourseID");
                            final String courseName = resultSet.getString("CourseName");

                            JButton courseButton = createButton(courseName, 150, 30);
                            courseButton.addActionListener(e -> {
                                if (courseButton.getText().contains(courseName)) {
                                    displayAttendanceForCourse(profID, courseId);
                                } else {
                                    showAllAttaButtons(profID);
                                }
                            });

                            panelCourses.add(courseButton);
                        }

                        panelCourses.revalidate();
                        panelCourses.repaint();
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
                SwingUtilities.invokeLater(super::done);
            }
        };

        worker.execute();
    }
    public void displayAttendanceForCourse(String profID, int courseID) {
        removeOtherCourseButtons();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = displayAttendanceForCourseTeacherQuery();
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setString(1, profID);
                    preparedStatement.setInt(2, courseID);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        DefaultTableModel tableModel = new DefaultTableModel();
                        tableModel.addColumn("AttendanceID");
                        tableModel.addColumn("Student Name");
                        tableModel.addColumn("Course ID");

                        for (int i = 1; i <= 14; i++) {
                            tableModel.addColumn("W" + i);
                        }

                        tableModel.addColumn("Total Attendance");

                        while (resultSet.next()) {
                            Object[] rowData = new Object[18];
                            rowData[0] = resultSet.getInt("AttendanceID");
                            rowData[1] = resultSet.getString("FullName");
                            rowData[2] = resultSet.getInt("CourseID");

                            for (int i = 1; i <= 14; i++) {
                                rowData[i + 2] = resultSet.getInt("W" + i);
                            }

                            rowData[17] = resultSet.getInt("TotalAttendance");

                            tableModel.addRow(rowData);
                        }

                        JTable newTable = new JTable(tableModel);
                        newTable.setShowGrid(true);
                        newTable.setShowVerticalLines(true);
                        newTable.setGridColor(Color.GRAY);
                        JScrollPane newScrollPane = new JScrollPane(newTable);

                        int minColumnSize = 20;
                        int maxColumnSize = 100;

                        for (int i = 0; i < newTable.getColumnCount(); i++) {
                            TableColumn column = newTable.getColumnModel().getColumn(i);
                            column.setMinWidth(minColumnSize);
                            column.setMaxWidth(maxColumnSize);
                        }

                        JButton newBackButton = createButton("Back", 10, 15);
                        newBackButton.addActionListener(e -> showAllAttaButtons(profID));

                        panelCourses.removeAll();
                        panelCourses.setLayout(new GridBagLayout());

                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.gridx = 0;
                        gbc.gridy = 0;
                        gbc.anchor = GridBagConstraints.NORTHWEST;

                        panelCourses.add(newBackButton, gbc);

                        gbc.gridx = 0;
                        gbc.gridy = 1;
                        gbc.fill = GridBagConstraints.BOTH;
                        gbc.weightx = 1.0;
                        gbc.weighty = 1.0;

                        panelCourses.add(newScrollPane, gbc);

                        panelCourses.revalidate();
                        panelCourses.repaint();
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
                SwingUtilities.invokeLater(super::done);
            }
        };

        worker.execute();
    }
    @Override
    public void populateStudentsFromDatabase(String profID) {
        removeOtherCourseButtons();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = populateStudentsFromDatabaseQuery();
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setString(1, profID);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        panelCourses.removeAll();
                        panelCourses.setLayout(new BoxLayout(panelCourses, BoxLayout.Y_AXIS));

                        while (resultSet.next()) {
                            final int courseID = resultSet.getInt("CourseID");
                            final String courseName = resultSet.getString("CourseName");

                            JButton courseButton = createButton("Course: " + courseName, 150, 30);
                            courseButton.addActionListener(e -> {
                                if (courseButton.getText().startsWith("Course: ")) {
                                    displayStudentNamesForCourse(profID, courseID);
                                } else {
                                    showAllStudentsButtons(profID);
                                }
                            });

                            panelCourses.add(courseButton);
                        }

                        panelCourses.revalidate();
                        panelCourses.repaint();
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
                SwingUtilities.invokeLater(super::done);
            }
        };

        worker.execute();
    }


    public void displayStudentNamesForCourse(String profID, int courseID) {
        removeOtherCourseButtons();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = displayStudentNamesForCourseQuery();
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setInt(1, courseID);
                    preparedStatement.setString(2, profID);
                    preparedStatement.setInt(3, courseID);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        DefaultTableModel tableModel = new DefaultTableModel();
                        tableModel.addColumn("Student ID");
                        tableModel.addColumn("Student Name");

                        while (resultSet.next()) {
                            int studentID = resultSet.getInt("StudentID");
                            String studentName = resultSet.getString("StudentName");
                            tableModel.addRow(new Object[]{studentID, studentName});
                        }

                        JTable newTable = new JTable(tableModel);
                        newTable.setShowGrid(true);
                        newTable.setShowVerticalLines(true);
                        newTable.setGridColor(Color.GRAY);
                        JScrollPane newScrollPane = new JScrollPane(newTable);

                        int minColumnSize = 20;
                        int maxColumnSize = 400;

                        for (int i = 0; i < newTable.getColumnCount(); i++) {
                            TableColumn column = newTable.getColumnModel().getColumn(i);
                            column.setMinWidth(minColumnSize);
                            column.setMaxWidth(maxColumnSize);
                        }

                        JButton newBackButton = createButton("Back", 10, 15);
                        newBackButton.addActionListener(e -> showAllStudentsButtons(profID));

                        panelCourses.removeAll();
                        panelCourses.setLayout(new GridBagLayout());

                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.gridx = 0;
                        gbc.gridy = 0;
                        gbc.anchor = GridBagConstraints.NORTHWEST;

                        panelCourses.add(newBackButton, gbc);

                        gbc.gridx = 0;
                        gbc.gridy = 1;
                        gbc.fill = GridBagConstraints.BOTH;
                        gbc.weightx = 1.0;
                        gbc.weighty = 1.0;

                        panelCourses.add(newScrollPane, gbc);

                        panelCourses.revalidate();
                        panelCourses.repaint();
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
                SwingUtilities.invokeLater(super::done);
            }
        };

        worker.execute();
    }
    @Override
    public void populateInfoFromDatabase(String profID) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                String sql = populateInfoFromDatabaseQuery();
                try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                    preparedStatement.setString(1, profID);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        panelCourses.removeAll();
                        panelCourses.setLayout(new GridLayout(0, 2));

                        if (resultSet.next()) {
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            for (int i = 1; i <= columnCount; i++) {
                                String columnName = metaData.getColumnName(i);
                                String columnValue = resultSet.getString(i);

                                JLabel label = new JLabel(columnName);
                                panelCourses.add(label);

                                JTextField textField = new JTextField(columnValue);
                                textField.setEditable(false);
                                panelCourses.add(textField);
                            }
                        }

                        panelCourses.revalidate();
                        panelCourses.repaint();
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
                SwingUtilities.invokeLater(super::done);
            }
        };

        worker.execute();
    }
    public JButton createButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }

    private void removeOtherCourseButtons() {
        panelCourses.removeAll();
        panelCourses.revalidate();
        panelCourses.repaint();
    }


    private void showAllCoursesButtons(String profID) {
        panelCourses.removeAll();
        populateCoursesProfessorFromDatabase(profID);
    }
    private void showAllStudentsButtons(String profID) {
        panelCourses.removeAll();
        populateStudentsFromDatabase(profID);
    }
    private void showAllAttaButtons(String profID) {
        panelCourses.removeAll();
        populateAttendancesProfessorFromDatabase(profID);
    }
    private void handleSQLException(SQLException e) {
        Logger logger = Logger.getLogger(SQLQueries.class.getName());
        logger.log(Level.SEVERE, "SQLException", e);
        e.printStackTrace();
    }


}
