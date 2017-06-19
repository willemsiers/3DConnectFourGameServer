import game.Board;
import game.GridMark;
import game.Move;
import org.junit.Assert;
import org.junit.Test;

public class MyTest {

    @Test
    public void testPrintHelloWorld() {

        Board board = new Board();
        Move move = new Move(4, 0, GridMark.RED);
        board.makeMove(new Move(0, 0, GridMark.RED));
        board.makeMove(new Move(0, 0, GridMark.RED));
        board.makeMove(new Move(0, 0, GridMark.RED));
        board.makeMove(new Move(1, 2, GridMark.YELLOW));
        Move last = new Move(0, 0, GridMark.RED);
        board.makeMove(last);
        Assert.assertTrue(board.isWinner(last));

    }

}
