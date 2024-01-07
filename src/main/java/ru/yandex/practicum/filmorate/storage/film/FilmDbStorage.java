package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDao mpaDao, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    @Override
    public List<Film> findFilms() {
        ArrayList<Film> films = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * from films");
        Film film = makeFilm(filmRows);
        while (film != null) {
            films.add(film);
            film = makeFilm(filmRows);
        }
        return films;
    }

    @Override
    public Film add(Film film) {
        int mpaId = film.getMpa().getId();
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration, mpa_rate_id) values (?, ?, ?, ?, ?)",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaId);
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films ORDER BY id DESC LIMIT 1");
        if (filmRows.next())
            film.setId(filmRows.getInt("id"));
        film.setMpa(mpaDao.getMpaRating(mpaId));
        setGenresForFilm(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?,  release_date = ?, duration = ?, mpa_rate_id = ? WHERE id = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        setGenresForFilm(film);
        return film;
    }

    @Override
    public Film find(Integer filmID) {
        return makeFilm(jdbcTemplate.queryForRowSet("select * from films where id = ?", filmID));
    }

    @Override
    public void remove(Integer filmID) {
        genreDao.removeFilmGenres(filmID);
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", filmID);
    }

    @Override
    public void setLike(Film film, Long userID) {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) values (?, ?)", film.getId(), userID);
    }

    @Override
    public void removeLike(Film film, Long userID) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", film.getId(), userID);
    }

    @Override
    public Boolean isContains(int filmID) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ? LIMIT 1", filmID);
        return filmRows.first();
    }

    private Film makeFilm(SqlRowSet filmRows) {
        Film film = null;
        if (filmRows.next()) {
            film = new Film(
                    filmRows.getInt("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    mpaDao.getMpaRating(filmRows.getInt("mpa_rate_id")));
        }
        if (film != null) {
            SqlRowSet likesRows = jdbcTemplate.queryForRowSet("select * from likes where film_id = ?", film.getId());
            while (likesRows.next())
                film.addLike(likesRows.getLong("user_id"));
            film.setGenres(genreDao.getFilmGenres(film.getId()));
        }
        return film;
    }

    private void setGenresForFilm(Film film) {
        int filmId = film.getId();
        genreDao.removeFilmGenres(filmId);
        for (Genre g : film.getGenres())
            genreDao.setFilmGenre(filmId, g.getId());
        film.setGenres(genreDao.getFilmGenres(film.getId()));
    }
}

