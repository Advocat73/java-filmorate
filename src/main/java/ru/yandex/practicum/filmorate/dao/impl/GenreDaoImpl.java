package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getFilmGenres(int filmID) {
        String sql = "select * from film_genres AS fg JOIN genres AS g ON fg.genre_id = g.id where fg.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmID);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("select * from genres", (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Genre getGenreByGenreId(int genreId) {
        Genre genre = null;
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE id = ? LIMIT 1", genreId);
        if (genreRows.next())
            genre = new Genre(genreRows.getInt("id"), genreRows.getString("name"));
        return genre;
    }

    @Override
    public void setFilmGenre(int filmId, int genreId) {
        try {
            jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) values (?, ?)", filmId, genreId);
        } catch (DuplicateKeyException ex) {
            log.info("Нарушение первичного ключа при добавлении жанра для фильма");
        } catch (DataIntegrityViolationException ex) {
            log.info("Ошибка данных при попытке добавления в таблицу");
        }
    }

    @Override
    public void removeFilmGenres(int filmId) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }
}
