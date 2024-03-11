package org.ums;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseConnector {
    Connection getConnection() throws SQLException;

    ResultSet executeQuery(String sql, Object... parameters) throws SQLException;

    void reconnect() throws SQLException;

    public int executeUpdate(String sql) throws SQLException;
}
