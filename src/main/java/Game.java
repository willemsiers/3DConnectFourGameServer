import java.util.List;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Game {
    private Player player1;

    private Player player2;

    private List<Move> moveList;

    private Board board;

    private int firstMove;

    public int numberOfPlayers(){
        return (player1 != null ? 1 : 0) + (player2 != null ? 1 : 0);
    }


    public void init(){
        board = new Board();
        firstMove = (int)Math.round(Math.random());
    }


    public void run(){
        int turn;
        if (firstMove == 0){
            turn = 0;
            Move move = player1.requestMove();
            this.makeMove(move,turn);
        } else {
            turn = 1;
            Move move = player2.requestMove();
            this.makeMove(move,turn);
        }

        while (!board.hasWinner()){
            turn = (turn + 1) % 2;
            if (turn == 0){
                Move move = player1.requestMove();
                this.makeMove(move,turn);
            } else {
                Move move = player2.requestMove();
                this.makeMove(move,turn);
            }
        }

        player1.announceWinner();
        player2.announceWinner();


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

    public void makeMove(Move move,int turn){
        boolean valid = board.isValidMove(move);
        if (turn == 0){
            board.makeMove(move,GridMark.RED);
        } else {
            board.makeMove(move,GridMark.YELLOW);
        }

        moveList.add(move);
//        TODO: Bad move
    }
}

