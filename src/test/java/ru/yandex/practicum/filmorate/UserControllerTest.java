package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private Validator validator;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
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
    void isUserCreateWithNullEmailAndNotCreateWithEmptyEmailAndEmailWithoutSpecialSignAndEmailWithSpecialSignOnEnd() {
        User userWithNullEmail = new User(0, null, "Advocate", "name",
                LocalDate.of(2000, 1, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(userWithNullEmail);
        assertTrue(violations.isEmpty());
        userController.create(userWithNullEmail);
        assertEquals(1, userWithNullEmail.getId(), "ID NOT GOOD");

        User userWithEmptyEmai = new User(0, " ", "Advocate",
                "name", LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithEmptyEmai);
        assertEquals(1, violations.size(), "Проблемы с пустым Email не пойманы");

        User userWithEmailWithoutSpecialSign = new User(0, "Email.Without.SpecialSign", "Advocate",
                "name", LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithEmailWithoutSpecialSign);
        assertEquals(1, violations.size(), "Проблемы с Email без @ не пойманы");

        User userWithEmailWithSpecialSignOnEnd = new User(0, "EmailWithSpecialSignOnEnd@", "Advocate",
                "name", LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithEmailWithSpecialSignOnEnd);
        assertEquals(1, violations.size(), "Проблемы с Email с @ на конце не пойманы");
    }

    @Test
    void isUserNotCreateWithNullLoginAndEmptyLoginAndLoginWithSpace() {
        User userWithNullLogin = new User(0, "name@mail.ru", null, "name",
                LocalDate.of(2000, 1, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(userWithNullLogin);
        assertEquals(1, violations.size(), "Проблемы с login = null не пойманы");

        User userWithEmptyLogin = new User(0, "name@mail.ru", "", "name",
                LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithEmptyLogin);
        assertEquals(1, violations.size(), "Проблемы с пустым login не пойманы");

        User userWithLoginWithSpace = new User(0, "name@mail.ru", "Login WithSpace", "name",
                LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithLoginWithSpace);
        assertEquals(1, violations.size(), "Проблемы с login, в котором пробелы не пойманы");
    }

    @Test
    void isUserWithNullNameAndIfNameEmptySoNameBecomeLogin() {
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

        User userWithWithFutureBirthday = new User(0, "name@mail.ru", "Advocate",
                "name", LocalDate.of(2024, 1, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(userWithWithFutureBirthday);
        assertEquals(1, violations.size(), "Проблемы с birthday, который в будущем не пойманы");
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
