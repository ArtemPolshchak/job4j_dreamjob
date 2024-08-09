package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveThenGetSameUser() {
        var user = new User(0, "test@mail.com", "Test User", "password");
        var savedUser = sql2oUserRepository.save(user).get();
        var fetchedUser = sql2oUserRepository.findByEmailAndPassword("test@mail.com", "password").get();
        assertThat(fetchedUser).usingRecursiveComparison().isEqualTo(savedUser);
    }

    @Test
    public void whenSaveUserWithExistingEmailThenReturnEmptyOptional() {
        var user1 = new User(0, "duplicate@mail.com", "User One", "password1");
        var user2 = new User(0, "duplicate@mail.com", "User Two", "password2");

        var savedUser1 = sql2oUserRepository.save(user1);
        var savedUser2 = sql2oUserRepository.save(user2);

        assertThat(savedUser1).isPresent();
        assertThat(savedUser2).isEmpty();
    }

    @Test
    public void whenFindUserByEmailAndPasswordThenReturnUser() {
        var user = new User(0, "login@mail.com", "Login User", "password");
        sql2oUserRepository.save(user);
        var foundUser = sql2oUserRepository.findByEmailAndPassword("login@mail.com", "password");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("login@mail.com");
        assertThat(foundUser.get().getPassword()).isEqualTo("password");
    }

    @Test
    public void whenFindUserByInvalidEmailAndPasswordThenReturnEmptyOptional() {
        var foundUser = sql2oUserRepository.findByEmailAndPassword("invalid@mail.com", "wrongpassword");
        assertThat(foundUser).isEmpty();
    }
}
