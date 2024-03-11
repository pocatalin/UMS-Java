package org.ums;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.getProfessorIDQuery;

public record Professor(String professorId, String firstName, String lastName, String password) {
    public static Professor getProfessorById(String professorId) {
        String sql = getProfessorIDQuery();
        try (Connection connection = new SQLDBConnector().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, professorId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String password = resultSet.getString("ProfessorPassword");
                return new Professor(professorId, firstName, lastName, password);
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }

        return null;
    }
}