package com.foxowlet.blog.dao;

import com.foxowlet.blog.domain.Post;
import com.foxowlet.blog.domain.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcPostDaoTest {
    private static final String JDBC_URL = "jdbc:h2:mem:testdb;db_close_delay=-1;init=runscript from 'src/test/resources/schema.sql'";
    private static LocalDateTime postTime = LocalDateTime.now();
    private JdbcPostDao dao = new JdbcPostDao(JDBC_URL);

    @BeforeAll
    static void addUsers() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO users(id, real_name, username) VALUES (1, 'user1', 'test1')");
            statement.execute("INSERT INTO users(id, real_name, username) VALUES (2, 'user2', 'test2')");
        }
    }
    @AfterAll
    static void removeUsers() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE users");
        }
    }

    @AfterEach
    void cleanupDB() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE posts");
        }
    }

    @Test
    void getAll_shouldReturnSavedPosts() {
        List<Post> posts = List.of(
                new Post(1, "text 1", new User(1, "user1", "test1"), postTime),
                new Post(2, "text 2", new User(2, "user2", "test2"), postTime)
        );

        for (Post post : posts) {
            dao.save(post);
        }
        List<Post> actual = dao.getAll();

        assertEquals(posts, actual);
    }

    @Test
    void get_shouldReturnSavedPost() {
        Post post = new Post(1, "text 1", new User(1, "user1", "test1"), postTime);

        dao.save(post);
        Optional<Post> actual = dao.get(post.getId());

        assertTrue(actual.isPresent());
        assertEquals(post, actual.get());
    }

    @Test
    void get_shouldReturnEmptyOptional_whenNoPostFound() {
        Optional<Post> post = dao.get(1);

        assertFalse(post.isPresent());
    }

    @Test
    void delete_shouldDeleteSavedPost() {
        Post post = new Post(1, "text 1", new User(1, "user1", "test1"), postTime);
        dao.save(post);

        dao.delete(post);

        Optional<Post> actual = dao.get(post.getId());
        assertFalse(actual.isPresent());
    }
}