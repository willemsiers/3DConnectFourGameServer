/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Move {
    private int row;
    private int column;

    public Move(String moveString) {
    }

    @Override
    public String toString() {
        switch (row){
            case 0:
                return "a" + column;
            case 1:
                return "b" + column;
            case 2:
                return "c" + column;
            case 3:
                return "d" + column;
            default:
                return "error";
        }
    }
}
