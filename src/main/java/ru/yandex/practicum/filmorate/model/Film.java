package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Длина описания фильма должна быть не больше {max} символов")
    private final String description;
    @MinimumDate
    private final LocalDate releaseDate;
    @PositiveOrZero(message = "Продолжительность фильма должна быть положительной")
    private final int duration;
}

