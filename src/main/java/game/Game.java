package game;

import server.Player;
import server.ServerPlayer;

import java.util.List;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Game implements Runnable {
    private Player player1;

    private Player player2;

    private List<String> winningMove;

    private Board board;

    private boolean started;

    private int firstMove;

    private String winner;

    public Game() {
        init();
    }

    public int numberOfPlayers(){
        return (player1 != null ? 1 : 0) + (player2 != null ? 1 : 0);
    }

    public boolean start(){
        return (player1 != null && player1.wantsToStart()) &&
                (player2 != null && player2.wantsToStart());
    }


    public void init(){
        board = new Board();
        firstMove = (int)Math.round(Math.random());
        player1 = null;
        player2 = null;

    }


    public void run(){
        while (true) {
            this.init();
            this.waitForAllPlayers();
            player1.sendGameStarted();
            player2.sendGameStarted();
            System.out.println("A game has been started");

            started = true;
            Move lastMove;
            int turn = firstMove;
            if (firstMove == 0) {
                lastMove = player1Move();
            } else {
                lastMove = player2Move();
            }
            while (!board.gameOver(lastMove)) {
                turn = (turn + 1) % 2;
                if (turn == 0) {
                    lastMove = this.player1Move();
                } else {
                    lastMove = this.player2Move();
                }
            }
            if (board.draw()) {
                winner = "draw";
                player1.announceWinner("draw");
                player2.announceWinner("draw");
            } else {
                System.out.println(board.toString());
                winner = turn == 0 ? player1.getName() : player2.getName();
                player1.announceWinner(turn == 0 ? "you" : "opponent");
                player2.announceWinner(turn == 0 ? "opponent" : "you");
            }


        }

    }

    public void waitForAllPlayers() {
        while (!start()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("game.Game interrupted");
            }
        }
    }

    public Move player1Move() {
        Move move = new Move(player1.requestMove(), GridMark.RED);
        while (!board.isValidMove(move)) {
            move = new Move(player1.moveDenied(), GridMark.RED);
        }
        this.makeMove(move);
        player2.sendOpponentMoved(move.toString());
        return move;
    }

    public Move player2Move() {
        Move move = new Move(player2.requestMove(), GridMark.YELLOW);
        while (!board.isValidMove(move)) {
            move = new Move(player2.moveDenied(), GridMark.YELLOW);
        }
        this.makeMove(move);
        player1.sendOpponentMoved(move.toString());
        return move;
    }


    public String getPlayer1Name(){
        return player1 != null ? player1.getName() : "No player1 yet";
    }

    public String getPlayer2Name(){
        return player2 != null ? player2.getName() : "No player2 yet";
    }

    public Board getBoard() {
        return board;
    }

    public void setPlayer1(ServerPlayer player1) {
        this.player1 = player1;
    }

    public void setPlayer2(ServerPlayer player2) {
        this.player2 = player2;
    }

    public boolean contains(ServerPlayer player) {
        return player1 == player || player2 == player;
    }

    public Player otherPlayer(ServerPlayer player) {
        return player1 == player ? player2 : player1;
    }

    public boolean isStarted() {
        return started;
    }

    public String getWinner() {
        return winner != null ? winner : "No winner yet";
    }

    public void makeMove(Move move){
        board.makeMove(move);
    }


}

