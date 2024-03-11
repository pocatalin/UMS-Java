package org.ums;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.getFirstNameByIdQuery;
import static org.ums.SQLQueries.getStudentIDQuery;

public record Student(int id, String name, String password) {

    public static Student getStudentById(int studentId) {
        String sql = getStudentIDQuery();
        try (Connection connection = new SQLDBConnector().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("StudentID");
                String password = resultSet.getString("StudentPassword");
                return new Student(studentId, name, password);
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }

        return null;
    }

    public static String getFirstNameById(int studentId) {
        String sql = getFirstNameByIdQuery();
        try (Connection connection = new SQLDBConnector().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("FirstName");
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }

        return null;
    }

}
