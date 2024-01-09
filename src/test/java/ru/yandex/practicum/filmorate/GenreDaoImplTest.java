package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoImplTest {
    private final Genre[] genres = {new Genre(0, "Заглушка"),
            new Genre(1, "Комедия"), new Genre(2, "Драма"),
            new Genre(3, "Мультфильм"), new Genre(4, "Триллер"),
            new Genre(5, "Документальный"), new Genre(6, "Боевик")};

    private final JdbcTemplate jdbcTemplate;
    private GenreDaoImpl genreDaoImpl;

    @BeforeEach
    void beforeEach() {
        genreDaoImpl = new GenreDaoImpl(jdbcTemplate);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testGetGenreByRandomGenreId() {
        int randomIndex = new Random().nextInt(6) + 1;
        Genre randomGenre = genreDaoImpl.getGenreByGenreId(randomIndex);
        assertThat(randomGenre).usingRecursiveComparison().isEqualTo(genres[randomIndex]);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testGetAllGenres() {
        List<Genre> getGenres = genreDaoImpl.getAllGenres();
        for (int i = 0; i < getGenres.size(); i++) {
            assertThat(getGenres.get(i)).usingRecursiveComparison().isEqualTo(genres[i + 1]);
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testSetFilmGenre() {
        MpaDao mpaDao = new MpaDaoImpl(jdbcTemplate);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, mpaDao, genreDaoImpl);
        Film newFilm = new Film(0, "newFilmName", "discriptionOfNewFilm",
                LocalDate.of(1980, 2, 2), 120, new Mpa(1, "G"));
        filmStorage.add(newFilm);
        int newFilmId = newFilm.getId();
        genreDaoImpl.setFilmGenre(newFilmId, 1);
        genreDaoImpl.setFilmGenre(newFilmId, 2);
        genreDaoImpl.setFilmGenre(newFilmId, 3);
        List<Genre> filmGenres = genreDaoImpl.getFilmGenres(newFilmId);
        assertThat(filmGenres.size()).isEqualTo(3);
        assertThat(filmGenres.get(0).getName()).isEqualTo(genres[1].getName());
        assertThat(filmGenres.get(1).getName()).isEqualTo(genres[2].getName());
        assertThat(filmGenres.get(2).getName()).isEqualTo(genres[3].getName());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testSetFilmGenreIfErrorData() {
        MpaDao mpaDao = new MpaDaoImpl(jdbcTemplate);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, mpaDao, genreDaoImpl);
        Film newFilm = new Film(0, "newFilmName", "discriptionOfNewFilm",
                LocalDate.of(1980, 2, 2), 120, new Mpa(1, "G"));
        filmStorage.add(newFilm);
        int newFilmId = newFilm.getId();
        genreDaoImpl.setFilmGenre(newFilmId, 1);
        genreDaoImpl.setFilmGenre(newFilmId, 1);
        genreDaoImpl.setFilmGenre(newFilmId, 999);
        genreDaoImpl.setFilmGenre(999, 2);
        List<Genre> filmGenres = genreDaoImpl.getFilmGenres(newFilmId);
        assertThat(filmGenres.size()).isEqualTo(1);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testRemoveGenresFromFilm() {
        MpaDao mpaDao = new MpaDaoImpl(jdbcTemplate);
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, mpaDao, genreDaoImpl);
        Film newFilm = new Film(0, "newFilmName", "discriptionOfNewFilm",
                LocalDate.of(1980, 2, 2), 120, new Mpa(1, "G"));
        filmStorage.add(newFilm);
        int newFilmId = newFilm.getId();
        genreDaoImpl.setFilmGenre(newFilmId, 1);
        genreDaoImpl.setFilmGenre(newFilmId, 2);
        genreDaoImpl.setFilmGenre(newFilmId, 3);
        genreDaoImpl.removeFilmGenres(newFilmId);
        List<Genre> filmGenres = genreDaoImpl.getFilmGenres(newFilmId);
        assertThat(filmGenres.size()).isEqualTo(0);
    }
}
