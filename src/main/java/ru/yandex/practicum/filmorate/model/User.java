package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.NotWithSpace;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @Email(message = "Email не верен, ошибка в написании")
    private final String email;
    @NotWithSpace(message = "Логин не может быть пустым и содержать пробелы")
    private final String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
    private final Map<Long, Boolean> friends = new HashMap<>();

    public void addFriend(Long friendID, boolean isFriendAccept) {
        friends.put(friendID, isFriendAccept);
    }

    public void addFriends(Map<Long, Boolean> friends) {
        if (friends != null) this.friends.putAll(friends);
    }

    public void removeFriend(Long friendID) {
        friends.remove(friendID);
    }
}

