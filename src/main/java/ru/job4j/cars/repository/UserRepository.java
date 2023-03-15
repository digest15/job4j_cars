package ru.job4j.cars.repository;

import ru.job4j.cars.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    Optional<User> add(User user);

    boolean delete(int id);

    boolean delete(User user);

    Optional<User> findById(int id);

    Optional<User> findByLoginAndPassword(String login, String password);

    Collection<User> findAll();
}
