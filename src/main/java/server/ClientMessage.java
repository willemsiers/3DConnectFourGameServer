package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.regex.Pattern;

/**
 * Created by Rogier on 17-12-16 in Enschede.
 */
public class ClientMessage {
    private ClientAction action;
    private String move;
    private int lobbyNumber;
    private String name;

    public ClientMessage(String object) throws WrongMessageException {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(object);
            action = ClientAction.fromString(((JSONObject) obj).get("action").toString());
            switch (action) {
                case CONNECT:
                    Object nameString = ((JSONObject) obj).get("name");
                    if (nameString != null) {
                        String tempName = nameString.toString();
                        if (!Pattern.matches("([a-zA-Z]|[0-9])*", tempName)
                                || tempName.length() > 16 || tempName.equals("No player1 yet")) {
                            throw new WrongMessageException();
                        }
                        name = (String) nameString;
                        break;
                    }
                    break;
                case JOIN:
                    Object lob = ((JSONObject) obj).get("room number");
                    if (lob != null) {
                        Long l = (Long) lob;
                        lobbyNumber = l.intValue();
                        break;
                    } else {
                        throw new WrongMessageException();
                    }

                case START:
                    break;
                case MOVE:
                    Object moveString = ((JSONObject) obj).get("move");
                    if (moveString != null) {
                        move = (String) moveString;
                        break;
                    } else {
                        throw new WrongMessageException();
                    }
                case RESTART:
                    break;
                case EXIT_GAME:
                    break;
                case DISCONNECT:
                    break;
                default:
                    throw new WrongMessageException();

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public ClientAction getAction() {
        return action;
    }

    public String getMove() {
        return move;
    }

    public int getLobbyNumber() {
        return lobbyNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ClientMessage{" +
                "action=" + action +
                ", move='" + move + '\'' +
                ", lobbyNumber=" + lobbyNumber +
                ", name='" + name + '\'' +
                '}';
    }
}
