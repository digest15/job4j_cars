package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.model.User;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PostRepositoryHbnTest {

    private static PostRepository postRepository;

    private static UserRepository userRepository;

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void initRepository() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        CrudRepository crudRepository = new CrudRepository(sessionFactory);
        userRepository = new UserRepositoryHbn(crudRepository);
        postRepository = new PostRepositoryHbn(crudRepository);
    }

    @AfterAll
    public static void clear() {
        sessionFactory.close();
    }

    @AfterEach
    public void clearRepository() {
        postRepository.findAll()
                .forEach(post -> postRepository.delete(post));
        userRepository.findAll()
                .forEach(task -> userRepository.delete(task));
    }

    @Test
    public void whenSave() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        Optional<Post> post = postRepository.add(new Post(0, "Post1", creationDate, user1, List.of(priceHistory)));

        assertThat(post).isPresent();
    }

    @Test
    public void whenSaveThenGetSame() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        Post post = postRepository.add(new Post(0, "Post1", creationDate, user1, List.of(priceHistory))).get();
        Optional<Post> findTask = postRepository.findById(post.getId());

        assertThat(findTask.isPresent()).isTrue();
        assertThat(post).isEqualTo(findTask.get());
        assertThat(findTask.get().getUser()).isEqualTo(user1);
        assertThat(findTask.get().getPriceHistories()).asList().contains(priceHistory);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistories = List.of(new PriceHistory(0, 0, 1, creationDate));
        Post post1 = postRepository.add(new Post(0, "Post1", creationDate, user1, priceHistories)).get();
        Post post2 = postRepository.add(new Post(0, "Post2", creationDate, user1, priceHistories)).get();
        Post post3 = postRepository.add(new Post(0, "Post3", creationDate, user1, priceHistories)).get();

        var posts = postRepository.findAll();
        assertThat(posts).isEqualTo(List.of(post1, post2, post3));
    }

    @Test
    public void whenFindById() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        Post post1 = postRepository.add(new Post(0, "Post1", creationDate, user1, List.of(priceHistory))).get();

        var foundPost = postRepository.findById(post1.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get()).isEqualTo(post1);
        assertThat(foundPost.get().getUser()).isEqualTo(user1);
        assertThat(foundPost.get().getPriceHistories()).asList().contains(priceHistory);

        var notFoundedPost = postRepository.findById(0);
        assertThat(notFoundedPost).isEmpty();
    }

    @Test
    public void whenFindByDescription() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        Post post1 = postRepository.add(new Post(0, "Post1", creationDate, user1, List.of(priceHistory))).get();

        var foundPosts = (List<Post>) postRepository.findByDescription(post1.getDescription());
        assertThat(foundPosts).isEqualTo(List.of(post1));
        assertThat(foundPosts.get(0).getUser()).isEqualTo(user1);
        assertThat(foundPosts.get(0).getPriceHistories()).asList().contains(priceHistory);

        var notFoundedPosts = postRepository.findByDescription("");
        assertThat(notFoundedPosts).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(postRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(postRepository.findById(0)).isEqualTo(Optional.empty());
        assertThat(postRepository.findByDescription("")).isEqualTo(Collections.emptyList());
    }

    @Test
    public void whenDeleteById() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var post1 = postRepository.add(new Post(0, "Post1", creationDate, user1, List.of())).get();

        var isDelete = postRepository.delete(post1.getId());
        assertThat(isDelete).isTrue();

        var notFoundPost = postRepository.findById(post1.getId());
        assertThat(notFoundPost).isEmpty();

        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        var post2 = postRepository.add(new Post(0, "Post2", creationDate, user1, List.of(priceHistory))).get();
        isDelete = postRepository.delete(post2.getId());
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenDeleteByObject() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        Post post1 = postRepository.add(new Post(0, "Post1", creationDate, user1, List.of(priceHistory))).get();

        var isDelete = postRepository.delete(post1);
        assertThat(isDelete).isTrue();

        var notFoundPost = postRepository.findById(post1.getId());
        assertThat(notFoundPost).isEmpty();
    }
}