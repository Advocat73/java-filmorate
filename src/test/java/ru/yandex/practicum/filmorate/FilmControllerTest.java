package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTest {
    private Validator validator;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    void beforeEach() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void isCreateFilmWithAllArgumentsNotGood() {
        Film film = new Film(0, "  ", "FilmDescription AboutFilmDescription " +
                "WhenFilmDescription Too Long So FilmDescription Must Be Change In Order To FilmDescription Not To Be So Long " +
                "BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA",
                LocalDate.of(1885, 1, 13), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(3, violations.size(), "Не все проблемы пойманы");
    }

    @Test
    void isGoodFilmAddAndGetGoodID() {
        Film film = new Film(0, "FilmName", "FilmDescription",
                LocalDate.of(1985, 1, 13), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void isFilmNotCreateWithNameNullOrEmptyName() {
        Film filmWithNullName = new Film(0, null, "FilmDescription",
                LocalDate.of(1985, 1, 13), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(filmWithNullName);
        assertEquals(1, violations.size(), "Проблемы с name не пойманы");

        Film filmWithEmptyName = new Film(0, " ", "FilmDescription",
                LocalDate.of(1985, 1, 13), 120);
        violations = validator.validate(filmWithEmptyName);
        assertEquals(1, violations.size(), "Проблемы с name не пойманы");
    }

    @Test
    void isFilmCreateAndAddWithDescriptionNullAndNotCreateIfDiscriptMoreThan200() {
        Film filmWithNullDescription = new Film(0, "FilmName", null,
                LocalDate.of(1985, 1, 13), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(filmWithNullDescription);
        assertTrue(violations.isEmpty());

        Film filmWithTooLondDescript = new Film(0, "FilmName", "FilmDescription AboutFilmDescription " +
                "WhenFilmDescription Too Long So FilmDescription Must Be Change In Order To FilmDescription Not To Be So Long " +
                "BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA BLA",
                LocalDate.of(1985, 1, 13), 120);
        violations = validator.validate(filmWithTooLondDescript);
        assertEquals(1, violations.size(), "Проблемы с description не пойманы");
    }

    @Test
    void isFilmCreateNadAddWithReleaseNullAndNotCreateIfReleaseBefore28121895() {
        Film filmWithNullRelease = new Film(0, "FilmName", "FilmDescription", null, 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(filmWithNullRelease);
        assertTrue(violations.isEmpty());

        Film filmWithReleaseBefore28121895 = new Film(0, "FilmName", "FilmDescription",
                LocalDate.of(1885, 1, 13), 120);
        violations = validator.validate(filmWithReleaseBefore28121895);
        assertEquals(1, violations.size(), "Проблемы с DateRelease не пойманы");
    }

    @Test
    void isFilmNotCreateIfDurationNegative() {
        Film filmWithNegativeDuration = new Film(0, "FilmName", "FilmDescription",
                LocalDate.of(1985, 1, 13), -100);
        Set<ConstraintViolation<Film>> violations = validator.validate(filmWithNegativeDuration);
        assertEquals(1, violations.size(), "Проблемы с Duration не пойманы");
    }
}
