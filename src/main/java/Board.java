import java.util.Arrays;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Board {
    public static final int GRID_SIZE = 4;

//    grid [x][y][z]
    private GridMark[][][] grid;


    public Board() {
        grid = new GridMark[GRID_SIZE][GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                for (int k = 0; k < GRID_SIZE; k++) {
                    grid[i][j][k]= GridMark.EMPTY;
                }
            }
        }
    }


    public GridMark[][][] getGrid() {
        return grid;
    }





    public static void main(String[] args) {
        Board board = new Board();
        board.makeMove(new Move(0,0),GridMark.RED);
        board.makeMove(new Move(0,0),GridMark.RED);
        board.makeMove(new Move(0,0),GridMark.RED);
        board.makeMove(new Move(0,1),GridMark.RED);
        board.makeMove(new Move(0,1),GridMark.RED);
        System.out.println(board);
    }

    public boolean isValidMove(Move move) {


        return false;
    }

    public void makeMove(Move move, GridMark mark) {

        if (grid[move.getX()][move.getY()][0] == GridMark.EMPTY){
            grid[move.getX()][move.getY()][0] = mark;
        } else if (grid[move.getX()][move.getY()][1] == GridMark.EMPTY){
            grid[move.getX()][move.getY()][1] = mark;
        } else if (grid[move.getX()][move.getY()][2] == GridMark.EMPTY){
            grid[move.getX()][move.getY()][2] = mark;
        } else if (grid[move.getX()][move.getY()][3] == GridMark.EMPTY){
            grid[move.getX()][move.getY()][3] = mark;
        }



    }

    public boolean hasWinner() {
        return false;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = GRID_SIZE -1; i >= 0; i--) {
            String result1 = "";
            String result2 = "";
            String result3 = "";
            String result4 = "";
            for (int j = 0; j < 4; j++) {
                result1 = result1 + " |" +  this.grid[j][i][0] +"|";
                result2 = result2 + " |" +  this.grid[j][i][1] +"|";
                result3 = result3 + " |" +  this.grid[j][i][2] +"|";
                result4 = result4 + " |" +  this.grid[j][i][3] +"|";
            }
            result = result + result4 +"\n";
            result = result + result3 +"\n";
            result = result + result2 +"\n";
            result = result + result1 +"\n";
            result = result + " --- --- --- ---" + "\n";
        }
        return result;
    }
}
