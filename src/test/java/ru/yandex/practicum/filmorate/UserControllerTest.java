package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private Validator validator;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    void isCreateUserWithAllArgumentsNotGood() {
        User user = new User(0, "name@", " ", " ",
                LocalDate.of(2024, 1, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size(), "Не все поймано");
    }
    @Test
    void isGoodUserCreateAndGetGoodID() {
        User user = new User(0, "name@mail.ru", "Advocate", "name",
                LocalDate.of(2000, 1, 13));
        userController.create(user);
        assertEquals(1, user.getId(), "ID NOT GOOD");
    }

    @Test
    void isUserCreateWithNullEmailAndNotCreateWithEmptyEmailAndEmailWithoutSpecialSign() {
        User userWithNullEmail = new User(0, null, "Advocate", "name",
                LocalDate.of(2000, 1, 13));
        userController.create(userWithNullEmail);
        assertEquals(1, userWithNullEmail.getId(), "ID NOT GOOD");

        ValidationException exception = assertThrows(
                ValidationException.class, () -> {
                    userController.create(new User(0, " ", "Advocate",
                            "name", LocalDate.of(2000, 1, 13)));
                });
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @",
                exception.getMessage(), "No ValidationException");

        exception = assertThrows(
                ValidationException.class, () -> {
                    userController.create(new User(0, "name.mail.ru", "Advocate",
                            "name", LocalDate.of(2000, 1, 13)));
                });
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @",
                exception.getMessage(), "No ValidationException");
    }

    @Test
    void isUserCreateWithNullLoginAndNotCreateWithEmptyLoginAndLoginWithSpace() {
        User userWithNullLogin = new User(0, "name@mail.ru", null, "name",
                LocalDate.of(2000, 1, 13));
        userController.create(userWithNullLogin);
        assertEquals(1, userWithNullLogin.getId(), "ID NOT GOOD");

        ValidationException exception = assertThrows(
                ValidationException.class, () -> {
                    userController.create(new User(0, "name@mail.ru", "   ",
                            "name", LocalDate.of(2000, 1, 13)));
                });
        assertEquals("Логин не может быть пустым и содержать пробелы",
                exception.getMessage(), "No ValidationException");

        exception = assertThrows(
                ValidationException.class, () -> {
                    userController.create(new User(0, "name@mail.ru", "login withSpace",
                            "name", LocalDate.of(2000, 1, 13)));
                });
        assertEquals("Логин не может быть пустым и содержать пробелы",
                exception.getMessage(), "No ValidationException");
    }

    @Test
    void isUserWithNullNameAndIfNameEmptyNameBecomeLogin() {
        User userWithNullName = new User(0, "name@mail.ru", "Advocate", null,
                LocalDate.of(2000, 1, 13));
        userController.create(userWithNullName);
        assertEquals(1, userWithNullName.getId(), "ID NOT GOOD");
        assertEquals(userWithNullName.getName(), userWithNullName.getLogin(), "NAME != LOGIN");

        User userWithEmptyName = new User(0, "name@mail.ru", "Advocate", "  ",
                LocalDate.of(2000, 1, 13));
        userController.create(userWithEmptyName);
        assertEquals(2, userWithEmptyName.getId(), "ID NOT GOOD");
        assertEquals(userWithEmptyName.getName(), userWithEmptyName.getLogin(), "NAME != LOGIN");
    }

    @Test
    void isUserCreateWithNullBirthdayAndNotCreateWithFutureBirthday() {
        User userWithNullEmail = new User(0, "name@mail.ru", "Advocate", "name", null);
        userController.create(userWithNullEmail);
        assertEquals(1, userWithNullEmail.getId(), "ID NOT GOOD");

        ValidationException exception = assertThrows(
                ValidationException.class, () -> {
                    userController.create(new User(0, "name@mail.ru", "Advocate",
                            "name", LocalDate.of(2024, 1, 13)));
                });
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage(), "No ValidationException");
    }

    @Test
    void isUserUpdateWithGoodIDAndNotUpdateWithNotExistID() {
        User user = new User(0, null, null, null, null);
        userController.create(user);
        User updateUser = new User(user.getId(), "name@mail.ru", "Advocate", "name",
                LocalDate.of(2000, 1, 13));
        userController.update(updateUser);
        assertEquals(1, updateUser.getId(), "UPDATE: ID NOT GOOD");
        assertEquals("name@mail.ru", updateUser.getEmail(), "UPDATE: EMAIL NOT GOOD");
        assertEquals("Advocate", updateUser.getLogin(), "UPDATE: LOGINT NOT GOOD");
        assertEquals("name", updateUser.getName(), "UPDATE: NAME NOT GOOD");
        assertEquals("2000-01-13", updateUser.getBirthday().format(formatter), "UPDATE: BIRTHDAY NOT GOOD");

        User updateUserWithNotExistID = new User(999, "name@mail.ru", "Advocate", "name",
                LocalDate.of(2000, 1, 13));
        ValidationException exception = assertThrows(
                ValidationException.class, () -> {
                    userController.update(updateUserWithNotExistID);
                });
        assertEquals("Нет пользователя с ID: 999", exception.getMessage(), "UPDATE: No ValidationException");
    }
}
