package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CarRepositoryHbnTest {

    private static CarRepository carRepository;

    private static EngineRepository engineRepository;

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
        engineRepository = new EngineRepositoryHbn(crudRepository);
        carRepository = new CarRepositoryHbn(crudRepository);
    }

    @AfterAll
    public static void clear() {
        sessionFactory.close();
    }

    @AfterEach
    public void clearRepository() {
        carRepository.findAll()
                .forEach(car -> carRepository.delete(car));
        engineRepository.findAll()
                .forEach(engine -> engineRepository.delete(engine));
        ownerRepository.findAll()
                .forEach(owner -> ownerRepository.delete(owner));
        userRepository.findAll()
                .forEach(task -> userRepository.delete(task));
    }

    @Test
    public void whenSave() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();

        var car1 = carRepository.add(new Car(0, "Car1", engine1, Set.of(owner1)));
        assertThat(car1).isPresent();
    }

    @Test
    public void whenSaveThenGetSame() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var car1 = carRepository.add(new Car(0, "Car1", engine1, Set.of(owner1))).get();

        var foundCar = carRepository.findById(car1.getId());
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get()).isEqualTo(car1);
        assertThat(foundCar.get().getEngine()).isEqualTo(engine1);
        assertThat(foundCar.get().getOwners()).isEqualTo(Set.of(owner1));
    }

    @Test
    public void whenSaveWithoutEngine() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();

        var car1 = carRepository.add(new Car(0, "Car1", null, Set.of(owner1)));

        assertThat(car1).isEmpty();
    }

    @Test
    public void whenSaveWithoutOwners() {
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var car1 = carRepository.add(new Car(0, "Car1", engine1, Set.of())).get();

        var foundCar = carRepository.findById(car1.getId());
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get()).isEqualTo(car1);
        assertThat(foundCar.get().getEngine()).isEqualTo(engine1);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();

        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var car1 = carRepository.add(new Car(0, "Car1", engine1, Set.of(owner1))).get();
        var engine2 = engineRepository.add(new Engine(0, "Engine2")).get();
        var car2 = carRepository.add(new Car(0, "Car1", engine2, Set.of(owner1))).get();

        var foundCar = carRepository.findAll();
        assertThat(foundCar).isEqualTo(List.of(car1, car2));
    }

    @Test
    public void whenSaveSeveralWithSameEngine() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();

        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var car1 = carRepository.add(new Car(0, "Car1", engine1, Set.of(owner1)));
        assertThat(car1).isPresent();

        var car2 = carRepository.add(new Car(0, "Car1", engine1, Set.of(owner1)));
        assertThat(car2).isEmpty();

        var foundCar = carRepository.findAll();
        assertThat(foundCar).isEqualTo(List.of(car1.get()));
    }

    @Test
    public void whenFindById() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var car1 = carRepository.add(new Car(0, "Car1", engine1, Set.of(owner1))).get();

        var foundCar = carRepository.findById(car1.getId());
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get()).isEqualTo(car1);
        assertThat(foundCar.get().getEngine()).isEqualTo(engine1);
        assertThat(foundCar.get().getOwners()).isEqualTo(Set.of(owner1));
    }

    @Test
    public void whenFindByName() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var car1 = carRepository.add(new Car(0, "Car1", engine1, Set.of(owner1))).get();

        var foundCars = carRepository.findByName("C%");
        assertThat(foundCars).isEqualTo(List.of(car1));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(carRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(carRepository.findById(0)).isEqualTo(Optional.empty());
        assertThat(carRepository.findByName("%r")).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteById() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var car1 = carRepository.add(new Car(0, "Car1", engine1, Set.of(owner1))).get();

        var isDelete = carRepository.delete(car1.getId());
        assertThat(isDelete).isTrue();

        var foundCar = carRepository.findById(car1.getId());
        assertThat(foundCar).isEmpty();
    }

    @Test
    public void whenDeleteByObject() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var owner1 = ownerRepository.add(new Owner(0, "Owner1", user1)).get();
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var car1 = carRepository.add(new Car(0, "Car1", engine1, Set.of(owner1))).get();

        var isDelete = carRepository.delete(car1);
        assertThat(isDelete).isTrue();

        var foundCar = carRepository.findById(car1.getId());
        assertThat(foundCar).isEmpty();
    }
}