package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserRepositoryHbnTest {

    private static UserRepository userRepository;

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void initRepository() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        CrudRepository crudRepository = new CrudRepository(sessionFactory);
        userRepository = new UserRepositoryHbn(crudRepository);
    }

    @AfterAll
    public static void clear() {
        sessionFactory.close();
    }

    @AfterEach
    public void clearRepository() {
        userRepository.findAll()
                .forEach(task -> userRepository.delete(task));
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = userRepository.add(new User(0, "User1", "123")).get();
        assertThat(user).isNotNull();

        var users = userRepository.findAll();
        assertThat(users).isEqualTo(List.of(user));
    }

    @Test
    public void whenSaveTwiceThenNotAdd() {
        var user1 = userRepository.add(new User(0, "User1", "123"));
        assertThat(user1).isPresent();

        var user2 = userRepository.add(new User(0, "User1", "123"));
        assertThat(user2).isEmpty();
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = userRepository.add(new User(0, "User1", "123")).get();
        var user2 = userRepository.add(new User(0, "User2", "123")).get();
        var user3 = userRepository.add(new User(0, "User3", "123")).get();

        var users = userRepository.findAll();
        assertThat(users).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    public void whenFindById() {
        var user = userRepository.add(new User(0, "User1", "123")).get();

        var foundUser = userRepository.findById(user.getId());
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get()).isEqualTo(user);

        var notFoundUser = userRepository.findById(0);
        assertThat(notFoundUser.isEmpty()).isTrue();
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(userRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(userRepository.findById(0)).isEqualTo(Optional.empty());
        assertThat(userRepository.findByLoginAndPassword("", "")).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDeleteById() {
        var user = userRepository.add(new User(0, "User1", "123")).get();

        var isDelete = userRepository.delete(user.getId());
        Optional<User> expectedEmty = userRepository.findById(user.getId());

        assertThat(isDelete).isTrue();
        assertThat(expectedEmty).isEqualTo(Optional.empty());

        isDelete = userRepository.delete(0);
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenDeleteByObject() {
        var user = userRepository.add(new User(0, "User1", "123")).get();

        var isDelete = userRepository.delete(user);
        Optional<User> expectedEmty = userRepository.findById(user.getId());

        assertThat(isDelete).isTrue();
        assertThat(expectedEmty).isEqualTo(Optional.empty());
    }

    @Test
    public void whenFindByLoginAndPassword() {
        var user1 = userRepository.add(new User(0, "User1", "123")).get();
        var user2 = userRepository.add(new User(0, "User2", "123")).get();

        Optional<User> foundUser = userRepository.findByLoginAndPassword(user1.getLogin(), user1.getPassword());
        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get()).isEqualTo(user1);
    }
}