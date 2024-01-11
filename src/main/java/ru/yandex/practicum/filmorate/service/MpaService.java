package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    @Autowired
    private final MpaDao mpaDao;

    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Mpa findMpaRating(Integer mpaId) {
        Mpa mpa = mpaDao.getMpaRating(mpaId);
        if (mpa == null)
            throw new NotFoundException("Нет рейтингаMPA с ID: " + mpaId);
        log.info("Рейтинг MPA с ID " + mpaId + " это " + mpa.getName());
        return mpa;
    }

    public List<Mpa> findAllMpaRatings() {
        return mpaDao.getAllMpaRatings();
    }
}