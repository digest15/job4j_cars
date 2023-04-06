package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class OwnerRepositoryHbnTest {

    private static OwnerRepository ownerRepository;

    private static UserRepository userRepository;

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void initRepository() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        CrudRepository crudRepository = new CrudRepository(sessionFactory);
        userRepository = new UserRepositoryHbn(crudRepository);
        ownerRepository = new OwnerRepositoryHbn(crudRepository);
    }

    @AfterAll
    public static void clear() {
        sessionFactory.close();
    }

    @AfterEach
    public void clearRepository() {
        ownerRepository.findAll()
                .forEach(owner -> ownerRepository.delete(owner));
        userRepository.findAll()
                .forEach(task -> userRepository.delete(task));
    }

    @Test
    public void whenSave() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();

        Optional<Owner> owner = ownerRepository.add(new Owner(0, "Owner1", user1));

        assertThat(owner).isPresent();
    }

    @Test
    public void whenSaveThenGetSame() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        Optional<Owner> owner = ownerRepository.add(new Owner(0, "Owner1", user1));

        Optional<Owner> findOwner = ownerRepository.findById(owner.get().getId());

        assertThat(findOwner.get()).isEqualTo(owner.get());
        assertThat(findOwner.get().getUser()).isEqualTo(user1);
    }

    @Test
    public void whenSaveWithoutUserThenNotSave() {
        Optional<Owner> owner = ownerRepository.add(new Owner(0, "Owner1", null));
        assertThat(owner).isEmpty();
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = userRepository.add(new User(0, "admin1", "123")).get();
        var user2 = userRepository.add(new User(0, "admin2", "123")).get();
        var user3 = userRepository.add(new User(0, "admin3", "123")).get();

        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();
        var owner2 = ownerRepository.add(new Owner(0, "Owner2", user2)).get();
        var owner3 = ownerRepository.add(new Owner(0, "Owner3", user3)).get();

        var owners = ownerRepository.findAll();

        assertThat(owners).isEqualTo(List.of(owner1, owner2, owner3));
    }

    @Test
    public void whenSaveSeveralWithSameUserThenNotSave() {
        var user1 = userRepository.add(new User(0, "admin1", "123")).get();

        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1));
        var owner2 = ownerRepository.add(new Owner(0, "Owner2", user1));
        var owners = ownerRepository.findAll();

        assertThat(owner1).isPresent();
        assertThat(owner2).isEmpty();
        assertThat(owners).isEqualTo(List.of(owner1.get()));
    }

    @Test
    public void whenFindById() {
        var user1 = userRepository.add(new User(0, "admin1", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();

        var foundOwner = ownerRepository.findById(owner1.getId());

        assertThat(foundOwner.get()).isEqualTo(owner1);
    }

    @Test
    public void whenFindByName() {
        var user1 = userRepository.add(new User(0, "admin1", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();

        var foundOwner = ownerRepository.findByName("%ner1");

        assertThat(foundOwner).isEqualTo(List.of(owner1));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(ownerRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(ownerRepository.findByName("%Own%")).isEqualTo(Collections.emptyList());
        assertThat(ownerRepository.findById(1)).isEmpty();
    }

    @Test
    public void whenDeleteById() {
        var user1 = userRepository.add(new User(0, "admin1", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();

        var isDelete = ownerRepository.delete(owner1.getId());
        assertThat(isDelete).isTrue();

        var notFoundOwner = ownerRepository.findById(owner1.getId());
        assertThat(notFoundOwner).isEmpty();

        isDelete = ownerRepository.delete(owner1.getId());
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenDeleteByObject() {
        var user1 = userRepository.add(new User(0, "admin1", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();

        var isDelete = ownerRepository.delete(owner1);
        assertThat(isDelete).isTrue();

        var notFoundOwner = ownerRepository.findById(owner1.getId());
        assertThat(notFoundOwner).isEmpty();
    }
}