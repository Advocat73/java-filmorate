package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
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
        log.info("Пользователь с ID " + user.getId() + " добавляем в систему");
        return userStorage.add(user);
    }

    public User update(User user) {
        long userId = user.getId();
        if (userStorage.isContains(userId)) {
            validate(user);
            log.info("Пользователь с ID " + userId + " изменен");
            return userStorage.add(user);
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
        user.addFriend(friendID);
        friend.addFriend(userID);
        log.info("Пользователи с ID " + userID + " и " + friendID + " подружились!");
    }

    public void removeFriends(Long userID, Long friendID) {
        User user = findUser(userID);
        User friend = findUser(friendID);
        user.removeFriend(friendID);
        friend.removeFriend(userID);
        log.info("Пользователи с ID " + userID + " и " + friendID + " перестали дружить!");
    }


    public List<User> findFriends(Long userID) {
        User user = findUser(userID);
        Set<Long> friends = user.getFriends();
        return friends.stream()
                .map(this::findUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userID, Long friendID) {
        User user = findUser(userID);
        User friend = findUser(friendID);
        return user.getFriends().stream()
                .filter(id -> friend.getFriends().contains(id))
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
