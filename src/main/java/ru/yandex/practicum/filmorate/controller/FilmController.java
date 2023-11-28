package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.LocalDateAdapter;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int counter = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @GetMapping
    public String findFilms() {
        return gson.toJson(films.values());
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
        String filmName = film.getName();
        if (filmName != null && filmName.isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        String filmDescription = film.getDescription();
        if (filmDescription != null) {
            log.info("Длина описания фильма - " + filmDescription.length());
            if (filmDescription.length() > 200) {
                log.error("Описание фильма не может быть длинее 200 символов");
                throw new ValidationException("Описание фильма не может быть длинее 200 символов");
            }
        }
        if (film.getReleaseDate() != null && !film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        return film;
    }


}
