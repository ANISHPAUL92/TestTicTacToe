package com.nokia.mongodocs;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Game {
    @Id
    private Integer gameID;
    private String gameState;
    private char opponentCharacter;
    private String opponentName;

    public Game(Integer gameID, String gameState, char opponentCharacter, String opponentName) {
        this.gameID = gameID;
        this.gameState = gameState;
        this.opponentCharacter = opponentCharacter;
        this.opponentName = opponentName;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public char getOpponentCharacter() {
        return opponentCharacter;
    }

    public void setOpponentCharacter(char opponentCharacter) {
        this.opponentCharacter = opponentCharacter;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }
}

