package com.techelevator.dao;

import com.techelevator.model.Game;
import com.techelevator.model.UserGame;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO class for interacting with the user-game information in the data store.
 */
public interface UserGameDao {

    /**
     * Get all user-games from the datastore ordered alphabetically by username.
     *
     * @return List of all UserGame objects, or an empty list if no UserGames are found.
     */
    List<UserGame> getUserGames();

    /**
     * Get a user-game from the datastore with the specified id.
     * If the id is not found, returns null.
     *
     * @param userId The user id of the user-game to return.
     * @param date Filter by date (optional).
     * @param type Filter by type (optional).
     * @return The matching UserGame object.
     */
    List<UserGame> getUserGamesByUserId(int userId, LocalDate date, Game.Type type);

    /**
     * Get a user-game from the datastore with the specified ids.
     * If the user-game is not found, returns null.
     *
     * @param userId The user id of the user-game to return.
     * @param gameId The game id of the user-game to return.
     * @return The matching UserGame object.
     */
    UserGame getUserGameByUserIdAndGameId(int userId, int gameId);

    /**
     * Adds a new user-game to the datastore.
     *
     * @param userId
     * @param gameId
     * @param newUserGame the new user-game
     * @return The new UserGame object with its new id filled in.
     */
    UserGame createUserGame(int userId, int gameId, UserGame newUserGame);

    /**
     * Updates the user-game information.
     *
     * @param modifiedUserGame the user-game data to update
     * @param success
     * @return the updated UserGame object
     */
    UserGame updateUserGame(int userId, int gameId, UserGame modifiedUserGame, boolean success);
}
