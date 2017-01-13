/**
 * Created by Rogier on 12-01-17.
 */
public interface Player {

    String getName();

    boolean wantsToStart();

    void sendOpponentMoved(String move);

    void sendGameStarted();

    String requestMove();

    void announceWinner(String winner);

    String moveDenied();
}
