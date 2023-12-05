package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public List<User> findUsers() {
        return new ArrayList<>(users.values());
    }

    public User add(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User find(Long userID) {
        return users.get(userID);
    }

    public void remove(Long userID) {
        users.remove(userID);
    }


    public Boolean isContains(Long userID) {
        return users.containsKey(userID);
    }
}