package server;

/**
 * Created by Rogier on 17-12-16 in Enschede.
 */
public enum ClientAction {
    CONNECT, JOIN, START, MOVE, RESTART, EXIT_GAME, DISCONNECT;


    public static ClientAction fromString(String action) throws WrongMessageException {
        if (action.equals("connect")) {
            return CONNECT;
        } else if (action.equals("join")) {
            return JOIN;
        } else if (action.equals("start")) {
            return START;
        } else if (action.equals("move")) {
            return MOVE;
        } else if (action.equals("restart")) {
            return RESTART;
        } else if (action.equals("exit game")) {
            return EXIT_GAME;
        } else if (action.equals("disconnect")) {
            return DISCONNECT;
        } else {
            throw new WrongMessageException();
        }


    }


}
