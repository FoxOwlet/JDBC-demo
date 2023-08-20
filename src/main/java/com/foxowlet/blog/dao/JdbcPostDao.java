package com.foxowlet.blog.dao;

import com.foxowlet.blog.domain.Post;
import com.foxowlet.blog.domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPostDao implements PostDao {
    private final String jdbcUrl;

    public JdbcPostDao(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    @Override
    public void save(Post post) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = connection.prepareStatement("INSERT INTO posts(id, text, author, created_at) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, post.getId());
            ps.setString(2, post.getText());
            ps.setInt(3, post.getAuthor().getId());
            ps.setObject(4, post.getCreatedAt());
            ps.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Can't save post", e);
        }
    }

    @Override
    public List<Post> getAll() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("""
                     SELECT p.id, p.text, p.author, p.created_at, u.real_name, u.username
                     FROM posts p JOIN users u ON u.id = p.author
                     """)) {
            List<Post> posts = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("text");
                int author = rs.getInt("author");
                String realName = rs.getString("real_name");
                String username = rs.getString("username");
                LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
                Post post = new Post(id, text, new User(author, realName, username), createdAt);
                posts.add(post);
            }
            return posts;
        } catch (SQLException e) {
            throw new IllegalStateException("Can't get posts", e);
        }
    }

    @Override
    public Optional<Post> get(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = connection.prepareStatement("""
                     SELECT p.id, p.text, p.author, p.created_at, u.real_name, u.username
                     FROM posts p JOIN users u ON u.id = p.author
                     WHERE p.id = ?
                     """)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int postId = rs.getInt("id");
                String text = rs.getString("text");
                int author = rs.getInt("author");
                String realName = rs.getString("real_name");
                String username = rs.getString("username");
                LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
                Post post = new Post(postId, text, new User(author, realName, username), createdAt);
                return Optional.of(post);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Can't get post by id", e);
        }
    }

    @Override
    public void delete(Post post) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = connection.prepareStatement("DELETE FROM posts WHERE id = ?")) {
            ps.setInt(1, post.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Can't delete post by id", e);
        }
    }
}
