package server;

/**
 * Created by Rogier on 12-01-17.
 */
public interface Player {

    String getName();

    boolean wantsToStart();

    boolean wantsToRestart();

    void sendOpponentMoved(String move);

    void sendGameStarted(String opponentName);

    String requestMove();

    void announceWinner(String winner, String[] winningMove);

    String moveDenied();
}
