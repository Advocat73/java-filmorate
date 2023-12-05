package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    void beforeEach() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void isCreateUserWithAllArgumentsNotGood() {
        User user = new User((long) 0, "name@", " ", " ",
                LocalDate.of(2024, 1, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size(), "Не все поймано");
    }

    @Test
    void isGoodUserCreate() {
        User user = new User((long) 0, "name@mail.ru", "Advocate", "name",
                LocalDate.of(2000, 1, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void isUserCreateWithNullEmailAndNotCreateWithEmptyEmailAndEmailWithoutSpecialSignAndEmailWithSpecialSignOnEnd() {
        User userWithNullEmail = new User((long) 0, null, "Advocate", "name",
                LocalDate.of(2000, 1, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(userWithNullEmail);
        assertTrue(violations.isEmpty());

        User userWithEmptyEmai = new User((long) 0, " ", "Advocate",
                "name", LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithEmptyEmai);
        assertEquals(1, violations.size(), "Проблемы с пустым Email не пойманы");

        User userWithEmailWithoutSpecialSign = new User((long) 0, "Email.Without.SpecialSign", "Advocate",
                "name", LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithEmailWithoutSpecialSign);
        assertEquals(1, violations.size(), "Проблемы с Email без @ не пойманы");

        User userWithEmailWithSpecialSignOnEnd = new User((long) 0, "EmailWithSpecialSignOnEnd@", "Advocate",
                "name", LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithEmailWithSpecialSignOnEnd);
        assertEquals(1, violations.size(), "Проблемы с Email с @ на конце не пойманы");
    }

    @Test
    void isUserNotCreateWithNullLoginAndEmptyLoginAndLoginWithSpace() {
        User userWithNullLogin = new User((long) 0, "name@mail.ru", null, "name",
                LocalDate.of(2000, 1, 13));
        Set<ConstraintViolation<User>> violations = validator.validate(userWithNullLogin);
        assertEquals(1, violations.size(), "Проблемы с login = null не пойманы");

        User userWithEmptyLogin = new User((long) 0, "name@mail.ru", "", "name",
                LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithEmptyLogin);
        assertEquals(1, violations.size(), "Проблемы с пустым login не пойманы");

        User userWithLoginWithSpace = new User((long) 0, "name@mail.ru", "Login WithSpace", "name",
                LocalDate.of(2000, 1, 13));
        violations = validator.validate(userWithLoginWithSpace);
        assertEquals(1, violations.size(), "Проблемы с login, в котором пробелы не пойманы");
    }

    @Test
    void isUserCreateWithNullBirthdayAndNotCreateWithFutureBirthday() {
        User userWithNullBirthday = new User((long) 0, "name@mail.ru", "Advocate", "name", null);
        Set<ConstraintViolation<User>> violations = validator.validate(userWithNullBirthday);
        assertEquals(0, violations.size(), "Проблемы с с birthday = null");

        User userWithWithFutureBirthday = new User((long) 0, "name@mail.ru", "Advocate",
                "name", LocalDate.of(2024, 1, 13));
        violations = validator.validate(userWithWithFutureBirthday);
        assertEquals(1, violations.size(), "Проблемы с birthday, который в будущем не пойманы");
    }
}
