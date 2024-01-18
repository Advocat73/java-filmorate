package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Mpa[] mpaTypes = {new Mpa(0, "Заглушка"),
            new Mpa(1, "G"), new Mpa(2, "PG"),
            new Mpa(3, "PG-13"), new Mpa(4, "R"), new Mpa(5, "NC-17")};
    private static final Genre[] genres = {new Genre(0, "Заглушка"),
            new Genre(1, "Комедия"), new Genre(2, "Драма"),
            new Genre(3, "Мультфильм"), new Genre(4, "Триллер"),
            new Genre(5, "Документальный"), new Genre(6, "Боевик")};
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film) {
        for (Mpa mpaType : mpaTypes)
            if (film.getMpa() != null && film.getMpa().getId() == mpaType.getId()) {
                film.setMpa(mpaType);
                break;
            }

        if (film.getGenres() != null && film.getGenres().size() > 0) {
            List<Genre> genresList = new ArrayList<>();
            for (Genre genre : genres)
                for (Genre g : film.getGenres())
                    if (g.getId() == genre.getId() && !genresList.contains(genre))
                        genresList.add(genre);
            film.setGenres(genresList);
        }

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        return add(film);
    }

    @Override
    public Film find(Integer userId) {
        return films.get(userId);
    }

    @Override
    public void remove(Integer userId) {
        films.remove(userId);
    }

    @Override
    public void setLike(Film film, Long userId) {
        film.addLike(userId);
        update(film);
    }

    @Override
    public void removeLike(Film film, Long userID) {
        update(film);
    }

    @Override
    public Boolean isContains(int userId) {
        return films.containsKey(userId);
    }
}
