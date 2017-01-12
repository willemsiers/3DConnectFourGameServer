import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by Rogier on 19-12-16.
 */
public class ServerMessage {

    public static String sendLobbyStatus(List<String> freeLobbies){
        JSONObject obj = new JSONObject();
        obj.put("event", "lobby");
        JSONArray array = new JSONArray();
        for (int i = 0; i < freeLobbies.size(); i++){
            JSONObject obj1  = new JSONObject();
            obj1.put("room number", i);
            obj1.put("opponent" , freeLobbies.get(i));
            array.add(obj1);
        }
        obj.put("free lobbies" , array);
        obj.put("message", "lobby message");
        return obj.toJSONString();
    }

    public static String sendGameStatus(String opponentName){
        JSONObject obj = new JSONObject();
        obj.put("event", "game");
        obj.put("opponent", opponentName);
        obj.put("message", "game message");
        return obj.toJSONString();
    }


    public static String sendError(String reason){
        JSONObject obj = new JSONObject();
        obj.put("event", "game");
        obj.put("reason", reason);
        obj.put("message", "error message");
        return obj.toJSONString();
    }

    public static String sendMakeMove(){
        JSONObject obj = new JSONObject();
        obj.put("event", "make move");
        obj.put("message", "make move message");
        return obj.toJSONString();
    }

    public static String sendOpponentMoved(String move){
        JSONObject obj = new JSONObject();
        obj.put("event", "opponent moved");
        obj.put("move", move);
        obj.put("message", "opponent moved message");
        return obj.toJSONString();
    }

    public static String sendGameOver(String winner){
        JSONObject obj = new JSONObject();
        obj.put("event", "game over");
        obj.put("winner", winner);
        obj.put("message", "lost message");
        return obj.toJSONString();
    }


    public static String sendGameStarted(String opponent) {
        JSONObject obj = new JSONObject();
        obj.put("event", "started");
        obj.put("opponent", opponent);
        obj.put("message", "game started message");
        return obj.toJSONString();
    }
}
