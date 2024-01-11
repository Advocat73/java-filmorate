package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDaoImplTest {
    private final Mpa[] mpaTypes = {new Mpa(0, "Заглушка"),
            new Mpa(1, "G"), new Mpa(2, "PG"),
            new Mpa(3, "PG-13"), new Mpa(4, "R"), new Mpa(5, "NC-17")};

    private final JdbcTemplate jdbcTemplate;

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testGetMpaTypeByRandomMpaId() {
        MpaDaoImpl mpaDaoImpl = new MpaDaoImpl(jdbcTemplate);
        int randomIndex = new Random().nextInt(5) + 1;
        Mpa randomMpa = mpaDaoImpl.getMpaRating(randomIndex);
        assertThat(randomMpa).usingRecursiveComparison().isEqualTo(mpaTypes[randomIndex]);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testGetAllMpaTypes() {
        MpaDaoImpl mpaDaoImpl = new MpaDaoImpl(jdbcTemplate);
        List<Mpa> getMpaTypes = mpaDaoImpl.getAllMpaRatings();
        for (int i = 0; i < getMpaTypes.size(); i++) {
            assertThat(getMpaTypes.get(i)).usingRecursiveComparison().isEqualTo(mpaTypes[i + 1]);
        }
    }
}
