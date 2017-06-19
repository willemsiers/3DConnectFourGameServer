package game;

/**
 * Created by Rogier on 20-12-16 in Enschede.
 */
public enum GridMark {
    RED, YELLOW, EMPTY;


    @Override
    public String toString() {
        switch (this) {
            case RED:
                return "R";
            case YELLOW:
                return "Y";
            case EMPTY:
                return "_";
            default:
                return "_";
        }

    }
}
