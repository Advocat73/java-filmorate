package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;
    private final List<Mpa> mpaList;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        mpaList = jdbcTemplate.query("select * from mpa_rates", (rs, rowNum) ->
                new Mpa(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public Mpa getMpaRating(int mpaId) {
        for (Mpa mpaTmp : mpaList)
            if (mpaTmp.getId() == mpaId)
                return new Mpa(mpaTmp.getId(), mpaTmp.getName());
        return null;
    }

    @Override
    public List<Mpa> getAllMpaRatings() {
        return mpaList;
    }
}
