package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CarRepositoryHbn implements CarRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<Car> add(Car car) {
        return Optional.ofNullable(
                crudRepository.tx(session -> {
                    session.save(car);
                    return car;
                })
        );
    }

    @Override
    public boolean delete(int id) {
        return crudRepository.run(
                "DELETE Car WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public boolean delete(Car car) {
        return crudRepository.tx(session -> {
                    session.delete(car);
                    return car;
                }
        ) != null;
    }

    @Override
    public Optional<Car> findById(int id) {
        return crudRepository.optional(
                "from Car c left join fetch c.owners where c.id = :id",
                Car.class,
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Car> findAll() {
        return crudRepository.query(
                "from Car c left join fetch c.owners",
                Car.class
        );
    }

    @Override
    public Collection<Car> findByName(String key) {
        return crudRepository.query(
                "from Car c left join fetch c.owners where c.name like :name",
                Car.class,
                Map.of("name", key)
        );
    }
}
