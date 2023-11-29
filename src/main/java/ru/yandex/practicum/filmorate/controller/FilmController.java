package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int counter = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection findFilms() {
        return films.values();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(++counter);
        log.info("Фильм с ID " + film.getId() + " добавлнен в систему");
        films.put(counter, film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        int filmId = film.getId();
        if (films.containsKey(filmId)) {
            validate(film);
            log.info("Фильм с ID " + filmId + " изменен");
            films.put(filmId, film);
        } else {
            log.error("Нет фильма с ID: " + filmId);
            throw new ValidationException("Нет фильма с ID: " + filmId);
        }
        return film;
    }

    private Film validate(Film film) {
        return film;
    }


}
