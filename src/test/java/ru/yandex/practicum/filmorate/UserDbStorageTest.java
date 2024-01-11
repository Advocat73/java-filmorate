package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userStorage;

    @BeforeEach
    void beforeEach() {
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testFindUserByIdAndFindAllUsers() {
        User newUser = new User(1L, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        userStorage.add(newUser);
        User savedUser = userStorage.find(1L);
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
        User newSecondUser = new User(3L, "secondUser@email.ru", "sidor45", "Sidor Sidorov", LocalDate.of(2000, 2, 2));
        userStorage.add(newSecondUser);
        User newThirdUser = new User(3L, "thirdUser@email.ru", "Ivanov11", "Vasya Ivanov", LocalDate.of(1949, 4, 4));
        userStorage.add(newThirdUser);
        List<User> users = userStorage.findUsers();
        assertThat(users.size()).isEqualTo(3);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testUpdateUser() {
        User newUser = new User(10L, "newUser@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(2000, 2, 2));
        userStorage.add(newUser);
        long newUserId = newUser.getId();
        User updateUser = new User(newUserId, "userForUpdate@email.ru", "sidor45", "Sidor Sidorov", LocalDate.of(1995, 3, 3));
        userStorage.update(updateUser);
        User testUser = userStorage.find(newUserId);
        assertThat(testUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isNotEqualTo(newUser)
                .isEqualTo(updateUser);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testIsContainsAndRemoveUser() {
        User newUser = new User(10L, "newUser@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(2000, 2, 2));
        userStorage.add(newUser);
        long newUserId = newUser.getId();
        boolean b = userStorage.isContains(newUserId);
        assertThat(b).isTrue();
        userStorage.remove(newUserId);
        b = userStorage.isContains(newUserId);
        assertThat(b).isFalse();
        User testUser = userStorage.find(newUserId);
        assertThat(testUser).isNull();
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testAddAndRemoveFriendship() {
        // Инициализация и добавление трех юзеров
        User newUser = new User(10L, "newUser@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(2000, 2, 2));
        userStorage.add(newUser);
        long newUserId = newUser.getId();
        User friendUser = new User(10L, "friendUser@email.ru", "sidor45", "Sidor Sidorov", LocalDate.of(1995, 3, 3));
        userStorage.add(friendUser);
        long friendUserId = friendUser.getId();
        User anotherFriendUser = new User(10L, "anotherFriendUser@email.ru", "Ivanov11", "Vasya Ivanov", LocalDate.of(1949, 4, 4));
        userStorage.add(anotherFriendUser);
        long anotherFriendUserId = anotherFriendUser.getId();
        // Устанавливаем одностороннюю дружбу
        userStorage.setFriendship(friendUser, newUser);
        userStorage.setFriendship(anotherFriendUser, newUser);
        List<Long> newUserFriends = userStorage.findFriends(newUserId);
        assertThat(newUserFriends.get(0)).isEqualTo(friendUserId);
        assertThat(newUserFriends.get(1)).isEqualTo(anotherFriendUserId);
        // Одну дружбу удаляем
        userStorage.removeFriendship(friendUser, newUser);
        newUserFriends = userStorage.findFriends(newUserId);
        assertThat(newUserFriends.size()).isEqualTo(1);
        assertThat(newUserFriends.get(0)).isEqualTo(anotherFriendUserId);
        // Полностью удаляем друга
        userStorage.remove(anotherFriendUserId);
        newUserFriends = userStorage.findFriends(newUserId);
        assertThat(newUserFriends.size()).isEqualTo(0);
    }
}