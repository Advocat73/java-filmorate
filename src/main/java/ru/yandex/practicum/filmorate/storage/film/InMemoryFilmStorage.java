package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        return add(film);
    }

    @Override
    public Film find(Integer filmID) {
        return films.get(filmID);
    }

    @Override
    public void remove(Integer filmID) {
        films.remove(filmID);
    }

    @Override
    public void setLike(Film film, Long userID) {
        update(film);
    }

    @Override
    public void removeLike(Film film, Long userID) {
        update(film);
    }

    @Override
    public Boolean isContains(int filmID) {
        return films.containsKey(filmID);
    }
}
