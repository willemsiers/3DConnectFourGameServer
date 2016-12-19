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

}
