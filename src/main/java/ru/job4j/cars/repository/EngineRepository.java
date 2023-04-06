package ru.job4j.cars.repository;

import ru.job4j.cars.model.Engine;

import java.util.Collection;
import java.util.Optional;

public interface EngineRepository {

    Optional<Engine> add(Engine engine);

    boolean delete(int id);

    boolean delete(Engine engine);

    Optional<Engine> findById(int id);

    Collection<Engine> findAll();

    Collection<Engine> findByName(String key);
}
