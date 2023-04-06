package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Engine;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class EngineRepositoryHbnTest {

    private static EngineRepository engineRepository;

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void initRepository() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        CrudRepository crudRepository = new CrudRepository(sessionFactory);
        engineRepository = new EngineRepositoryHbn(crudRepository);
    }

    @AfterAll
    public static void clear() {
        sessionFactory.close();
    }

    @AfterEach
    public void clearRepository() {
        engineRepository.findAll()
                .forEach(engine -> engineRepository.delete(engine));
    }

    @Test
    public void whenSave() {
        var engine1 = engineRepository.add(new Engine(0, "Engine1"));
        assertThat(engine1).isPresent();
    }

    @Test
    public void whenFindById() {
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var findEngine = engineRepository.findById(engine1.getId());
        assertThat(findEngine).isPresent();
        assertThat(findEngine.get()).isEqualTo(engine1);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var engine2 = engineRepository.add(new Engine(0, "Engine2")).get();
        var engine3 = engineRepository.add(new Engine(0, "Engine3")).get();

        var engines = engineRepository.findAll();

        assertThat(engines).isEqualTo(List.of(engine1, engine2, engine3));
    }

    @Test
    public void whenFindByName() {
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();
        var findEngine = engineRepository.findByName("%ne1");
        assertThat(findEngine).contains(engine1);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(engineRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(engineRepository.findByName("%Own%")).isEqualTo(Collections.emptyList());
        assertThat(engineRepository.findById(1)).isEmpty();
    }

    @Test
    public void whenDeleteById() {
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();

        var isDelete = engineRepository.delete(engine1.getId());
        assertThat(isDelete).isTrue();

        var notFoundEngine = engineRepository.findById(engine1.getId());
        assertThat(notFoundEngine).isEmpty();
    }

    @Test
    public void whenDeleteByObject() {
        var engine1 = engineRepository.add(new Engine(0, "Engine1")).get();

        var isDelete = engineRepository.delete(engine1);
        assertThat(isDelete).isTrue();

        var notFoundEngine = engineRepository.findById(engine1.getId());
        assertThat(notFoundEngine).isEmpty();
    }
}