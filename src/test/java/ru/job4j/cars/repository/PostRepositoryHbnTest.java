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
        userRepository.findAll()
                .forEach(task -> userRepository.delete(task));
        postRepository.findAll()
                .forEach(post -> postRepository.delete(post));
    }

    @Test
    public void whenSave() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Optional<Post> post = postRepository.add(new Post(0, "Post1", creationDate, user1));

        assertThat(post).isPresent();
    }

    @Test
    public void whenSaveThenGetSame() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Post post = postRepository.add(new Post(0, "Post1", creationDate, user1)).get();
        Optional<Post> findTask = postRepository.findById(post.getId());

        assertThat(findTask.isPresent()).isTrue();
        assertThat(post).isEqualTo(findTask.get());
        assertThat(findTask.get().getUser()).isEqualTo(user1);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Post post1 = postRepository.add(new Post(0, "Post1", creationDate, user1)).get();
        Post post2 = postRepository.add(new Post(0, "Post2", creationDate, user1)).get();
        Post post3 = postRepository.add(new Post(0, "Post3", creationDate, user1)).get();

        var posts = postRepository.findAll();
        assertThat(posts).isEqualTo(List.of(post1, post2, post3));
    }

    @Test
    public void whenFindById() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Post post1 = postRepository.add(new Post(0, "Post1", creationDate, user1)).get();

        var foundPost = postRepository.findById(post1.getId());
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get()).isEqualTo(post1);

        var notFoundedPost = postRepository.findById(0);
        assertThat(notFoundedPost).isEmpty();
    }

    @Test
    public void whenFindByDescription() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Post post1 = postRepository.add(new Post(0, "Post1", creationDate, user1)).get();

        var foundPosts = postRepository.findByDescription(post1.getDescription());
        assertThat(foundPosts).isEqualTo(List.of(post1));

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
        Post post1 = postRepository.add(new Post(0, "Post1", creationDate, user1)).get();

        var isDelete = postRepository.delete(post1.getId());
        assertThat(isDelete).isTrue();

        var notFoundPost = postRepository.findById(post1.getId());
        assertThat(notFoundPost).isEmpty();

        isDelete = postRepository.delete(post1.getId());
        assertThat(isDelete).isFalse();
    }

    @Test
    public void whenDeleteByObject() {
        var user1 = userRepository.add(new User(0, "admin", "123")).get();
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        Post post1 = postRepository.add(new Post(0, "Post1", creationDate, user1)).get();

        var isDelete = postRepository.delete(post1);
        assertThat(isDelete).isTrue();

        var notFoundPost = postRepository.findById(post1.getId());
        assertThat(notFoundPost).isEmpty();
    }
}