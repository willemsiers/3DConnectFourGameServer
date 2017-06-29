import com.sun.org.omg.CORBA.ExceptionDescription;
import game.Board;
import game.GridMark;
import game.Move;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MyTest {
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testGetWinner() {
		Board board = new Board();
		Assert.assertTrue(board.getWinner() == GridMark.EMPTY);
		Move move = new Move(4, 0, GridMark.RED);
		board.makeMove(new Move(0, 0, GridMark.RED));
		board.makeMove(new Move(0, 0, GridMark.RED));
		board.makeMove(new Move(0, 0, GridMark.RED));
		board.makeMove(new Move(1, 2, GridMark.YELLOW));
		Assert.assertTrue(board.getWinner() == GridMark.EMPTY);
		board.makeMove(new Move(0, 0, GridMark.RED));
		Assert.assertTrue(board.getWinner() == GridMark.RED);
	}

	@Test
	public void testIsFinished() {
		Board board = new Board();
		Assert.assertFalse(board.isFinished());
		Move move = new Move(4, 0, GridMark.RED);
		board.makeMove(new Move(0, 0, GridMark.RED));
		board.makeMove(new Move(0, 0, GridMark.RED));
		board.makeMove(new Move(0, 0, GridMark.RED));
		board.makeMove(new Move(1, 2, GridMark.YELLOW));
		Assert.assertFalse(board.isFinished());
		board.makeMove(new Move(0, 0, GridMark.RED));
		Assert.assertTrue(board.isFinished());
	}

	@Test
	public void testMakeInvalidMove() {
		Board board = new Board();
		int moveX = Board.GRID_SIZE_X;
		exception.expectMessage(moveX + " exceeds the board size of "+Board.CONNECT);
		board.makeMove(new Move(moveX, 0, GridMark.RED));
	}

	@Test
	public void testDefaultWorld() {

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
