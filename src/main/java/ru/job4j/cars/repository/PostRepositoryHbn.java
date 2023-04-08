package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PostRepositoryHbn implements PostRepository {

    private final CrudRepository crudRepository;

    @Override
    public Optional<Post> add(Post post) {
        return Optional.ofNullable(
                crudRepository.tx(session -> {
                    session.save(post);
                    return post;
                })
        );
    }

    @Override
    public boolean delete(int id) {
        return crudRepository.run(
                "DELETE Post WHERE id = :id",
                Map.of("id", id)
        );
    }

    @Override
    public boolean delete(Post post) {
        return crudRepository.tx(session -> {
                    session.delete(post);
                    return post;
                }
        ) != null;
    }

    @Override
    public Optional<Post> findById(int id) {
        return crudRepository.optional("""
                            from Post p 
                                left join fetch p.priceHistories
                                left join fetch p.subscribers 
                                left join fetch p.photos
                            where p.id = :id
                        """,
                Post.class,
                Map.of("id", id)
        );
    }

    @Override
    public Collection<Post> findAll() {
        return crudRepository.query(
                "from Post p left join fetch p.priceHistories left join fetch p.subscribers",
                Post.class
        );
    }

    @Override
    public Collection<Post> findByDescription(String key) {
        return crudRepository.query(
                "from Post p left join fetch p.priceHistories left join fetch p.subscribers where p.description like :description",
                Post.class,
                Map.of("description", key)
        );
    }

    @Override
    public Collection<Post> findAllByCreationDateLaterDate(LocalDateTime date) {
        return crudRepository.query(
                "from Post p left join fetch p.priceHistories left join fetch p.subscribers where p.creationDate >= :startOfDay",
                Post.class,
                Map.of("startOfDay", date.truncatedTo(ChronoUnit.DAYS))
        );
    }

    @Override
    public Collection<Post> findByCarName(String carName) {
        return crudRepository.query(
                "from Post p left join fetch p.priceHistories left join fetch p.subscribers where p.car.name = :carName",
                Post.class,
                Map.of("carName", carName)
        );
    }
}
