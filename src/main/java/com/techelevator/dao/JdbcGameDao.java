package com.techelevator.dao;

import com.techelevator.model.Game;
import com.techelevator.utils.Word;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class JdbcGameDao implements GameDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcGameDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final RowMapper<Game> MAPPER = new RowMapper<Game>() {
        @Override
        public Game mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("game_id");
            String word = rs.getString("word");
            LocalDate date = rs.getDate("game_date").toLocalDate();
            Game.Type type = Game.Type.values()[rs.getInt("type")];
            return new Game(id, word, date, type);
        }
    };

    @Override
    public List<Game> getGames() {
        return jdbcTemplate.query("SELECT * FROM game", MAPPER);
    }

    @Override
    public Game getGameById(int gameId) {
        List<Game> games = jdbcTemplate.query("SELECT * FROM game WHERE game_id = ?", MAPPER, gameId);
        return games.isEmpty() ? null : games.get(0);
    }

    @Override
    public List<Game> getGamesByDateAndType(LocalDate date, Game.Type type) {
        return jdbcTemplate.query("SELECT * FROM game WHERE game_date = ? AND type =?", MAPPER, date, type.ordinal());
    }

    @Override
    public Game createGame(Game newGame) {
        String sql = "INSERT INTO game(word, game_date, type) VALUES(?, ?, ?) RETURNING game_id";
        Integer gameId = jdbcTemplate.queryForObject(sql, int.class, newGame.getWord(), newGame.getDate(),
                newGame.getType().ordinal());
        return gameId == null ? null : getGameById(gameId);
    }

    @Override
    public Game updateGame(int gameId, Game modifiedGame) {
        String sql = "UPDATE game SET word = ?, game_date = ?, type = ? WHERE game_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, modifiedGame.getWord(), modifiedGame.getDate(),
                modifiedGame.getType().ordinal(), gameId);
        return rowsUpdated == 1 ? getGameById(gameId) : null;
    }
}
