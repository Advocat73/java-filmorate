package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private long counter = 0;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findUsers() {
        return userStorage.findUsers();
    }

    public User create(User user) {
        validate(user);
        user.setId(++counter);
        log.info("Пользователя с ID " + user.getId() + " добавляем в систему");
        return userStorage.add(user);
    }

    public User update(User user) {
        long userId = user.getId();
        if (userStorage.isContains(userId)) {
            validate(user);
            log.info("Пользователь с ID " + userId + " изменен");
            return userStorage.update(user);
        } else throw new NotFoundException("Нет пользователя с ID: " + userId);
    }

    public User findUser(Long userID) {
        if (userStorage.isContains(userID))
            return userStorage.find(userID);
        else
            throw new NotFoundException("Нет пользователя с ID: " + userID);
    }

    public void makeFriends(Long userID, Long friendID) {
        User user = findUser(userID);
        User friend = findUser(friendID);
        user.addFriend(friendID, false);
        //friend.addFriend(userID, false);
        userStorage.setFriendship(user, friend);
        log.info("Пользователm с ID " + userID + " добавил себя в друзья пользвателя с ID " + friendID);
    }

    public void removeFriends(Long userID, Long friendID) {
        User user = findUser(userID);
        User friend = findUser(friendID);
        user.removeFriend(friendID);
        friend.removeFriend(userID);
        userStorage.removeFriendship(user, friend);
        log.info("Пользователи с ID " + userID + " удалил себя из друзей пользователя с ID " + friendID);
    }


    public List<User> findFriends(Long userID) {
        User user = findUser(userID);
        Map<Long, Boolean> friends = user.getFriends();
        return friends.keySet().stream()
                .map(this::findUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userID, Long friendID) {
        User user = findUser(userID);
        User friend = findUser(friendID);
        return user.getFriends().keySet().stream()
                .filter(id -> friend.getFriends().containsKey(id))
                .map(userStorage::find)
                .collect(Collectors.toList());
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пустое -> присваиваем логин");
            user.setName(user.getLogin());
        }
    }
}
