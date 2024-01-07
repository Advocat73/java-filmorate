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

    @Override
    public List<User> findUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        return add(user);
    }

    @Override
    public User find(Long userID) {
        return users.get(userID);
    }

    @Override
    public void remove(Long userID) {
        users.remove(userID);
    }

    @Override
    public Boolean isContains(Long userID) {
        return users.containsKey(userID);
    }

    @Override
    public void setFriendship(User friend1, User friend2) {
        add(friend1);
        //add(friend2);
    }

    @Override
    public void removeFriendship(User friend1, User friend2) {
        add(friend1);
        add(friend2);
    }
}