package game;

import org.json.simple.JSONArray;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Board {
    /**
     * 1D
     */

    private static final int[] UP = new int[]{0, 0, 1};
    private static final int[] DOWN = new int[]{0, 0, -1};
    private static final int[] LEFT = new int[]{-1, 0, 0};
    private static final int[] RIGHT = new int[]{1, 0, 0};
    private static final int[] FORWARD = new int[]{0, -1, 0};
    private static final int[] BACKWARD = new int[]{0, 1, 0};

    private static final int[][] axis1 = new int[][]{UP, DOWN};
    private static final int[][] axis2 = new int[][]{LEFT, RIGHT};
    private static final int[][] axis3 = new int[][]{FORWARD, BACKWARD};

    /**
     * 2D
     */

    private static final int[] D1 = new int[]{1, 1, 0};
    private static final int[] D2 = new int[]{-1, -1, 0};
    private static final int[] D3 = new int[]{1, -1, 0};
    private static final int[] D4 = new int[]{-1, 1, 0};
    private static final int[] D5 = new int[]{1, 0, 1};
    private static final int[] D6 = new int[]{-1, 0, -1};
    private static final int[] D7 = new int[]{1, 0, -1};
    private static final int[] D8 = new int[]{-1, 0, 1};
    private static final int[] D9 = new int[]{0, 1, 1};
    private static final int[] D10 = new int[]{0, -1, -1};
    private static final int[] D11 = new int[]{0, 1, -1};
    private static final int[] D12 = new int[]{0, -1, 1};

    private static final int[][] axis4 = new int[][]{D1, D2};
    private static final int[][] axis5 = new int[][]{D3, D4};
    private static final int[][] axis6 = new int[][]{D5, D6};
    private static final int[][] axis7 = new int[][]{D7, D8};
    private static final int[][] axis8 = new int[][]{D9, D10};
    private static final int[][] axis9 = new int[][]{D11, D12};

    /**
     * 3D
     */

    private static final int[] DD1 = new int[]{1,1,1};
    private static final int[] DD2 = new int[]{-1,-1,-1};
    private static final int[] DD3 = new int[]{1,1,-1};
    private static final int[] DD4 = new int[]{-1,-1,1};
    private static final int[] DD5 = new int[]{1,-1,1};
    private static final int[] DD6 = new int[]{-1,1,-1};
    private static final int[] DD7 = new int[]{-1,1,1};
    private static final int[] DD8 = new int[]{1,-1,-1};

    private static final int[][] axis10 = new int[][]{DD1, DD2};
    private static final int[][] axis11 = new int[][]{DD3, DD4};
    private static final int[][] axis12 = new int[][]{DD5, DD6};
    private static final int[][] axis13 = new int[][]{DD7, DD8};

    /**
     * All axes that need to be checked
     */
    private static final int[][][] axes = new int[][][]{axis1, axis2, axis3, axis4, axis5, axis6, axis7, axis8, axis9, axis10, axis11, axis12, axis13};


    public static final int CONNECT = 4;

    public static final int GRID_SIZE_X = 4;
    public static final int GRID_SIZE_Y = 4;
    public static final int GRID_SIZE_Z = 4;

    //    grid [x][y][z]
    private GridMark[][][] grid;

    private Set<String> winningMove;


    public Board() {
        grid = new GridMark[GRID_SIZE_X][GRID_SIZE_Y][GRID_SIZE_Z];
        for (int i = 0; i < GRID_SIZE_X; i++) {
            for (int j = 0; j < GRID_SIZE_Y; j++) {
                for (int k = 0; k < GRID_SIZE_Z; k++) {
                    grid[i][j][k] = GridMark.EMPTY;
                }
            }
        }
        winningMove = new HashSet<>();
    }


    public static void main(String[] args) {
        Board board = new Board();
        Move move = new Move(4, 0,GridMark.RED);
        System.out.println(board.isValidMove(move));
        board.makeMove(new Move(0, 0,GridMark.RED));
        board.makeMove(new Move(0, 0,GridMark.RED));
        board.makeMove(new Move(0, 0,GridMark.RED));
        board.makeMove(new Move(1, 2, GridMark.YELLOW));
        Move last = new Move(0, 0,GridMark.RED);
        board.makeMove(last);
        System.out.println("Winner? " + board.isWinner(last));
        System.out.println(Arrays.deepToString(board.grid));
        System.out.println(board);

    }

    public GridMark[][][] getGrid() {
        return grid;
    }

    public boolean isValidMove(Move move) {
        int x = move.getX();
        int y = move.getY();
        return x >= 0 && x < GRID_SIZE_X && y >= 0 && y < GRID_SIZE_Y && grid[x][y][3] == GridMark.EMPTY;
    }

    public boolean isValidSlot(int x, int y, int z){
        return x >= 0 && x < GRID_SIZE_X && y >= 0 && y < GRID_SIZE_Y && z >= 0 && z < GRID_SIZE_Z;
    }

    public void makeMove(Move move) {

        if (grid[move.getX()][move.getY()][0] == GridMark.EMPTY) {
            move.setZ(0);
        } else if (grid[move.getX()][move.getY()][1] == GridMark.EMPTY) {
            move.setZ(1);
        } else if (grid[move.getX()][move.getY()][2] == GridMark.EMPTY) {
            move.setZ(2);
        } else if (grid[move.getX()][move.getY()][3] == GridMark.EMPTY) {
            move.setZ(3);
        }
        grid[move.getX()][move.getY()][move.getZ()] = move.getMark();

    }

    private boolean isWinner(Move move) {
        final GridMark color = move.getMark();
        final int[] origin = new int[]{move.getX(), move.getY(), move.getZ()};
        for (int[][] axis : axes) {
            int[] direction1 = axis[0];
            int[] direction2 = axis[1];
            int chainLength = 1;
            winningMove.add("" + move.getX() + move.getY() + move.getZ());
            chainLength += checkDirection(origin, direction1, color);
            chainLength += checkDirection(origin, direction2, color);
            if (chainLength >= CONNECT) {
                return true;
            } else {
                winningMove = new HashSet<>();
            }
        }
        return false;
    }


    private int[] getLocation(final int[] origin, final int[] direction, final int multiplier) {
        return new int[]{origin[0] + multiplier * direction[0],
                origin[1] + multiplier * direction[1],
                origin[2] + multiplier * direction[2]};
    }

    private int checkDirection(final int[] origin, final int[] direction, final GridMark color) {
        int chainLength = 0;
        for (int i = 1; i < CONNECT; i++) {
            int[] coordinates = getLocation(origin, direction, i);
            if (this.isValidSlot(coordinates[0], coordinates[1], coordinates[2])) {
                GridMark occupiedBy = grid[coordinates[0]][coordinates[1]][coordinates[2]];
                if (occupiedBy == color) {
                    winningMove.add("" + coordinates[0] + coordinates[1] + coordinates[2]);
                    chainLength++;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return chainLength;
    }


    public boolean draw() {
        boolean result = true;
        for (int x = 0; x < GRID_SIZE_X; x++) {
            for (int y = 0; y < GRID_SIZE_Y; y++) {
                for (int z = 0; z < GRID_SIZE_Z; z++) {
                    result = result && grid[x][y][z] != GridMark.EMPTY;
                }
            }
        }
        return result;
    }

    public boolean gameOver(Move move) {
        return draw() || isWinner(move);

    }


    @Override
    public String toString() {
        String result = "";
        for (int i = GRID_SIZE_Y - 1; i >= 0; i--) {
            String result1 = "";
            String result2 = "";
            String result3 = "";
            String result4 = "";
            for (int j = 0; j < GRID_SIZE_X; j++) {
                result1 = result1 + " |" + this.grid[j][i][0] + "|";
                result2 = result2 + " |" + this.grid[j][i][1] + "|";
                result3 = result3 + " |" + this.grid[j][i][2] + "|";
                result4 = result4 + " |" + this.grid[j][i][3] + "|";
            }
            result = result + result4 + "\n";
            result = result + result3 + "\n";
            result = result + result2 + "\n";
            result = result + result1 + "\n";
            result = result + " --- --- --- ---" + "\n";
        }
        return result;
    }

    public JSONArray toJSONString() {
        JSONArray arr = new JSONArray();

        for (int x = 0; x < GRID_SIZE_X; x++) {
            JSONArray array = new JSONArray();
            for (int y = 0; y < GRID_SIZE_Y; y++) {
                JSONArray jsonArray = new JSONArray();
                for (int z = 0; z < GRID_SIZE_Z; z++) {
                    jsonArray.add(grid[x][y][z].toString());
                }
                array.add(jsonArray);
            }
            arr.add(array);
        }
        return arr;
    }

    public String[] getWinningMove() {
        String[] result = winningMove.toArray(new String[4]);
        Arrays.sort(result);
        return result;
    }
}
