package com.foxowlet.blog.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T> {
    void save(T entity);

    List<T> getAll();

    Optional<T> get(int id);

    void delete(T entity);
}
