package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.LocalDateAdapter;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int counter = 0;
    private final Map<Integer, User> users = new HashMap<>();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @GetMapping
    public String findUsers() {
        return gson.toJson(users.values());
    }
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validate(user);
        user.setId(++counter);
        log.info("Пользователь с ID " + user.getId() + " добавляем в систему");
        users.put(counter, user);
        return user;
    }
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        int userId = user.getId();
        if (users.containsKey(userId)) {
            validate(user);
            log.info("Пользователь с ID " + userId + " изменен");
            users.put(userId, user);
        } else {
            log.error("Нет пользователя с ID: " + userId);
            throw new ValidationException("Нет пользователя с ID: " + userId);
        }
        return user;
    }
    private User validate(User user) {
        String userEmail = user.getEmail();
        if (userEmail != null && (userEmail.isBlank() || !userEmail.contains("@"))) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        String userLogin = user.getLogin();
        if (userLogin != null && (userLogin.isBlank() || userLogin.contains(" "))) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пустое -> присваиваем логин");
            user.setName(userLogin);
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        return user;
    }
}
