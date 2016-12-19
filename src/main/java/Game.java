import java.util.List;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Game {
    private Player player1;

    private Player player2;

    private List<Move> moveList;

    private Board board;

    public int numberOfPlayers(){
        return (player1 != null ? 1 : 0) + (player2 != null ? 1 : 0);
    }



    public String getPlayer1Name(){
        return player1 != null ? player1.getName() : "No opponent yet";
    }

    public String getPlayer2Name(){
        return player2.getName();
    }


    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public boolean contains(Player player){
        return player1 == player || player2 == player;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
}
