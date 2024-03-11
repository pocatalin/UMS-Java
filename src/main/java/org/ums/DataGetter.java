package org.ums;

import java.sql.SQLException;

public interface DataGetter {

    void populateTableFromDatabase(Integer studentID) throws SQLException;

    void populateCoursesProfessorFromDatabase(String profID) throws SQLException;

    void populateTabledFromDatabase(Integer studentID) throws SQLException;

    void populateGradesFromDatabase(Integer studentID) throws SQLException;
    void populateAttendancesProfessorFromDatabase(String profID) throws SQLException;
    void populateStudentsFromDatabase(String profID) throws SQLException;

    void populateInfoFromDatabase(String profID) throws SQLException;

}
