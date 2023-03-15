package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepositoryHbn implements UserRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<User> add(User user) {
        return Optional.ofNullable(
                crudRepository.tx(session -> {
                    session.save(user);
                    return user;
                })
        );
    }

    @Override
    public boolean delete(int id) {
        return crudRepository.run(
                "DELETE User WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public boolean delete(User user) {
        return crudRepository.run(session -> {
                    session.delete(user);
                    return true;
                }
        );
    }

    @Override
    public Optional<User> findById(int id) {
        return crudRepository.optional(
                "from User where id = :id",
                User.class,
                Map.of("id", id)
        );
    }

    @Override
    public Optional<User> findByLoginAndPassword(String login, String password) {
        return crudRepository.optional(
                "from User where login = :login and password = :password",
                User.class,
                Map.of("login", login, "password", password)
        );
    }

    @Override
    public Collection<User> findAll() {
        return crudRepository.query(
                "from User",
                User.class
        );
    }
}
