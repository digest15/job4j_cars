package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class EngineRepositoryHbn implements EngineRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<Engine> add(Engine engine) {
        return Optional.ofNullable(
                crudRepository.tx(session -> {
                    session.save(engine);
                    return engine;
                })
        );
    }

    @Override
    public boolean delete(int id) {
        return crudRepository.run(
                "DELETE Engine WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public boolean delete(Engine engine) {
        return crudRepository.tx(session -> {
                    session.delete(engine);
                    return engine;
                }
        ) != null;
    }

    @Override
    public Optional<Engine> findById(int id) {
        return crudRepository.optional(
                "from Engine where id = :id",
                Engine.class,
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Engine> findAll() {
        return crudRepository.query(
                "from Engine",
                Engine.class
        );
    }

    @Override
    public Collection<Engine> findByName(String key) {
        return crudRepository.query(
                "from Engine o where o.name like :name",
                Engine.class,
                Map.of("name", key)
        );
    }
}
