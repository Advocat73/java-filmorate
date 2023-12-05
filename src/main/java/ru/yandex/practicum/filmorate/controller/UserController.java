package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public List<User> findUsers() {
        return userService.findUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/{userId}")
    public User findUser(@PathVariable("userId") Long userID) {
        return userService.findUser(userID);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long userID, @PathVariable("friendId") Long friendID) {
        userService.makeFriends(userID, friendID);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") Long userID, @PathVariable("friendId") Long friendID) {
        userService.removeFriends(userID, friendID);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable("id") Long userID) {
        return userService.findFriends(userID);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("id") Long userID, @PathVariable("otherId") Long otherID) {
        return userService.getCommonFriends(userID, otherID);
    }
}
