package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int counter = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findUsers() {
        return users.values();
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
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пустое -> присваиваем логин");
            user.setName(user.getLogin());
        }
        return user;
    }
}
