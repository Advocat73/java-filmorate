package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Длина описания фильма должна быть не больше {max} символов")
    private final String description;
    private Genre genre;
    @MinimumDate
    private final LocalDate releaseDate;
    @PositiveOrZero(message = "Продолжительность фильма должна быть положительной")
    private final int duration;
    private final Set<Long> likes = new HashSet<>();
    private RatingMPA rating;

    public void addLike(long like) {
        likes.add(like);
    }

    public void removeLike(long like) {
        likes.remove(like);
    }
}

