/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Move {
    private int y;
    private int x;


    public Move(String moveString) {
    }

    public Move(int x, int y){
        this.x = x;
        this.y = y;
    }


    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        switch (y){
            case 0:
                return "a" + x;
            case 1:
                return "b" + x;
            case 2:
                return "c" + x;
            case 3:
                return "d" + x;
            default:
                return "error";
        }
    }
}
