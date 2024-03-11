package org.ums;

import javax.swing.*;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLDBConnector implements DatabaseConnector {
    private static final String PROPERTIES_FILE = "db.properties";

    private String jdbcUrl;
    private String user;
    private String password;

    private Connection connection;
    private final Map<String, PreparedStatement> preparedStatementCache = new ConcurrentHashMap<>();

    public SQLDBConnector() {
        loadDatabaseProperties();
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            Logger logger = Logger.getLogger(SQLQueries.class.getName());
            logger.log(Level.SEVERE, "ClassNotFoundException", e);
        }
    }

    private void loadDatabaseProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            Properties properties = new Properties();
            if (input != null) {
                properties.load(input);
                jdbcUrl = properties.getProperty("jdbc.url");
                user = properties.getProperty("db.user");
                password = properties.getProperty("db.password");
            } else {
                Logger logger = Logger.getLogger(SQLDBConnector.class.getName());
                logger.log(Level.SEVERE, "Sorry, unable to find " + PROPERTIES_FILE);
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger(SQLDBConnector.class.getName());
            logger.log(Level.SEVERE, "Error loading database properties", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            reconnect();
        }
        return connection;
    }

    @Override
    public void reconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connection = DriverManager.getConnection(jdbcUrl, user, password);
    }

    @Override
    public ResultSet executeQuery(String sql, Object... parameters) throws SQLException {
        SwingWorker<ResultSet, Void> worker = new SwingWorker<>() {
            @Override
            protected ResultSet doInBackground() throws Exception {
                getConnection();
                PreparedStatement statement = getPreparedStatement(sql);

                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }

                return statement.executeQuery();
            }

            @Override
            protected void done() {
                try {
                    ResultSet resultSet = get();
                } catch (Exception e) {
                    Logger logger = Logger.getLogger(SQLQueries.class.getName());
                    logger.log(Level.SEVERE, "Error executing query", e);
                }
            }
        };

        worker.execute();
        return null;
    }


    @Override
    public int executeUpdate(String sql) throws SQLException {
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                Connection connection = null;
                PreparedStatement statement = null;

                try {
                    connection = getConnection();
                    statement = connection.prepareStatement(sql);

                    return statement.executeUpdate();
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    int updateCount = get();
                } catch (Exception e) {
                    Logger logger = Logger.getLogger(SQLQueries.class.getName());
                    logger.log(Level.SEVERE, "Error executing update", e);
                }
            }
        };

        worker.execute();
        return 0;
    }


    private PreparedStatement getPreparedStatement(String sql) {
        return preparedStatementCache.computeIfAbsent(sql, key -> {
            try {
                return connection.prepareStatement(key);
            } catch (SQLException e) {
                Logger logger = Logger.getLogger(SQLQueries.class.getName());
                logger.log(Level.SEVERE, "SQLException", e);
                return null;
            }
        });
    }
}
