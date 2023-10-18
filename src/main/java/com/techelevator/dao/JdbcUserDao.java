package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The JdbcUserDao class is used for interacting with the user information in the datastore.

 * While the DAO pattern allows us to encapsulate and abstract interactions with a data store,
 * this implementation class is specifically used to access data from a SQL database using JDBC.

 * This DAO supports only Create and Read access for Users.
 * Note that password information is salted and hashed prior to being stored in the database.
 */
@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public User getUserById(int userId) {
        User user = null;

        String sql = "SELECT user_id, username, password_hash, role, display_name, img_url, short_bio FROM app_user WHERE user_id = ?";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                user =  mapRowToUser(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        try {
            // Intentionally excluding password_hash - Not a good idea to allow mass selection of user password data (even if hashed).
            String sql = "SELECT user_id, username, role, display_name, img_url, short_bio FROM app_user ORDER BY username;";

            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                User user = mapRowToUser(results);
                users.add(user);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return users;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = null;

        String sql = "SELECT user_id, username, password_hash, role, display_name, img_url, short_bio FROM app_user WHERE username = LOWER(TRIM(?))";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            if (results.next()) {
                user = mapRowToUser(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return user;
    }

    @Override
    public User createUser(String username, String password, String role) {
        User newUser = null;

        String insertUserSql = "INSERT INTO app_user (username,password_hash,role) VALUES (LOWER(TRIM(?)), ?, ?) RETURNING user_id";

        String password_hash = new BCryptPasswordEncoder().encode(password);
        String ssRole = role.toUpperCase().startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();

        try {
            int userId = jdbcTemplate.queryForObject(insertUserSql, int.class, username, password_hash, ssRole);
            newUser = getUserById(userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return newUser;
    }

    @Override
    public User updateUser(User modifiedUser) {
        User updatedUser = null;

        String shortBio = modifiedUser.getShortBio();
        String displayName = modifiedUser.getDisplayName();
        String imgUrl = modifiedUser.getProfileImageUrl();
        // Make sure values are not too long.
        if (shortBio.length() > 500) {
            shortBio = shortBio.substring(0, 499);
        }
        if (displayName.length() > 50) {
            displayName = modifiedUser.getDisplayName().substring(0, 49);
        }
        if (imgUrl.length() > 500) {
            imgUrl = modifiedUser.getProfileImageUrl().substring(0, 499);
        }

        String sql = "UPDATE app_user SET display_name=?, img_url=?, short_bio=? WHERE user_id=?;";

        try {
            int rowsAffected = jdbcTemplate.update(sql, displayName, imgUrl, shortBio, modifiedUser.getId());
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
            updatedUser = getUserById(modifiedUser.getId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return updatedUser;
    }

    /*
     * Helper method to convert a SqlRowSet into a User object.
     */
    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        // Password column is not always included
        if (hasColumnName(rs, "password_hash")) {
            user.setPassword(rs.getString("password_hash"));
        }
        user.setAuthorities(Objects.requireNonNull(rs.getString("role")));
        user.setDisplayName(rs.getString("display_name"));
        user.setProfileImageUrl(rs.getString("img_url"));
        user.setShortBio(rs.getString("short_bio"));
        return user;
    }

    /*
     * Helper method to determine if a SqlRowSet contains a particular column.
     * Used by the mapRowToUser method to check if the password_hash is included.
     */
    private boolean hasColumnName(SqlRowSet rs, String columnName) {
        SqlRowSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();

        // resultSet column indexes start from 1
        for (int i = 1; i < numberOfColumns + 1; i++) {
            String currentColumn = rsMetaData.getColumnName(i);
            if (columnName.equalsIgnoreCase(currentColumn)) {
                return true;
            }
        }
        return false;
    }
}
