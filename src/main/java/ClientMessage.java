import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Rogier on 17-12-16 in Enschede.
 */
public class ClientMessage  {
    private ClientAction action;
    private String move;
    private int lobbyNumber;
    private String name;

    public ClientMessage(String object) throws WrongMessageException {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(object);
            action = ClientAction.fromString(((JSONObject)obj).get("action").toString());
            switch (action){
                case CONNECT:
                    Object nameString = ((JSONObject)obj).get("name");
                    if (nameString != null){
                        name = (String) nameString;
                        break;
                    } else{
                        throw new WrongMessageException();
                    }
                case JOIN:
                    Object lob = ((JSONObject)obj).get("room number");
                    if (lob != null){
                        Long l = (Long)lob;
                        lobbyNumber = l.intValue();
                        break;
                    } else {
                        throw new WrongMessageException();
                    }

                case START:
                    break;
                case MOVE:
                    Object moveString = ((JSONObject)obj).get("move");
                    if (moveString != null){
                        move = (String)moveString;
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
}
