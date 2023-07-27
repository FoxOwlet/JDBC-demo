package com.foxowlet.jdbc;

import java.sql.*;

public class Main {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:");
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE foo(val INT, name VARCHAR(100))");
            insertData(connection);
            readData(statement);
        }
    }

    private static void readData(Statement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery("SELECT val, name FROM foo")) {
            while (resultSet.next()) {
                int val = resultSet.getInt(1);
                int val2 = resultSet.getInt("val");
                String name = resultSet.getString("name");
                System.out.printf("val=%d, val2=%d, name=%s%n", val, val2, name);
            }
        }
    }

    private static void insertData(Connection connection) throws SQLException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO foo(val, name) VALUES (?, ?)")) {
            for (int i = 1; i < 4; i++) {
                preparedStatement.setInt(1, i);
                preparedStatement.setString(2, "test" + i);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }
}
