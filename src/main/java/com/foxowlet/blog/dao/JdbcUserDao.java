package com.foxowlet.blog.dao;

import com.foxowlet.blog.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDao implements UserDao {
    private final String jdbcUrl;

    public JdbcUserDao(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    @Override
    public void save(User user) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = connection.prepareStatement("INSERT INTO users(id, real_name, username) VALUES (?, ?, ?)")) {
            ps.setInt(1, user.getId());
            ps.setString(2, user.getRealName());
            ps.setString(3, user.getUsername());
            ps.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Can't save user", e);
        }
    }

    @Override
    public List<User> getAll() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT id, real_name, username FROM users")) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String realName = rs.getString("real_name");
                String username = rs.getString("username");
                User user = new User(id, realName, username);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new IllegalStateException("Can't get users", e);
        }
    }

    @Override
    public Optional<User> get(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = connection.prepareStatement("SELECT id, real_name, username FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String realName = rs.getString("real_name");
                String username = rs.getString("username");
                User user = new User(userId, realName, username);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Can't get user by id", e);
        }
    }

    @Override
    public void delete(User user) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, user.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Can't delete user by id", e);
        }
    }
}
