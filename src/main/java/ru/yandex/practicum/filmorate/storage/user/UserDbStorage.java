package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findUsers() {
        ArrayList<User> users = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * from users");
        User user = makeUser(userRows);
        while (user != null) {
            users.add(user);
            user = makeUser(userRows);
        }
        return users;
    }

    @Override
    public User add(User user) {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) values (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users ORDER BY id DESC LIMIT 1");
        if (userRows.next())
            user.setId(userRows.getLong("id"));
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User find(Long userID) {
        return makeUser(jdbcTemplate.queryForRowSet("select * from users where id = ?", userID));
    }

    @Override
    public void remove(Long userID) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", userID);
    }

    @Override
    public Boolean isContains(Long userID) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ? LIMIT 1", userID);
        return userRows.first();
    }

    @Override
    public void setFriendship(User friend1, User friend2) {
        Boolean isFriendshipAccepted = friend1.getFriends().get(friend2.getId());
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, isAccepted) values (?, ?, ?)",
                friend1.getId(), friend2.getId(), isFriendshipAccepted);
        //jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, isAccepted) values (?, ?, ?)",
        //        friend2.getId(), friend1.getId(), isFriendshipAccepted);
    }

    @Override
    public void removeFriendship(User friend1, User friend2) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", friend1.getId(), friend2.getId());
        //jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", friend2.getId(), friend1.getId());
    }

    private User makeUser(SqlRowSet userRows) {
        User user = null;
        if (userRows.next()) {
            user = new User(
                    userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
        }
        if (user != null) {
            SqlRowSet friendRows = jdbcTemplate.queryForRowSet("select * from friends where user_id = ?", user.getId());
            while (friendRows.next())
                user.addFriend(friendRows.getLong("friend_id"), friendRows.getBoolean("isAccepted"));
        }
        return user;
    }
}
