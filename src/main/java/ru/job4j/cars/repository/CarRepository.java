package ru.job4j.cars.repository;

import ru.job4j.cars.model.Car;

import java.util.Collection;
import java.util.Optional;

public interface CarRepository {

    Optional<Car> add(Car car);

    boolean delete(int id);

    boolean delete(Car car);

    Optional<Car> findById(int id);

    Collection<Car> findAll();

    Collection<Car> findByName(String key);
}
