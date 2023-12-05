package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public List<User> findUsers();

    public User add(User user);

    public User find(Long userID);

    public void remove(Long userID);

    public Boolean isContains(Long userID);
}
