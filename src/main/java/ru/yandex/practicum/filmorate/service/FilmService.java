package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    @Autowired
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private int counter = 0;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findFilms() {
        return filmStorage.findFilms();
    }

    public Film add(Film film) {
        film.setId(++counter);
        Film tmpFilm = filmStorage.add(film);
        log.info("Фильм с ID " + tmpFilm.getId() + " добавлен");
        return tmpFilm;
    }

    public Film update(Film film) {
        int filmId = film.getId();
        if (filmStorage.isContains(filmId)) {
            log.info("Фильм с ID " + filmId + " изменен");
            return filmStorage.update(film);
        } else throw new NotFoundException("Нет фильма с ID: " + filmId);
    }

    public Film find(Integer filmId) {
        if (filmStorage.isContains(filmId)) {
            log.info("Фильм с ID " + filmId + " найден");
            return filmStorage.find(filmId);
        } else throw new NotFoundException("Нет фильма с ID: " + filmId);
    }

    public void remove(Integer filmId) {
        if (filmStorage.isContains(filmId)) {
            log.info("Фильм с ID " + filmId + " удален");
            filmStorage.remove(filmId);
        } else throw new NotFoundException("Нет фильма с ID: " + filmId);
    }


    public List<Film> findMostPopularFilms(int size) {
        List<Film> films = findFilms();
        return films.stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(size)
                .collect(Collectors.toList());
    }

    public void addLike(Integer filmId, Long userId) {
        if (userStorage.isContains(userId)) {
            Film film = find(filmId);
            film.addLike(userId);
            log.info("Пользователь с ID " + userId + " лайкнул фильм с ID " + filmId);
            filmStorage.setLike(film, userId);
        } else
            throw new NotFoundException("Нет пользователя с ID: " + userId);
    }

    public void removeLike(Integer filmId, Long userId) {
        if (userStorage.isContains(userId)) {
            Film film = find(filmId);
            film.removeLike(userId);
            log.info("Пользователь с ID " + userId + " удалил лайк фильма с ID " + filmId);
            filmStorage.removeLike(film, userId);
        } else
            throw new NotFoundException("Нет пользователя с ID: " + userId);
    }
}
