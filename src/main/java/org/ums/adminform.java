package org.ums;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.*;

public class adminform extends JFrame {
    protected final DatabaseConnector databaseConnector;
    private JPanel panelSelectRow;

    public adminform() {
        initComponents();
        initComponents();
        initComponents();
        databaseConnector = new SQLDBConnector();

        try {
            refreshAllTables();
        } catch (SQLException e) {
            handleSQLException(e);
        }

        btAddAttedance.addActionListener(e -> handleAttendanceInsertion());

        btAddGroup.addActionListener(e -> handelebtAddGroups());

        btAddFaculties.addActionListener(e -> handelebtAddFaculties());

        btAddDepartments.addActionListener(e -> handelebtAddDepartments());

        btAddStudentCourses.addActionListener(e -> SwingUtilities.invokeLater(() -> new studentcourseForm(adminform.this)));
        btAddCourse.addActionListener(e -> SwingUtilities.invokeLater(() -> new addcourseForm(adminform.this)));
        btAddProfessor.addActionListener(e -> SwingUtilities.invokeLater(() -> new addprofessorForm(adminform.this)));
        btAddStudent.addActionListener(e -> SwingUtilities.invokeLater(() -> new addstudentForm(adminform.this)));
        btSelectRow.addActionListener(e -> handleSelectRow());
        btSaveChanges.addActionListener(e -> saveDataToDB());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(tabbedPane1);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);



        tabbedPane2.addChangeListener(e -> updateButtonState(tabbedPane2.getSelectedIndex()));
    }

    private void handleSQLException(SQLException e) {
        Logger logger = Logger.getLogger(SQLQueries.class.getName());
        logger.log(Level.SEVERE, "SQLException", e);
    }

    private void handleSelectRow() {
        int selectedTabIndex = tabbedPane2.getSelectedIndex();

        switch (selectedTabIndex) {
            case 0:
                displayRowData(tableStudents);
                break;
            case 1:
                displayRowData(tableProfessors);
                break;
            case 2:
                displayRowData(tableProfessorCourses);
                break;
            case 3:
                displayRowData(tableProfessorCourseStudents);
                break;
            case 4:
                displayRowData(tableCourses);
                break;
            case 5:
                displayRowData(tableStudentCourses);
                break;
            case 6:
                displayRowData(tableGrades);
                break;
            case 7:
                displayRowData(tableAttendance);
                break;
            case 8:
                displayRowData(tableStudentGroup);
                break;
            case 9:
                displayRowData(tableYearTable);
                break;
            case 10:
                displayRowData(tableStudentYear);
                break;
            case 11:
                displayRowData(tableDepartments);
                break;
            case 12:
                displayRowData(tableStudentDepartments);
                break;
            case 13:
                displayRowData(tableFaculties);
                break;
            case 14:
                displayRowData(tableGroups);
                break;
            default:
                showMessage("Invalid Tab Index");
                break;
        }
    }
    private void displayRowData(JTable table) {
        int selectedRowIndex = table.getSelectedRow();

        if (selectedRowIndex != -1) {
            Object[] rowData = new Object[table.getColumnCount()];
            for (int i = 0; i < table.getColumnCount(); i++) {
                rowData[i] = table.getValueAt(selectedRowIndex, i);
            }

            JTextField[] textFields = new JTextField[table.getColumnCount()];
            for (int i = 0; i < textFields.length; i++) {
                textFields[i] = new JTextField(rowData[i].toString());
            }

            JPanel panel = new JPanel(new GridLayout(table.getColumnCount(), 2));
            for (int i = 0; i < textFields.length; i++) {
                panel.add(new JLabel(table.getColumnName(i)));
                panel.add(textFields[i]);
            }

            int option = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Selected Row Data",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                for (int i = 0; i < textFields.length; i++) {
                    table.setValueAt(textFields[i].getText(), selectedRowIndex, i);
                }
                try {
                    refreshAllTables();
                } catch (SQLException e) {
                    handleSQLException(e);
                }
            }
        } else {
            showMessage("No row selected");
        }
    }



    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }



    private void saveDataToDB() {
        int selectedTabIndex = tabbedPane2.getSelectedIndex();
        updateButtonState(selectedTabIndex);
        switch (selectedTabIndex) {
            case 0:
                saveTableToDB(tableStudents, "Students");
                break;
            case 1:
                saveTableToDB(tableProfessors, "Professors");
                break;
            case 2:
                //saveTableToDB(tableProfessorCourses, "ProfessorCourses");
                break;
            case 3:
                //saveTableToDB(tableProfessorCourseStudents, "ProfessorCourseStudents");
                break;
            case 4:
                saveTableToDB(tableCourses, "Courses");
                break;
            case 5:
                //saveTableToDB(tableStudentCourses, "StudentCourses");
                break;
            case 6:
                saveTableToDB(tableGrades, "Grades");
                break;
            case 7:
                saveTableToDB(tableAttendance, "Attendance");
                break;
            case 8:
                //saveTableToDB(tableStudentGroup, "StudentGroup");
                break;
            case 9:
                saveTableToDB(tableYearTable, "YearTable");
                break;
            case 10:
                saveTableToDB(tableStudentYear, "StudentYear");
                break;
            case 11:
                saveTableToDB(tableDepartments, "Departments");
                break;
            case 12:
                saveTableToDB(tableStudentDepartments, "StudentDepartment");
                break;
            case 13:
                saveTableToDB(tableFaculties, "Faculties");
                break;
            case 14:
                saveTableToDB(tableGroups, "Groups");
                break;
            default:
                showMessage("Invalid Tab Index");
                break;
        }
    }
    private void updateButtonState(int selectedTabIndex) {
        switch (selectedTabIndex) {
            case 0:
            case 1:
            case 4:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                enableButtons();
                break;
            case 2:
            case 3:
            case 5:
            case 8:
                disableButtons();
                break;
            default:
                enableButtons();
                showMessage("Invalid Tab Index");
                break;
        }
    }

    private void enableButtons() {
        btSelectRow.setEnabled(true);
        btSaveChanges.setEnabled(true);
    }

    private void disableButtons() {
        btSelectRow.setEnabled(false);
        btSaveChanges.setEnabled(false);
    }

    private void saveTableToDB(JTable table, String tableName) {
        int rowCount = table.getRowCount();
        int colCount = table.getColumnCount();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    DatabaseConnector connection = new SQLDBConnector();
                    StringBuilder updateStatement = new StringBuilder("UPDATE " + tableName + " SET ");

                    for (int j = 1; j < colCount; j++) {
                        String columnName = table.getColumnName(j);

                        if (!columnName.equals("Age") && !columnName.equals("TotalCredits") && !columnName.equals("FinalGrade") && !columnName.equals("Credits") && !columnName.equals("CreditPoints")) {
                            updateStatement.append(columnName).append(" = ?, ");
                        }
                    }

                    if (updateStatement.toString().endsWith(", ")) {
                        updateStatement = new StringBuilder(updateStatement.substring(0, updateStatement.length() - 2));
                    }

                    updateStatement.append(" WHERE ").append(table.getColumnName(0)).append(" = ?");

                    try (PreparedStatement preparedStatement = connection.getConnection().prepareStatement(updateStatement.toString())) {
                        for (int i = 0; i < rowCount; i++) {
                            int parameterIndex = 1;

                            for (int j = 1; j < colCount; j++) {
                                String columnName = table.getColumnName(j);

                                if (!columnName.equals("Age") && !columnName.equals("TotalCredits") && !columnName.equals("FinalGrade") && !columnName.equals("Credits") && !columnName.equals("CreditPoints")) {
                                    Object columnValue = table.getValueAt(i, j);
                                    preparedStatement.setObject(parameterIndex, columnValue);
                                    parameterIndex++;
                                }
                            }
                            try {
                                refreshAllTables();
                            } catch (SQLException e) {
                                handleSQLException(e);
                            }

                            Object primaryKeyValue = table.getValueAt(i, 0);
                            preparedStatement.setObject(parameterIndex, primaryKeyValue);

                            preparedStatement.executeUpdate();
                        }

                    }
                } catch (SQLException e) {
                    Logger logger = Logger.getLogger(SQLQueries.class.getName());
                    logger.log(Level.SEVERE, "Error updating database", e);
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








    public void refreshTable(JTable table, String tableName) {
        try {
            if (!databaseConnector.getConnection().isValid(1)) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                databaseConnector.reconnect();
            }

            fillTable(table, tableName);
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }

    public void refreshAllTables() throws SQLException {
        fillTable(tableStudents, "Students");
        fillTable(tableStudentCourses, "StudentCourses");
        fillTable(tableStudentDepartments, "StudentDepartment");
        fillTable(tableStudentGroup, "StudentGroup");
        fillTable(tableStudentYear, "StudentYear");
        fillTable(tableProfessors, "Professors");
        fillTable(tableProfessorCourses, "ProfessorCourses");
        fillTable(tableProfessorCourseStudents, "ProfessorCourseStudents");
        fillTable(tableCourses, "Courses");
        fillTable(tableGrades, "Grades");
        fillTable(tableAttendance, "Attendance");
        fillTable(tableYearTable, "YearTable");
        fillTable(tableDepartments, "Departments");
        fillTable(tableGroups, "Groups");
        fillTable(tableFaculties, "Faculties");
    }
    private void handelebtAddGroups(){
        try {
            int nextGroupNumber = getNextGroupNumber();

            String insertSql = "INSERT INTO Groups (GroupNumber) VALUES (?)";
            try (PreparedStatement insertStatement = databaseConnector.getConnection().prepareStatement(insertSql)) {
                insertStatement.setInt(1, nextGroupNumber);
                insertStatement.executeUpdate();
            }

        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }
    private void handelebtAddFaculties(){
        String facultyName = JOptionPane.showInputDialog("Enter the name of the faculty:");

        if (facultyName != null && !facultyName.trim().isEmpty()) {
            try {
                String insertSql = "INSERT INTO Faculties (FacultyName) VALUES (?)";
                try (PreparedStatement insertStatement = databaseConnector.getConnection().prepareStatement(insertSql)) {
                    insertStatement.setString(1, facultyName);
                    insertStatement.executeUpdate();
                }

                refreshTable(tableFaculties, "Faculties");

                JOptionPane.showMessageDialog(null, "Added Faculty: " + facultyName, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                Logger logger = Logger.getLogger(SQLQueries.class.getName());
                logger.log(Level.SEVERE, "SQLException", e);
            }
        }

    }
    private void handelebtAddDepartments()
    {
        JTextField departmentNameField = new JTextField();
        JTextField facultyIdField = new JTextField();

        JComponent[] inputs = new JComponent[]{
                new JLabel("Department Name:"),
                departmentNameField,
                new JLabel("Faculty ID:"),
                facultyIdField
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Department Information", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String departmentName = departmentNameField.getText().trim();
                int facultyId;

                try {
                    facultyId = Integer.parseInt(facultyIdField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Faculty ID. Please enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String insertSql = "INSERT INTO Departments (DepartmentName, FacultyID) VALUES (?, ?)";
                try (PreparedStatement insertStatement = databaseConnector.getConnection().prepareStatement(insertSql)) {
                    insertStatement.setString(1, departmentName);
                    insertStatement.setInt(2, facultyId);
                    insertStatement.executeUpdate();
                }
                refreshTable(tableDepartments, "Departments");
                JOptionPane.showMessageDialog(null, "Added Department: " + departmentName, "Confirmation", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                Logger logger = Logger.getLogger(SQLQueries.class.getName());
                logger.log(Level.SEVERE, "SQLException", e);
            }
        }
    }

    int getNextGroupNumber() throws SQLException {
        String sql = getCourseIdByNameNQuery();

        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int maxGroupNumber = resultSet.getInt(1);
                int nextGroupNumber = maxGroupNumber + 1;
                fillTable(tableGroups, "Groups");

                String message = "Inserted Group Number: " + nextGroupNumber;
                JOptionPane.showMessageDialog(this, message, "Group Insertion", JOptionPane.INFORMATION_MESSAGE);

                return nextGroupNumber;
            } else {
                return 1;
            }
        }
    }
    private void handleAttendanceInsertion() {
        try {
            String sql = handleAttendanceInsertionQuery();

            try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                int expectedRows = 0;
                int insertedRows = 0;
                StringBuilder existingRecords = new StringBuilder();

                while (resultSet.next()) {
                    int studentID = resultSet.getInt("StudentID");
                    int courseID = resultSet.getInt("CourseID");
                    expectedRows++;

                    if (insertDefaultAttendance(studentID, courseID)) {
                        insertedRows++;
                    } else {
                        existingRecords.append("Record for StudentID: ").append(studentID)
                                .append(", CourseID: ").append(courseID).append(" already exists.\n");
                    }
                }

                String message1 = "Inserted " + insertedRows + " out of " + expectedRows + " rows.";

                if (!existingRecords.isEmpty()) {
                    String message2 = "Existing Records:\n" + existingRecords;
                    JOptionPane.showMessageDialog(this, message2, "Existing Rows", JOptionPane.WARNING_MESSAGE);
                }

                JOptionPane.showMessageDialog(this, message1, "Attendance Insertion", JOptionPane.INFORMATION_MESSAGE);

                fillTable(tableAttendance, "Attendance");
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }
    }
    private boolean insertDefaultAttendance(int studentID, int courseID) throws SQLException {
        String checkIfExistsSql = insertDefaultAttendanceQuery();
        try (PreparedStatement checkIfExistsStatement = databaseConnector.getConnection().prepareStatement(checkIfExistsSql)) {
            checkIfExistsStatement.setInt(1, studentID);
            checkIfExistsStatement.setInt(2, courseID);
            ResultSet resultSet = checkIfExistsStatement.executeQuery();
            resultSet.next();
            int rowCount = resultSet.getInt(1);

            if (rowCount > 0) {
                return false;
            }
        }
        String insertSql = "INSERT INTO Attendance (StudentID, CourseID) VALUES (?, ?)";
        try (PreparedStatement insertStatement = databaseConnector.getConnection().prepareStatement(insertSql)) {
            insertStatement.setInt(1, studentID);
            insertStatement.setInt(2, courseID);
            insertStatement.executeUpdate();
            return true;
        }
    }
    public void fillTable(JTable table, String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            DefaultTableModel model = new DefaultTableModel();
            table.setModel(model);

            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                model.addColumn(resultSet.getMetaData().getColumnName(i));
            }

            while (resultSet.next()) {
                Object[] row = new Object[resultSet.getMetaData().getColumnCount()];
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                model.addRow(row);
            }

            TableColumnModel columnModel = table.getColumnModel();
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                TableColumn column = columnModel.getColumn(i);
                column.setMinWidth(1);
                column.setMaxWidth(100);
            }

            table.setShowGrid(true);
            table.setShowVerticalLines(true);
            table.setGridColor(Color.GRAY);
        }
    }




    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
        tabbedPane1 = new JTabbedPane();

        JPanel panel1 = new JPanel();
        JScrollPane scrollPane2 = new JScrollPane();
        tabbedPane2 = new JTabbedPane();
        JScrollPane scrollPaneStudents = new JScrollPane();
        tableStudents = new JTable();
        JScrollPane scrollPaneProfessors = new JScrollPane();
        tableProfessors = new JTable();
        JScrollPane scrollPaneProfessorCourses = new JScrollPane();
        tableProfessorCourses = new JTable();
        JScrollPane scrollPaneProfessorCoursesStudents = new JScrollPane();
        tableProfessorCourseStudents = new JTable();
        JScrollPane scrollPaneCourses = new JScrollPane();
        tableCourses = new JTable();
        JScrollPane scrollPaneStudentCourses = new JScrollPane();
        tableStudentCourses = new JTable();
        JScrollPane scrollPaneGrades = new JScrollPane();
        tableGrades = new JTable();
        JScrollPane scrollPaneAttendance = new JScrollPane();
        tableAttendance = new JTable();
        JScrollPane scrollPaneStudentGroup = new JScrollPane();
        tableStudentGroup = new JTable();
        JScrollPane scrollPaneYearTable = new JScrollPane();
        tableYearTable = new JTable();
        JScrollPane scrollPaneStudentYear = new JScrollPane();
        tableStudentYear = new JTable();
        JScrollPane scrollPaneDepartments = new JScrollPane();
        tableDepartments = new JTable();
        JScrollPane scrollPaneStudentDepartments = new JScrollPane();
        tableStudentDepartments = new JTable();
        JScrollPane scrollPaneFaculties = new JScrollPane();
        tableFaculties = new JTable();
        JScrollPane scrollPaneGroups = new JScrollPane();
        tableGroups = new JTable();
        btAddStudent = new JButton();
        btAddProfessor = new JButton();
        btAddCourse = new JButton();
        btAddStudentCourses = new JButton();
        btAddAttedance = new JButton();
        btAddDepartments = new JButton();
        btAddFaculties = new JButton();
        btAddGroup = new JButton();
        btSelectRow = new JButton();
        btSaveChanges = new JButton();

        //======== tabbedPane1 ========
        {
            tabbedPane1.setPreferredSize(new Dimension(1500, 700));

            //======== panel1 ========
            {
                panel1.setPreferredSize(new Dimension(1500, 700));
                panel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.
                EmptyBorder(0,0,0,0), "",javax.swing.border.TitledBorder.CENTER,javax.swing
                .border.TitledBorder.BOTTOM,new java.awt.Font("Dia\u006cog",java.awt.Font.BOLD,12),
                java.awt.Color.red),panel1. getBorder()));panel1. addPropertyChangeListener(new java.beans.PropertyChangeListener()
                {@Override public void propertyChange(java.beans.PropertyChangeEvent e){if("\u0062ord\u0065r".equals(e.getPropertyName()))
                throw new RuntimeException();}});

                //======== scrollPane2 ========
                {
                    scrollPane2.setPreferredSize(new Dimension(1500, 700));

                    //======== tabbedPane2 ========
                    {
                        tabbedPane2.setPreferredSize(new Dimension(1500, 700));

                        //======== scrollPaneStudents ========
                        {
                            scrollPaneStudents.setViewportView(tableStudents);
                        }
                        tabbedPane2.addTab("Students", scrollPaneStudents);

                        //======== scrollPaneProfessors ========
                        {
                            scrollPaneProfessors.setViewportView(tableProfessors);
                        }
                        tabbedPane2.addTab("Professors", scrollPaneProfessors);

                        //======== scrollPaneProfessorCourses ========
                        {
                            scrollPaneProfessorCourses.setViewportView(tableProfessorCourses);
                        }
                        tabbedPane2.addTab("Professors Courses", scrollPaneProfessorCourses);

                        //======== scrollPaneProfessorCoursesStudents ========
                        {
                            scrollPaneProfessorCoursesStudents.setViewportView(tableProfessorCourseStudents);
                        }
                        tabbedPane2.addTab("Professor Course Students", scrollPaneProfessorCoursesStudents);

                        //======== scrollPaneCourses ========
                        {
                            scrollPaneCourses.setViewportView(tableCourses);
                        }
                        tabbedPane2.addTab("Courses", scrollPaneCourses);

                        //======== scrollPaneStudentCourses ========
                        {
                            scrollPaneStudentCourses.setViewportView(tableStudentCourses);
                        }
                        tabbedPane2.addTab("Student Courses", scrollPaneStudentCourses);

                        //======== scrollPaneGrades ========
                        {
                            scrollPaneGrades.setViewportView(tableGrades);
                        }
                        tabbedPane2.addTab("Grades", scrollPaneGrades);

                        //======== scrollPaneAttendance ========
                        {
                            scrollPaneAttendance.setViewportView(tableAttendance);
                        }
                        tabbedPane2.addTab("Attendance", scrollPaneAttendance);

                        //======== scrollPaneStudentGroup ========
                        {
                            scrollPaneStudentGroup.setViewportView(tableStudentGroup);
                        }
                        tabbedPane2.addTab("Student Group", scrollPaneStudentGroup);

                        //======== scrollPaneYearTable ========
                        {
                            scrollPaneYearTable.setViewportView(tableYearTable);
                        }
                        tabbedPane2.addTab("Year Table", scrollPaneYearTable);

                        //======== scrollPaneStudentYear ========
                        {
                            scrollPaneStudentYear.setViewportView(tableStudentYear);
                        }
                        tabbedPane2.addTab("Student Year", scrollPaneStudentYear);

                        //======== scrollPaneDepartments ========
                        {
                            scrollPaneDepartments.setViewportView(tableDepartments);
                        }
                        tabbedPane2.addTab("Departments", scrollPaneDepartments);

                        //======== scrollPaneStudentDepartments ========
                        {
                            scrollPaneStudentDepartments.setViewportView(tableStudentDepartments);
                        }
                        tabbedPane2.addTab("Student Departments", scrollPaneStudentDepartments);

                        //======== scrollPaneFaculties ========
                        {
                            scrollPaneFaculties.setViewportView(tableFaculties);
                        }
                        tabbedPane2.addTab("Faculties", scrollPaneFaculties);

                        //======== scrollPaneGroups ========
                        {

                            //---- tableGroups ----
                            tableGroups.setPreferredScrollableViewportSize(new Dimension(600, 400));
                            scrollPaneGroups.setViewportView(tableGroups);
                        }
                        tabbedPane2.addTab("Groups", scrollPaneGroups);
                    }
                    scrollPane2.setViewportView(tabbedPane2);
                }

                //---- btAddStudent ----
                btAddStudent.setText("Add Student");

                //---- btAddProfessor ----
                btAddProfessor.setText("Add Professor");

                //---- btAddCourse ----
                btAddCourse.setText("Add Course");

                //---- btAddStudentCourses ----
                btAddStudentCourses.setText("Add Student Courses");

                //---- btAddAttedance ----
                btAddAttedance.setText("Add Attendance");

                //---- btAddDepartments ----
                btAddDepartments.setText("Add Departments");

                //---- btAddFaculties ----
                btAddFaculties.setText("Add Faculty");

                //---- btAddGroup ----
                btAddGroup.setText("Add Group");

                //---- btSelectRow ----
                btSelectRow.setText("Select Row");

                //---- btSaveChanges ----
                btSaveChanges.setText("Save Changes");

                GroupLayout panel1Layout = new GroupLayout(panel1);
                panel1.setLayout(panel1Layout);
                panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 1050, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createSequentialGroup()
                            .addGroup(panel1Layout.createParallelGroup()
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addComponent(btAddStudent, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btAddProfessor)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btAddCourse)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btAddStudentCourses)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btAddAttedance)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btAddFaculties)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btAddGroup)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btAddDepartments))
                                .addGroup(panel1Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(btSelectRow)
                                    .addGap(18, 18, 18)
                                    .addComponent(btSaveChanges)))
                            .addContainerGap(134, Short.MAX_VALUE))
                );
                panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btAddProfessor)
                                .addComponent(btAddStudent)
                                .addComponent(btAddCourse)
                                .addComponent(btAddStudentCourses)
                                .addComponent(btAddAttedance)
                                .addComponent(btAddFaculties)
                                .addComponent(btAddGroup)
                                .addComponent(btAddDepartments))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btSelectRow)
                                .addComponent(btSaveChanges))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 506, GroupLayout.PREFERRED_SIZE))
                );
            }
            tabbedPane1.addTab("All Tables", panel1);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Evaluation license - Pistol Ovidiu Catalin
    private JTabbedPane tabbedPane1;
    private JTabbedPane tabbedPane2;
    private JTable tableStudents;
    private JTable tableProfessors;
    private JTable tableProfessorCourses;
    private JTable tableProfessorCourseStudents;
    private JTable tableCourses;
    private JTable tableStudentCourses;
    private JTable tableGrades;
    private JTable tableAttendance;
    private JTable tableStudentGroup;
    private JTable tableYearTable;
    private JTable tableStudentYear;
    private JTable tableDepartments;
    private JTable tableStudentDepartments;
    private JTable tableFaculties;
    private JTable tableGroups;
    private JButton btAddStudent;
    private JButton btAddProfessor;
    private JButton btAddCourse;
    private JButton btAddStudentCourses;
    JButton btAddAttedance;
    private JButton btAddDepartments;
    private JButton btAddFaculties;
    private JButton btAddGroup;
    private JButton btSelectRow;
    private JButton btSaveChanges;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
