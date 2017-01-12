import java.util.List;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Game implements Runnable {
    private Player player1;

    private Player player2;

    private List<Move> moveList;

    private Board board;

    private boolean started;

    private int firstMove;


    public int numberOfPlayers(){
        return (player1 != null ? 1 : 0) + (player2 != null ? 1 : 0);
    }

    public boolean start(){
        return (player1 != null && player1.getState() == ServerEvents.STARTED) &&
                (player2 != null && player2.getState() == ServerEvents.STARTED);
    }


    public void init(){
        board = new Board();
        firstMove = (int)Math.round(Math.random());

    }


    public void run(){
        while(!start()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.init();
        started = true;
        Move lastMove;
        int turn;
        if (firstMove == 0){
            turn = 0;
            Move move = new Move(player1.requestMove(), GridMark.RED);
            this.makeMove(move);
            lastMove = move;
            player2.sendOpponentMoved(lastMove.toString());
        } else {
            turn = 1;
            Move move = new Move(player2.requestMove(),GridMark.YELLOW);
            this.makeMove(move);
            lastMove = move;
            player1.sendOpponentMoved(lastMove.toString());
        }

        while (!board.hasWinner(lastMove) || !board.draw()){
            turn = (turn + 1) % 2;
            if (turn == 0){
                Move move = new Move(player1.requestMove(), GridMark.RED);
                this.makeMove(move);
                lastMove = move;
                player2.sendOpponentMoved(lastMove.toString());
            } else {
                Move move = new Move(player2.requestMove(),GridMark.YELLOW);
                this.makeMove(move);
                lastMove = move;
                player1.sendOpponentMoved(lastMove.toString());
            }
        }
        if (board.draw()) {
            player1.announceWinner("Draw");
            player2.announceWinner("Draw");
        } else {
            if (turn == 0){
                player1.announceWinner("you");
                player2.announceWinner("opponent");
            } else {
                player1.announceWinner("opponent");
                player2.announceWinner("you");
            }
        }



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

    public Player otherPlayer(Player player){
        return player1 == player ? player2 : player1;
    }

    public boolean isStarted() {
        return started;
    }

    public void makeMove(Move move){
        boolean valid = board.isValidMove(move);
        if (valid){
            board.makeMove(move);

            moveList.add(move);
        } else {

        }

//        TODO: Bad move
    }


}

