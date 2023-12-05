package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private int counter = 0;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findFilms() {
        return filmStorage.findFilms();
    }

    public Film add(Film film) {
        film.setId(++counter);
        log.info("Фильм с ID " + film.getId() + " добавлнен в систему");
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        int filmID = film.getId();
        if (filmStorage.isContains(filmID)) {
            log.info("Фильм с ID " + filmID + " изменен");
            return filmStorage.add(film);
        } else throw new NotFoundException("Нет фильма с ID: " + filmID);
    }

    public Film find(Integer filmID) {
        if (filmStorage.isContains(filmID))
            return filmStorage.find(filmID);
        else throw new NotFoundException("Нет фильма с ID: " + filmID);
    }

    public void remove(Integer filmID) {
        if (filmStorage.isContains(filmID)) {
            log.info("Фильм с ID " + filmID + " удален");
            filmStorage.remove(filmID);
        } else throw new NotFoundException("Нет фильма с ID: " + filmID);
    }

    public List<Film> findMostPopularFilms(int size) {
        List<Film> films = findFilms();
        return films.stream()
                .sorted(this::compare)
                .limit(size)
                .collect(Collectors.toList());
    }

    public void addLike(Integer filmID, Long userID) {
        Film film = find(filmID);
        checkUserID(userID);
        film.addLike(userID);
        filmStorage.add(film);
    }

    public void removeLike(Integer filmID, Long userID) {
        Film film = find(filmID);
        checkUserID(userID);
        film.removeLike(userID);
        filmStorage.add(film);
    }

    private int compare(Film f1, Film f2) {
        return -1 * (f1.getLikes().size() - f2.getLikes().size());
    }

    private void checkUserID(Long userID) {
        if (userID < 0)
            throw new NotFoundException("ID пользователя не может быть отрицательным");
    }
}
