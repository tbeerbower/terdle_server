package com.techelevator.controller;

import com.techelevator.dao.UserDao;
import com.techelevator.dao.UserGameDao;
import com.techelevator.exception.DaoException;
import com.techelevator.model.*;
import com.techelevator.service.GameService;
import com.techelevator.validator.UserGameValidator;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The UserController is a class for handling HTTP Requests related to getting User information.
 *
 * It depends on an instance of a UserDAO for retrieving and storing data. This is provided
 * through dependency injection.
 *
 * Note: This class does not handle authentication (registration/login) of Users. That is
 * handled separately in the AuthenticationController.
 */
@RestController
@Validated
@CrossOrigin
@PreAuthorize("isAuthenticated()")
@RequestMapping( path = "/users")
public class UserController {

    private final UserDao userDao;
    private final UserGameDao userGameDao;
    private final GameService gameService;
    private final UserGameValidator userGameValidator;


    public UserController(UserDao userDao, UserGameDao userGameDao, GameService gameService, UserGameValidator userGameValidator) {
        this.userDao = userDao;
        this.userGameDao = userGameDao;
        this.gameService = gameService;
        this.userGameValidator = userGameValidator;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAllUsers() {
        try {
            return userDao.getUsers();
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public User getById(@PathVariable int userId, Principal principal) {
        try {
            return userDao.getUserById(userId);
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(path = "", method = RequestMethod.PUT)
    public User updateProfile(@RequestBody User modifiedUser, Principal principal) {
        User user = null;

        try {
            User loggedInUser = userDao.getUserByUsername(principal.getName());
            if ((loggedInUser != null) && (loggedInUser.getId() != modifiedUser.getId())) {
                throw new AccessDeniedException("Access denied");
            }
            user = userDao.updateUser(modifiedUser);
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return user;
    }

    @RequestMapping(path = "/{userId}/games", method = RequestMethod.GET)
    public List<UserGame> getGamesByUserId(@PathVariable int userId,
                                           @RequestParam(required = false) String date,
                                           @RequestParam(required = false) String type) {

        LocalDate filterDate = date == null ? null : LocalDate.parse(date);
        Game.Type filterType = type == null ? null : Game.Type.valueOf(type);

        return userGameDao.getUserGamesByUserId(userId, filterDate , filterType);
    }

    @RequestMapping(path = "/{userId}/games", method = RequestMethod.POST)
    public UserGame createUserGame(@PathVariable int userId, @RequestBody UserGame newUserGame) {
        return gameService.createUserGame(userId, newUserGame);
    }

    @RequestMapping(path = "/{userId}/games/{gameId}", method = RequestMethod.GET)
    public UserGame getUserGame(@PathVariable int userId, @PathVariable int gameId) {
        return gameService.getUserGame(userId, gameId);
    }

    @RequestMapping(path = "/{userId}/games/{gameId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGame(@PathVariable int userId, @PathVariable int gameId,
                           @Valid @RequestBody UserGame modifiedUserGame,
                           BindingResult result ) throws NoSuchMethodException, MethodArgumentNotValidException {

        UserGame userGame = new UserGame(userId, gameId, modifiedUserGame.getGuesses());
        userGameValidator.validate(userGame, result);
        if (result.hasErrors()) {
                throw new MethodArgumentNotValidException(
                        new MethodParameter(this.getClass().getDeclaredMethod("updateGame", int.class, int.class, UserGame.class, BindingResult.class), 2),
                        result);
        }
        gameService.updateUserGame(userId, gameId, userGame);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errors.put(((FieldError) error).getField(), error.getDefaultMessage());
        });
        return errors;
    }
}
