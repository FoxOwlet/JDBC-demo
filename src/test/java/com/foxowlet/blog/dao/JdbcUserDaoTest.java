package com.foxowlet.blog.dao;

import com.foxowlet.blog.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcUserDaoTest {
    public static final String JDBC_URL = "jdbc:h2:mem:testdb;init=runscript from 'src/test/resources/schema.sql';db_close_delay=-1";
    private final JdbcUserDao dao = new JdbcUserDao(JDBC_URL);

    @AfterEach
    void cleanupDB() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE users");
        }
    }

    @Test
    void getAll_shouldReturnSavedUsers() {
        List<User> users = List.of(
                new User(1, "user1", "test1"),
                new User(2, "user2", "test2"));

        for (User user : users) {
            dao.save(user);
        }
        List<User> actual = dao.getAll();

        assertEquals(users, actual);
    }

    @Test
    void get_shouldReturnSavedUser_whenPresentInDB() {
        User user = new User(1, "user", "test");
        dao.save(user);

        Optional<User> actual = dao.get(user.getId());

        assertTrue(actual.isPresent());
        assertEquals(user, actual.get());
    }

    @Test
    void get_shouldReturnEmptyOptional_whenNoUserFound() {
        Optional<User> actual = dao.get(1);

        assertFalse(actual.isPresent());
    }

    @Test
    void delete_shouldDeleteSavedUser() {
        User user = new User(1, "test", "user");
        dao.save(user);

        dao.delete(user);

        Optional<User> actual = dao.get(user.getId());
        assertFalse(actual.isPresent());
    }
}