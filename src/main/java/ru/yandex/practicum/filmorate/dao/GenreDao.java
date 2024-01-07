package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    List<Genre> getFilmGenres(int filmID);

    Genre getGenreByGenreId(int genreId);

    List<Genre> getAllGenres();

    void setFilmGenre(int filmId, int genreId);

    void removeFilmGenres(int filmId);
}
