package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private int id;
    @Email(message = "Email не верен, ошибка в написании")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private final String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
}
