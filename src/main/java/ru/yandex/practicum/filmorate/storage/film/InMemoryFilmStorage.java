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

    public List<Film> findFilms() {
        return new ArrayList<>(films.values());
    }

    public Film add(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    public Film find(Integer filmID) {
        return films.get(filmID);
    }

    public void remove(Integer filmID) {
        films.remove(filmID);
    }

    public Boolean isContains(int filmID) {
        return films.containsKey(filmID);
    }
}
