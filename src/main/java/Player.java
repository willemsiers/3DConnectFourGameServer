/**
 * Created by Rogier on 12-01-17.
 */
public interface Player {

    String getName();

    boolean wantsToStart();

    void sendOpponentMoved(String move);

    void sendGameStarted(String opponent);

    String requestMove();

    void announceWinner(String winner);

}
