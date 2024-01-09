package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public List<User> findUsers();

    public User add(User user);

    public User update(User user);

    public User find(long userId);

    public void remove(long userId);

    public Boolean isContains(long userId);

    public void setFriendship(User friend1, User friend2);

    public void removeFriendship(User friend1, User friend2);

    public List<Long> findFriends(long userId);
}
