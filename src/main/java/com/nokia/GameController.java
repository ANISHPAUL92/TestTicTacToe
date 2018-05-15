package com.nokia;

import com.nokia.exceptions.GameNotFoundException;
import com.nokia.exceptions.GenericGameException;
import com.nokia.exceptions.InvalidMoveException;
import com.nokia.models.Board;
import com.nokia.mongodocs.Game;
import com.nokia.mongorepos.GameRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;


@RestController
@RequestMapping(path="/game")
public class GameController {

    public static final String DRAW_GAME = "Draw ! .. try again next time !";
    public static final String COMPUTER_WON = "Computer won !";
    public static final String PLAYER_WON = "Player Won !";
    public static final String GAME_ONGOING = "Game ongoing, your move";
    public static final char NO_WINNER = '#';
    @Autowired
    GameRepository gameRepository;

    @RequestMapping(method = RequestMethod.POST)
    public String createNewGame (@RequestParam(value="input") JSONObject input) throws JSONException{
        String playerCharacter = (String)input.get("character");
        if (playerCharacter.equals("X")){
            throw new GenericGameException("X is reserved for computer, please choose another character !");
        }
        int gamesCount=getAllGamesFromStorage().size();
        Game newGame = new Game(gamesCount+1,"*********",playerCharacter.charAt(0),(String)input.get("name"));
        gameRepository.save(newGame);
        return "Created new game with ID:" + (++gamesCount);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{gameId}")
    public String getGame(@PathVariable Integer gameId){
        String stateOfGame = GAME_ONGOING;

        Game  currentGame = getGameFromStorage(gameId);
        if(currentGame == null){
            throw new GameNotFoundException("Entered game ID not found in storage !");
        }
        Board currentBoard = new Board(currentGame.getGameState().toCharArray());
        int winner= NO_WINNER;
        winner=currentBoard.hasWinner();

        if(winner == NO_WINNER) {
            if (!currentBoard.movesAvailable()) {
                stateOfGame = DRAW_GAME;
            }
        }else{
            System.out.println("winner is " + winner);
            stateOfGame=(winner=='0')? COMPUTER_WON : PLAYER_WON;
        }

        System.out.println("GAME STATE:" + currentBoard.getGameState());
        return currentBoard.getBoardInASCII(currentGame.getOpponentCharacter()) + "\n" + stateOfGame;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{gameId}/move")
    public String tryMove(@PathVariable Integer gameId,@RequestParam(value="input") JSONObject moveInput) throws JSONException {
        String stateOfGame= GAME_ONGOING;
        String keys="ABC";
        char winner= NO_WINNER;


        Game currentGame = getGameFromStorage(gameId);
        Board currentBoard = new Board(currentGame.getGameState().toCharArray());

        winner=currentBoard.hasWinner();

        if(winner == NO_WINNER) {

            int rowToMove = -1;
            int columnToMove = -1;

            rowToMove = keys.indexOf(String.valueOf(moveInput.get("row")).charAt(0));
            columnToMove = keys.indexOf(String.valueOf(moveInput.get("column")).charAt(0));

            if (rowToMove < 0 || columnToMove < 0) {
                throw new InvalidMoveException("Invalid coordinates to move !");
            }
            if (!currentBoard.movesAvailable()) {
                stateOfGame = DRAW_GAME;
            } else if (currentBoard.makeMove(rowToMove, columnToMove, false) == false) {
                stateOfGame = "You cannot move there as the location is already occupied !";
            } else {

                winner = currentBoard.hasWinner();

                if (winner == NO_WINNER) {

                    if (currentBoard.movesAvailable()) {
                        Random rand = new Random();
                        int randomMove = rand.nextInt((currentBoard.availablePlaces.size()));
                        int placeToMove = currentBoard.availablePlaces.get(randomMove);
                        rowToMove = placeToMove / 3;
                        columnToMove = placeToMove % 3;
                        currentBoard.makeMove(rowToMove, columnToMove, true);

                        winner = currentBoard.hasWinner();

                    } else {
                        stateOfGame = DRAW_GAME;
                    }
                }
            }
        }
        if(winner != NO_WINNER){
            System.out.println("winner is " + winner);
            stateOfGame=(winner == '0')? COMPUTER_WON : PLAYER_WON;
        }

        System.out.println("GAME STATE:" + currentBoard.getGameState());
        currentGame.setGameState(currentBoard.getGameState());
        gameRepository.save(currentGame);
        return currentBoard.getBoardInASCII(currentGame.getOpponentCharacter()) + "\n" + stateOfGame;
    }

    public Game getGameFromStorage(int gId) {
        if (gameRepository == null) {
            throw new GenericGameException("Unable to initialize mongo db repository !");
        } else if (gameRepository.findAll() == null) {
            System.out.println("Nothing in the storage");
            return null;
        }
        return gameRepository.findByGameID(gId);
    }

    public List<Game> getAllGamesFromStorage() {
        if (gameRepository == null) {
            throw new GenericGameException("Unable to initialize mongo db repository !");
        } else if (gameRepository.findAll() == null) {
            System.out.println("Nothing in the storage");
            return null;
        }
        return gameRepository.findAll();
    }

}
