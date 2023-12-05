package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping
    public List<Film> findFilms() {
        return filmService.findFilms();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable("id") Integer filmID) {
        return filmService.find(filmID);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable("id") Integer filmID) {
        filmService.remove(filmID);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmID, @PathVariable("userId") Long userId) {
        filmService.addLike(filmID, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Integer filmID, @PathVariable("userId") Long userID) {
        filmService.removeLike(filmID, userID);
    }

    @GetMapping("/popular")
    List<Film> findMostPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.findMostPopularFilms(count);
    }
}
