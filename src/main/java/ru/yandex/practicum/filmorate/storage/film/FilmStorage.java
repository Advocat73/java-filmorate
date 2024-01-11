package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public List<Film> findFilms();

    public Film add(Film film);

    public Film update(Film film);

    public Film find(Integer filmID);

    public void remove(Integer filmID);

    public void setLike(Film film, Long userID);

    public void removeLike(Film film, Long userID);

    public Boolean isContains(int filmID);
}
