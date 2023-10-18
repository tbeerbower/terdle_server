package com.techelevator.dao;

import com.techelevator.model.Game;
import com.techelevator.model.UserGame;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class JdbcUserGameDao implements UserGameDao {
    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<UserGame> MAPPER = new RowMapper<UserGame>() {
        @Override
        public UserGame mapRow(ResultSet rs, int rowNum) throws SQLException {
            int userId = rs.getInt("user_id");
            int gameId = rs.getInt("game_id");
            int guesses = rs.getInt("guesses");
            String word = rs.getString("word");
            LocalDate date = rs.getDate("game_date").toLocalDate();
            Game.Type type = Game.Type.values()[rs.getInt("type")];
            boolean success = rs.getBoolean("success");
            UserGame userGame = new UserGame(userId, gameId, word, date, type, success);
            List<String> guessList = userGame.getGuesses();
            for (int i = 1; i <= guesses; ++i) {
                String guess = rs.getString(String.format("guess%d", i));
                guessList.add(guess);
            }
            return userGame;
        }
    };

    public JdbcUserGameDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UserGame> getUserGames() {
        return jdbcTemplate.query(
                "SELECT * FROM user_game ug JOIN game g ON ug.game_id = g.game_id", MAPPER);
    }

    @Override
    public List<UserGame> getUserGamesByUserId(int userId, LocalDate date, Game.Type type) {
        if (date != null && type != null) {
            return jdbcTemplate.query("SELECT * FROM user_game ug JOIN game g ON ug.game_id = g.game_id " +
                    "WHERE ug.user_id = ? AND g.game_date = ? AND g.type =?", MAPPER, userId, date, type.ordinal());
        }
        if (date != null) {
            return jdbcTemplate.query("SELECT * FROM user_game ug JOIN game g ON ug.game_id = g.game_id " +
                            "WHERE ug.user_id = ? AND g.game_date = ?", MAPPER, userId, date);
        }
        if (type != null) {
            return jdbcTemplate.query("SELECT * FROM user_game ug JOIN game g ON ug.game_id = g.game_id " +
                            "WHERE ug.user_id = ? AND g.type =?", MAPPER, userId, type.ordinal());
        }
        return jdbcTemplate.query("SELECT * FROM user_game ug JOIN game g ON ug.game_id = g.game_id WHERE ug.user_id = ?", MAPPER, userId);
    }

    public UserGame getUserGameByUserIdAndGameId(int userId, int gameId) {
        List<UserGame> userGames = jdbcTemplate.query("SELECT * FROM user_game ug JOIN game g ON ug.game_id = g.game_id WHERE ug.user_id = ? AND ug.game_id = ?",
                MAPPER, userId, gameId);
        return userGames.isEmpty() ? null : userGames.get(0);
    }

    @Override
    public UserGame createUserGame(int userId, int gameId, UserGame newUserGame) {
        String sql = "INSERT INTO user_game (user_id, game_id, guesses, " +
                "guess1, guess2, guess3, guess4, guess5, guess6, success) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        List<String> guessList = newUserGame.getGuesses();
        int guesses = guessList.size();
        int rowsUpdated = jdbcTemplate.update(sql, userId, gameId, guesses,
                guesses > 0 ? guessList.get(0) : null,
                guesses > 1 ? guessList.get(1) : null,
                guesses > 2 ? guessList.get(2) : null,
                guesses > 3 ? guessList.get(3) : null,
                guesses > 4 ? guessList.get(4) : null,
                guesses > 5 ? guessList.get(5) : null,
                newUserGame.isSuccess());
        return rowsUpdated == 1 ? getUserGameByUserIdAndGameId(userId, gameId) : null;
    }

    @Override
    public UserGame updateUserGame(int userId, int gameId, UserGame modifiedUserGame, boolean success) {
        String sql = "UPDATE user_game SET guesses  = ?, " +
                "guess1 = ?, guess2 = ?, guess3 = ?, guess4 = ?, guess5 = ?, guess6 = ?, success = ? " +
                "WHERE user_id = ? AND game_id = ?";

        List<String> guessList = modifiedUserGame.getGuesses();
        int guesses = guessList.size();
        int rowsUpdated = jdbcTemplate.update(sql, guesses,
                guesses > 0 ? guessList.get(0) : null,
                guesses > 1 ? guessList.get(1) : null,
                guesses > 2 ? guessList.get(2) : null,
                guesses > 3 ? guessList.get(3) : null,
                guesses > 4 ? guessList.get(4) : null,
                guesses > 5 ? guessList.get(5) : null,
                success, userId, gameId);
        return rowsUpdated == 1 ? getUserGameByUserIdAndGameId(userId, gameId) : null;
    }
}
