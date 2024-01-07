package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaRating(int mpaId) {
        Mpa mpa = null;
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa_rates where id = ?", mpaId);
        if (mpaRows.next())
            mpa = new Mpa(mpaRows.getInt("id"), mpaRows.getString("name"));
        return mpa;
    }

    @Override
    public List<Mpa> getAllMpaRatings() {
        return jdbcTemplate.query("select * from mpa_rates", (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }
}
