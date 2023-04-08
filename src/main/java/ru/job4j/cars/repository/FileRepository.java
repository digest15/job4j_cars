package ru.job4j.cars.repository;


import ru.job4j.cars.model.File;

import java.util.List;
import java.util.Optional;

public interface FileRepository {

    Optional<File> save(File file);

    Optional<File> findById(int id);

    List<File> findAll();

    boolean deleteById(int id);

    boolean delete(File file);
}
