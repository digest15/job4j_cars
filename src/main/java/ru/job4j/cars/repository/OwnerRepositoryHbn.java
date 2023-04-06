package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Owner;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class OwnerRepositoryHbn implements OwnerRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<Owner> add(Owner owner) {
        return Optional.ofNullable(
                crudRepository.tx(session -> {
                    session.save(owner);
                    return owner;
                })
        );
    }

    @Override
    public boolean delete(int id) {
        return crudRepository.run(
                "DELETE Owner WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public boolean delete(Owner owner) {
        return crudRepository.tx(session -> {
                    session.delete(owner);
                    return owner;
                }
        ) != null;
    }

    @Override
    public Optional<Owner> findById(int id) {
        return crudRepository.optional(
                "from Owner where id = :id",
                Owner.class,
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Owner> findAll() {
        return crudRepository.query(
                "from Owner",
                Owner.class
        );
    }

    @Override
    public Collection<Owner> findByName(String key) {
        return crudRepository.query(
                "from Owner o where o.name like :name",
                Owner.class,
                Map.of("name", key)
        );
    }

}
