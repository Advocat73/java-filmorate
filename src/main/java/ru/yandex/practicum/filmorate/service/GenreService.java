package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    @Autowired
    private final GenreDao genreDao;

    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Genre findGenreByGenreId(Integer genreId) {
        Genre genre = genreDao.getGenreByGenreId(genreId);
        if (genre == null)
            throw new NotFoundException("Нет жанра с ID: " + genreId);
        log.info("Жанр с ID " + genreId + " это " + genre.getName());
        return genre;
    }

    public List<Genre> findAllGenres() {
        return genreDao.getAllGenres();
    }
}