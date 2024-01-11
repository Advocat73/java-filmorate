package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmStorage;

    @BeforeEach
    void beforeEach() {
        GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        MpaDao mpaDao = new MpaDaoImpl(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, mpaDao, genreDao);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testFindFilmByIdAndFindAllFilms() {
        Film newFilm = new Film(0, "newFilmName", "discriptionOfNewFilm",
                LocalDate.of(1980, 2, 2), 120, new Mpa(1, "G"));
        filmStorage.add(newFilm);
        int newFilmId = newFilm.getId();
        Film savedFilm = filmStorage.find(newFilmId);
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
        Film anotherFilm = new Film(0, "anotherFilmName", "discriptionOfAnotherFilm",
                LocalDate.of(1990, 3, 3), 130, new Mpa(2));
        filmStorage.add(anotherFilm);
        Film anotherOneFilm = new Film(0, "anotherOneFilmName", "discriptionOfanotherOneFilm",
                LocalDate.of(1980, 4, 4), 140, new Mpa(3));
        filmStorage.add(anotherOneFilm);
        List<Film> films = filmStorage.findFilms();
        assertThat(films.size()).isEqualTo(3);
        assertThat(films.get(1))
                .usingRecursiveComparison()
                .isEqualTo(anotherFilm);
        assertThat(films.get(2))
                .usingRecursiveComparison()
                .isEqualTo(anotherOneFilm);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testUpdateFilm() {
        Film newFilm = new Film(0, "newFilmName", "discriptionOfNewFilm",
                LocalDate.of(1980, 2, 2), 120, new Mpa(1, "G"));
        filmStorage.add(newFilm);
        int newFilmId = newFilm.getId();
        Film updateFilm = new Film(newFilmId, "updateFilmName", "discriptionOfUpdateFilm",
                LocalDate.of(1990, 3, 3), 130, new Mpa(2, "PG"));
        filmStorage.update(updateFilm);
        Film testFilm = filmStorage.find(newFilmId);
        assertThat(testFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isNotEqualTo(newFilm)
                .isEqualTo(updateFilm);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testIsContainsAndRemoveFilm() {
        Film newFilm = new Film(0, "newFilmName", "discriptionOfNewFilm",
                LocalDate.of(1980, 2, 2), 120, new Mpa(1, "G"));
        filmStorage.add(newFilm);
        int newFilmId = newFilm.getId();
        boolean b = filmStorage.isContains(newFilmId);
        assertThat(b).isTrue();
        filmStorage.remove(newFilmId);
        b = filmStorage.isContains(newFilmId);
        assertThat(b).isFalse();
        Film testFilm = filmStorage.find(newFilmId);
        assertThat(testFilm).isNull();
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testAddAndRemoveLikes() {
        // Создаем фильм
        Film newFilm = new Film(0, "newFilmName", "discriptionOfNewFilm",
                LocalDate.of(1980, 2, 2), 120, new Mpa(1, "G"));
        filmStorage.add(newFilm);
        int newFilmId = newFilm.getId();
        // Создаем три юзера
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User newUser1 = new User(10L, "newUser@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(2000, 2, 2));
        userStorage.add(newUser1);
        long newUser1Id = newUser1.getId();
        User newUser2 = new User(10L, "friendUser@email.ru", "sidor45", "Sidor Sidorov", LocalDate.of(1995, 3, 3));
        userStorage.add(newUser2);
        long newUser2Id = newUser2.getId();
        User newUser3 = new User(10L, "anotherFriendUser@email.ru", "Ivanov11", "Vasya Ivanov", LocalDate.of(1949, 4, 4));
        userStorage.add(newUser3);
        long newUser3Id = newUser3.getId();
        // Юзеры лайкают фильм
        filmStorage.setLike(newFilm, newUser1Id);
        filmStorage.setLike(newFilm, newUser2Id);
        filmStorage.setLike(newFilm, newUser3Id);
        // Проверяем результат добавления лайков
        Film saveFilm = filmStorage.find(newFilmId);
        Set<Long> likes = saveFilm.getLikes();
        assertThat(likes.size()).isEqualTo(3);
        // Постепенно удаляем лайки, получаем фильм из БД и проверяем результат
        filmStorage.removeLike(newFilm, newUser1Id);
        saveFilm = filmStorage.find(newFilmId);
        likes = saveFilm.getLikes();
        assertThat(likes.size()).isEqualTo(2);
        filmStorage.removeLike(newFilm, newUser3Id);
        saveFilm = filmStorage.find(newFilmId);
        likes = saveFilm.getLikes();
        assertThat(likes.toArray()[0]).isEqualTo(newUser2Id);
        filmStorage.removeLike(newFilm, newUser2Id);
        saveFilm = filmStorage.find(newFilmId);
        likes = saveFilm.getLikes();
        assertThat(likes.size()).isEqualTo(0);
    }
}
