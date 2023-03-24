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
import java.util.Set;

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
        var priceHistory1 = new PriceHistory(0, 0, 1, creationDate);
        var priceHistory2 = new PriceHistory(0, 0, 2, creationDate);
        Optional<Post> post = postRepository.add(Post.builder()
                .description("Post1")
                .creationDate(creationDate)
                .user(user1)
                .priceHistories(List.of(priceHistory1, priceHistory2))
                .subscribers(Set.of(user1))
                .build()
        );

        assertThat(post).isPresent();
    }

    @Test
    public void whenSaveThenGetSame() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistory1 = new PriceHistory(0, 0, 1, creationDate);
        var priceHistory2 = new PriceHistory(0, 1, 2, creationDate);
        Optional<Post> post = postRepository.add(Post.builder()
                .description("Post1")
                .creationDate(creationDate)
                .user(user1)
                .priceHistories(List.of(priceHistory1, priceHistory2))
                .subscribers(Set.of(user1))
                .build()
        );
        Optional<Post> findPost = postRepository.findById(post.get().getId());

        assertThat(findPost).isPresent();
        assertThat(findPost.get()).isEqualTo(post.get());
        assertThat(findPost.get().getUser()).isEqualTo(user1);
        assertThat(findPost.get().getPriceHistories()).asList().contains(priceHistory1, priceHistory2);
        assertThat(findPost.get().getSubscribers()).isEqualTo(Set.of(user1));
    }

    @Test
    public void whenSaveWithoutPriceHistoryAndSubscribersThenGetSame() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Optional<Post> post = postRepository.add(Post.builder()
                .description("Post1")
                .creationDate(creationDate)
                .user(user1)
                .build()
        );
        Optional<Post> findPost = postRepository.findById(post.get().getId());

        assertThat(findPost).isPresent();
        assertThat(findPost.get()).isEqualTo(post.get());
        assertThat(findPost.get().getUser()).isEqualTo(user1);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistories = List.of(new PriceHistory(0, 0, 1, creationDate));
        var subscribers = Set.of(user1);
        Post post1 = postRepository.add(Post.builder()
                .description("Post1")
                .creationDate(creationDate)
                .user(user1)
                .priceHistories(priceHistories)
                .subscribers(subscribers)
                .build()
        ).get();
        Post post2 = postRepository.add(Post.builder()
                .description("Post2")
                .creationDate(creationDate)
                .user(user1)
                .priceHistories(priceHistories)
                .subscribers(subscribers)
                .build()
        ).get();
        Post post3 = postRepository.add(Post.builder()
                .description("Post3")
                .creationDate(creationDate)
                .user(user1)
                .priceHistories(priceHistories)
                .subscribers(subscribers)
                .build()
        ).get();

        var posts = postRepository.findAll();
        assertThat(posts).isEqualTo(List.of(post1, post2, post3));
    }

    @Test
    public void whenFindById() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        Post post1 = postRepository.add(Post.builder()
                .description("Post1")
                .creationDate(creationDate)
                .user(user1)
                .priceHistories(List.of(priceHistory))
                .subscribers(Set.of(user1))
                .build()
        ).get();

        var foundPost = postRepository.findById(post1.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get()).isEqualTo(post1);
        assertThat(foundPost.get().getUser()).isEqualTo(user1);
        assertThat(foundPost.get().getPriceHistories()).asList().contains(priceHistory);
        assertThat(foundPost.get().getSubscribers()).isEqualTo(Set.of(user1));

        var notFoundedPost = postRepository.findById(0);
        assertThat(notFoundedPost).isEmpty();
    }

    @Test
    public void whenFindByDescription() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        Post post1 = postRepository.add(Post.builder()
                .description("Post1")
                .creationDate(creationDate)
                .user(user1)
                .priceHistories(List.of(priceHistory))
                .subscribers(Set.of(user1))
                .build()
        ).get();

        var foundPosts = (List<Post>) postRepository.findByDescription(post1.getDescription());
        assertThat(foundPosts).isEqualTo(List.of(post1));
        assertThat(foundPosts.get(0).getUser()).isEqualTo(user1);
        assertThat(foundPosts.get(0).getPriceHistories()).asList().contains(priceHistory);
        assertThat(foundPosts.get(0).getSubscribers()).isEqualTo(Set.of(user1));

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
        Post post1 = postRepository.add(Post.builder()
                .description("Post1")
                .creationDate(creationDate)
                .user(user1)
                .build()
        ).get();

        var isDelete = postRepository.delete(post1.getId());
        assertThat(isDelete).isTrue();

        var notFoundPost = postRepository.findById(post1.getId());
        assertThat(notFoundPost).isEmpty();

        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        Post post2 = postRepository.add(Post.builder()
                .description("Post1")
                .creationDate(creationDate)
                .user(user1)
                .priceHistories(List.of(priceHistory))
                .build()
        ).get();
        isDelete = postRepository.delete(post2.getId());
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenDeleteByObject() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priceHistory = new PriceHistory(0, 0, 1, creationDate);
        Post post1 = postRepository.add(Post.builder()
                .description("Post1")
                .creationDate(creationDate)
                .user(user1)
                .priceHistories(List.of(priceHistory))
                .subscribers(Set.of(user1))
                .build()
        ).get();

        var isDelete = postRepository.delete(post1);
        assertThat(isDelete).isTrue();

        var notFoundPost = postRepository.findById(post1.getId());
        assertThat(notFoundPost).isEmpty();
    }
}