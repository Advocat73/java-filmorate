package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private Validator validator;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private FilmController filmController;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
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
        filmController.add(film);
        assertEquals(1, film.getId(), "ID NOT GOOD");
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
        filmController.add(filmWithNullDescription);
        assertEquals(1, filmWithNullDescription.getId(), "ID NOT GOOD");

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
        filmController.add(filmWithNullRelease);
        assertEquals(1, filmWithNullRelease.getId(), "ID NOT GOOD");

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

    @Test
    void isFilmUpdateWithGoodIDAndNotUpdateWithNotExistID() {
        Film film = new Film(0, null, null, null, 0);
        filmController.add(film);
        Film updateFilm = new Film(film.getId(), "FilmName", "FilmDescription",
                LocalDate.of(1985, 1, 13), 120);
        filmController.update(updateFilm);
        assertEquals(1, updateFilm.getId(), "UPDATE: ID NOT GOOD");
        assertEquals("FilmName", updateFilm.getName(), "UPDATE_FILM: NAME NOT GOOD");
        assertEquals("FilmDescription", updateFilm.getDescription(), "UPDATE_FILM: DESCRIPTION NOT GOOD");
        assertEquals("1985-01-13", updateFilm.getReleaseDate().format(formatter), "UPDATE_FILM: RELEASE NOT GOOD");
        assertEquals(120, updateFilm.getDuration(), "UPDATE_FILM: DURATION NOT GOOD");

        Film updateFilmWithNotExistID = new Film(999, "FilmName", "FilmDescription",
                LocalDate.of(1985, 1, 13), 120);
        ValidationException exception = assertThrows(
                ValidationException.class, () -> {
                    filmController.update(updateFilmWithNotExistID);
                });
        assertEquals("Нет фильма с ID: 999", exception.getMessage(), "UPDATE: No ValidationException");
    }
}
