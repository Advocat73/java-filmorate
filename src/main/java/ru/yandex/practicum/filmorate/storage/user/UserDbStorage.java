package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("checkstyle:SeparatorWrap")
    @Override
    public List<User> findUsers() {
        List<Map<String, Object>> friendsDatabaseResult = jdbcTemplate.queryForList("SELECT * from friends");
        Map<Long, Map<Long, Boolean>> friends = new HashMap<>();
        friendsDatabaseResult.forEach(map -> friends.computeIfAbsent((Long) map.get("user_id"), k -> new HashMap<>())
                .put((Long) map.get("friend_id"), (Boolean) map.get("isAccepted")));
        List<User> users = jdbcTemplate.query("SELECT * from users", (rs, rowNum) -> makeUserForList(rs));
        users.forEach(user -> user.addFriends(friends.get(user.getId())));
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
    public User find(long userId) {
        return makeUser(jdbcTemplate.queryForRowSet("select * from users where id = ?", userId));
    }

    @Override
    public void remove(long userId) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }

    @Override
    public Boolean isContains(long userID) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ? LIMIT 1", userID);
        return userRows.first();
    }

    @Override
    public void setFriendship(User friend1, User friend2) {
        Boolean isFriendshipAccepted = friend1.getFriends().get(friend2.getId());
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, isAccepted) values (?, ?, ?)",
                friend1.getId(), friend2.getId(), isFriendshipAccepted);
    }

    @Override
    public void removeFriendship(User friend1, User friend2) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", friend1.getId(), friend2.getId());
    }

    @Override
    public List<Long> findFriends(long userId) {
        List<Long> friends = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM friends WHERE friend_id = ?", userId);
        while (userRows.next())
            friends.add(userRows.getLong("user_id"));
        return friends;
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

    private User makeUserForList(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
    }
}

