package ru.job4j.cars.repository;

import ru.job4j.cars.model.Post;

import java.util.Collection;
import java.util.Optional;

public interface PostRepository {

    Optional<Post> add(Post post);

    boolean delete(int id);

    boolean delete(Post post);

    Optional<Post> findById(int id);

    Collection<Post> findAll();

    Collection<Post> findByDescription(String key);
}
