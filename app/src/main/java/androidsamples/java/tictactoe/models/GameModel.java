package androidsamples.java.tictactoe.models;

import java.util.Arrays;
import java.util.List;

public class GameModel {

    private List<String> gameArray = null;
    private String host;
    private String username;
    private String gameId;
    private int status;
    // STATUS CODES :
    // 0 = draw
    // 1 = host won
    // -1 = host lost (non-host won)
    // 2 = non-host forfeited
    // -2 = host forfeited
    // 3 = ongoing game
    private int turn;
    // 1 = host turn
    // -1 = non-host turn

    public GameModel(String host, String id, String username) {
        this.host = host;
        gameArray = Arrays.asList("", "", "", "", "", "", "", "", "");
        this.gameId = id;
        this.username = username;
        status = 3;
        turn = 1;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GameModel(){}

    public List<String> getGameArray() {
        return gameArray;
    }

    public void setGameArray(List<String> gameArray) {
        this.gameArray = (gameArray);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}