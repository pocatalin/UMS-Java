package org.ums;

import javax.swing.plaf.PanelUI;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLQueries {
    private static final Properties sqlProperties = loadProperties();

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = SQLQueries.class.getClassLoader().getResourceAsStream("sql.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "Exception", e);
        }
        return properties;
    }


    public static String getUpdateProfessorPasswordQuery() {
        return sqlProperties.getProperty("updateProfessorPassword");
    }

    public static String getUpdateStudentPasswordQuery() {
        return sqlProperties.getProperty("updateStudentPassword");
    }
    public static String getAdminIDQuery()
    {
        return sqlProperties.getProperty("getAdminID");
    }
    public static String getProfessorIDQuery()
    {
        return sqlProperties.getProperty("getProfessorID");
    }
    public static String getStudentIDQuery()
    {
        return sqlProperties.getProperty("getStudentID");
    }

    public static String getFirstNameByIdQuery()
    {
        return sqlProperties.getProperty("getFirstNameById");
    }
    public static String populateTableFromDatabaseQuery()
    {
        return sqlProperties.getProperty("populateTableFromDatabase");
    }
    public static String populateTabledFromDatabaseQuery()
    {
        return sqlProperties.getProperty("populateTabledFromDatabase");
    }

    public static String displayGradesForCourseQuery()
    {
        return sqlProperties.getProperty("displayGradesForCourse");
    }
    public static String displayAttendanceForCourseQuery()
    {
        return sqlProperties.getProperty("displayAttendanceForCourse");
    }
    public static String populateGradesFromDatabaseQuery()
    {
        return sqlProperties.getProperty("populateGradesFromDatabase");
    }
    public static String populateCoursesProfessorFromDatabaseQuery()
    {
        return sqlProperties.getProperty("populateCoursesProfessorFromDatabase");
    }
    public static String displayGradesForCourseTeacherQuery()
    {
        return sqlProperties.getProperty("displayGradesForCourseTeacher");
    }
    public static String populateAttendancesProfessorFromDatabaseQuery()
    {
        return sqlProperties.getProperty("populateAttendancesProfessorFromDatabase");
    }
    public static String displayAttendanceForCourseTeacherQuery()
    {
        return sqlProperties.getProperty("displayAttendanceForCourseTeacher");
    }
    public static String populateStudentsFromDatabaseQuery()
    {
        return sqlProperties.getProperty("populateStudentsFromDatabase");
    }
    public static String displayStudentNamesForCourseQuery()
    {
        return sqlProperties.getProperty("displayStudentNamesForCourse");
    }
    public static String populateInfoFromDatabaseQuery()
    {
        return sqlProperties.getProperty("populateInfoFromDatabase");
    }
    public static String getCourseIdByNameQuery()
    {
        return sqlProperties.getProperty("getCourseIdByName");
    }
    public static String fillCourseComboBoxQuery()
    {
        return sqlProperties.getProperty("fillCourseComboBox");
    }
    public static String getCourseIdByNameNQuery()
    {
        return sqlProperties.getProperty("getCourseIdByNameN");
    }
    public static String handleAttendanceInsertionQuery()
    {
        return sqlProperties.getProperty("handleAttendanceInsertion");
    }
    public static String insertDefaultAttendanceQuery()
    {
        return sqlProperties.getProperty("insertDefaultAttendance");
    }
    public static String getStudentIdByNameQuery()
    {
        return sqlProperties.getProperty("getStudentIdByName");
    }

    public static String fillStudentComboBoxQuery()
    {
        return sqlProperties.getProperty("fillStudentComboBox");
    }
    public static String fillGroupComboBoxQuery()
    {
        return sqlProperties.getProperty("fillGroupComboBox");
    }
    public static String fillProfessorComboBoxQuery()
    {
        return sqlProperties.getProperty("fillProfessorComboBox");
    }
    public static String getDepartmentIdByNameQuery()
    {
        return sqlProperties.getProperty("getDepartmentIdByName");
    }
    public static String getProfessorIdByNameQuery()
    {
        return sqlProperties.getProperty("getProfessorIdByName");
    }
    public static String fillDepartmentComboBoxQuery()
    {
        return sqlProperties.getProperty("fillDepartmentComboBox");
    }
    public static String getNextProfessorIDQuery()
    {
        return sqlProperties.getProperty("getNextProfessorID");
    }
    public static String insertProfessorDataQuery()
    {
        return sqlProperties.getProperty("insertProfessorData");
    }
    public static String fillFacultyComboBoxQuery()
    {
        return sqlProperties.getProperty("fillFacultyComboBox");
    }
    public static String getFacultyIdByNameQuery()
    {
        return sqlProperties.getProperty("getFacultyIdByName");
    }
    public static String fillDepartmentComboBoxBossQuery()
    {
        return sqlProperties.getProperty("fillDepartmentComboBoxBoss");
    }
}