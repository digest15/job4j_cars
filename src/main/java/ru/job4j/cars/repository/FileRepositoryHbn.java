package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.File;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class FileRepositoryHbn implements FileRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<File> save(File file) {
        return Optional.ofNullable(
                crudRepository.tx(session -> {
                    session.save(file);
                    return file;
                })
        );
    }

    @Override
    public Optional<File> findById(int id) {
        return crudRepository.optional(
                "from File id = :id",
                File.class,
                Map.of("id", id)
        );
    }

    @Override
    public boolean deleteById(int id) {
        return crudRepository.run(
                "DELETE File WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public List<File> findAll() {
        return crudRepository.query(
                "from File",
                File.class
        );
    }

    @Override
    public boolean delete(File file) {
        return crudRepository.tx(session -> {
                    session.delete(file);
                    return file;
                }
        ) != null;
    }

}
