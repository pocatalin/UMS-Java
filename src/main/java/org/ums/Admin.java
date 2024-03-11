package org.ums;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ums.SQLQueries.getAdminIDQuery;

public record Admin(String adminID, String password) {

    public static Admin getAdminById(String adminID) {
        String sql = getAdminIDQuery();
        try (Connection connection = new SQLDBConnector().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, adminID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String password = resultSet.getString("AdminPassword");
                return new Admin(adminID, password);
            }
        } catch (SQLException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "SQLException", e);
        }

        return null;
    }
}
