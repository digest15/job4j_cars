package ru.job4j.cars.repository;


import ru.job4j.cars.model.Owner;

import java.util.Collection;
import java.util.Optional;

public interface OwnerRepository {

    Optional<Owner> add(Owner owner);

    boolean delete(int id);

    boolean delete(Owner owner);

    Optional<Owner> findById(int id);

    Collection<Owner> findAll();

    Collection<Owner> findByName(String key);
}
