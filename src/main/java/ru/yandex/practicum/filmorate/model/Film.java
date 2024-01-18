package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Длина описания фильма должна быть не больше {max} символов")
    private final String description;
    private final List<Genre> genres = new ArrayList<>();
    @MinimumDate
    private final LocalDate releaseDate;
    @PositiveOrZero(message = "Продолжительность фильма должна быть положительной")
    private final int duration;
    private final Set<Long> likes = new HashSet<>();
    private Mpa mpa;

    public void addLike(long like) {
        likes.add(like);
    }

    public void addLikes(Set<Long> likes) {
        if (likes != null)
            this.likes.addAll(likes);
    }

    public void removeLike(long like) {
        likes.remove(like);
    }

    public void setGenres(List<Genre> genres) {
        if (genres != null) {
            this.genres.clear();
            this.genres.addAll(genres);
        }
    }
}

