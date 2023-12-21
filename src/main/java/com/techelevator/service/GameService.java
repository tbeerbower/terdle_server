package com.techelevator.service;

import com.techelevator.dao.GameDao;
import com.techelevator.dao.UserGameDao;
import com.techelevator.model.Game;
import com.techelevator.model.UserGame;
import com.techelevator.model.UserGameAnalysisDto;
import com.techelevator.model.ai.ChatCompletionDto;
import com.techelevator.model.ai.ChatRequestDto;
import com.techelevator.model.ai.Message;
import com.techelevator.utils.Words;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.techelevator.model.Game.Type.DAILY;
import static com.techelevator.model.Game.Type.RANDOM;

@Component
public class GameService {
    public static final int MAX_ATTEMPTS_FOR_UNUSED_WORD = 1000;
    private static final String OPENAI_ENDPOINT =
            Optional.ofNullable(System.getenv("OPENAI_ENDPOINT")).orElse("https://api.openai.com/v1/chat/completions");
    private static final String OPENAI_KEY = System.getenv("OPENAI_KEY");
    public static final String OPENAI_MODEL =
            Optional.ofNullable(System.getenv("OPENAI_MODEL")).orElse("gpt-3.5-turbo");


    /*
      private static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_KEY = "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";
    public static final String OPENAI_MODEL = "gpt-3.5-turbo";



     public String getUserGameAnalysis(List<UserGame> games) {

        String question = String.format("If I play the following Wordle games where 'word' is the secret word, 'guesses' " +
                "are the attempts at guessing the secret word and 'success' is if the game was won, could you analyze " +
                "the game play and rate me as a Wordle player?  %s", games);

        // Set the token in the auth header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + OPENAI_KEY);

        // Create the input for ChatGPT
        ChatRequestDto request = new ChatRequestDto(OPENAI_MODEL, 0.2);
        request.setModel(OPENAI_MODEL);
        request.setTemperature(1.2);
        request.addMessage(new Message("user", question));

        // Package the body and headers in an entity
        HttpEntity<ChatRequestDto> requestEntity = new HttpEntity<>(request, headers);

        // Post to ChatGPT through Spring REST
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ChatCompletionDto> responseEntity = restTemplate.exchange(OPENAI_ENDPOINT, HttpMethod.POST, requestEntity, ChatCompletionDto.class);

        // Process the response and extract the generated answer
        ChatCompletionDto chatCompletion = responseEntity.getBody();

        String analysis = chatCompletion.getChoices().get(0).getMessage().getContent();

        return analysis;
    }



     */

    private final GameDao gameDao;
    private final UserGameDao userGameDao;

    public GameService(GameDao gameDao, UserGameDao userGameDao) {
        this.gameDao = gameDao;
        this.userGameDao = userGameDao;
    }

    public synchronized Game createGame(Game game) {
        return gameDao.createGame(game);
    }

    public UserGame createUserGame(int userId, UserGame userGame) {
        int gameId = userGame.getGameId();

        if (gameId == 0) {
            Game game = userGame.getType() == DAILY ?
                    getDailyGame(userGame.getDate(), userGame.getWord()) : getRandomGame(userId, userGame.getWord());
            gameId = game.getGameId();
        }
        return userGameDao.createUserGame(userId, gameId, userGame);
    }

    public UserGame getUserGame(int userId, int gameId) {
        return userGameDao.getUserGameByUserIdAndGameId(userId, gameId);
    }

    public void updateUserGame(int userId, int gameId, UserGame updatedUserGame) {
        userGameDao.updateUserGame(userId, gameId,
                new UserGame(userGameDao.getUserGameByUserIdAndGameId(userId, gameId), updatedUserGame.getGuesses()));
    }

    public UserGameAnalysisDto getUserGameAnalysis(int userId, int gameId) {


        UserGame userGame = getUserGame(userId, gameId);

        String commaSeparatedString = String.join(",", userGame.getGuesses());
        String question =
                String.format("Could you analyze a game of Wordle where the word was %s and the guesses were %s?  Could you give examples of better guesses at each attempt?",
                        userGame.getWord(), commaSeparatedString);

        System.out.println(question);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + OPENAI_KEY);

        // Create the input for ChatGPT

        ChatRequestDto request = new ChatRequestDto(OPENAI_MODEL, 0.2);
        request.setModel(OPENAI_MODEL);
        request.setTemperature(1.2);
        request.addMessage(new Message("user", question));

        HttpEntity<ChatRequestDto> requestEntity = new HttpEntity<>(request, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ChatCompletionDto> responseEntity = restTemplate.exchange(OPENAI_ENDPOINT, HttpMethod.POST, requestEntity, ChatCompletionDto.class);

        // Process the response and extract the generated answer
        ChatCompletionDto chatCompletion = responseEntity.getBody();

        String analysis = chatCompletion.getChoices().get(0).getMessage().getContent();

        System.out.println(analysis);

        return new UserGameAnalysisDto(analysis);
    }

    private synchronized Game getDailyGame(LocalDate date, String word) {
        if (date == null) {
            date = LocalDate.now(ZoneId.of("GMT"));
        }
        List<Game> games = gameDao.getGamesByDateAndType(date, DAILY);
        if (games.isEmpty()) {
            if (word == null) {
                List<Game> userGames = gameDao.getGamesByType(DAILY);
                Set<String> usedWords = userGames.stream().map(Game::getWord).collect(Collectors.toSet());
                word = Words.getUnusedWord(usedWords, MAX_ATTEMPTS_FOR_UNUSED_WORD);
            }
            return gameDao.createGame(new Game(word, date, DAILY));
        }
        return games.get(0);
    }

    private synchronized Game getRandomGame(int userId, String word) {
        if (word == null) {
            List<UserGame> userGames = userGameDao.getUserGamesByUserId(userId, null, RANDOM);
            Set<String> usedWords = userGames.stream().map(Game::getWord).collect(Collectors.toSet());
            word = Words.getUnusedWord(usedWords, MAX_ATTEMPTS_FOR_UNUSED_WORD);
        }
        List<Game> games = gameDao.getGamesByWordAndType(word, RANDOM);
        return games.isEmpty() ? gameDao.createGame(new Game(word, LocalDate.now(ZoneId.of("GMT")), RANDOM)) : games.get(0);
    }
}
