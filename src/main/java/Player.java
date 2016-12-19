import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Player implements Runnable {
    private Socket socket;
    private String name;
    private Lobby lobby;
    private boolean connected;
    private BufferedReader in;
    private PrintWriter out;
    private Game currentGame;

    public Player(Socket socket,BufferedReader in, Lobby lobby) {
        this.socket = socket;
        this.lobby = lobby;
        this.in = in;
        this.connected = true;
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            connected = false;
            e.printStackTrace();
        }
    }

    public void run() {

        while(isConnected()){
            String userInput;
            try {
                while ((userInput = in.readLine()) != null && isConnected()) {
                    System.out.println(userInput);
                    ClientMessage message = new ClientMessage(userInput);
                    switch (message.getAction()){
                        case CONNECT:
                            System.out.println("Player connected: " + message.getName());
                            this.name = message.getName();
                            lobby.addPlayerToLobby(this);
                            this.sendLobbyStatus();
//                            TODO: same name error
                            break;
                        case JOIN:
                            int roomNumber = message.getLobbyNumber();
                            String opponent = lobby.getOpponentName(roomNumber);
                            currentGame = lobby.addPlayerToRoom(this,roomNumber);

                            if (currentGame != null){
                                System.out.println(this.name + " added to a game");
                                this.sendGameStatus(opponent);
                            } else {
                                this.sendError("room full");
//                                TODO: Send Error
                            }
                            break;
                        case START:
                            break;
                        case MOVE:
                            break;
                        case RESTART:
                            break;
                        case EXIT_GAME:
                            break;
                        case DISCONNECT:
                            connected = false;
                            lobby.disconnectPlayer(this);
                            break;
                    }
                }
            } catch (IOException e){
                connected = false;
                System.out.println("error");
            } catch (WrongMessageException e){
                System.out.println("Wrong message received");
//                                TODO: Send Error
            }
        }

        try {
            socket.close();
            System.out.println("Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private boolean isConnected(){
        return connected && socket.isConnected();
    }

    public String getName(){
        return name;
    }

    public void sendLobbyStatus(){
        String json = ServerMessage.sendLobbyStatus(lobby.getFreeRooms());
            this.out.println(json);
            this.out.flush();
    }

    public void sendGameStatus(String opponent){
        String json = ServerMessage.sendGameStatus(opponent);
        this.out.println(json);
        this.out.flush();
    }

    public void sendError(String message){
        String json = ServerMessage.sendError(message);
        this.out.println(json);
        this.out.flush();
    }



    public static void main(String[] args) {
        Map<String, String> obj = new HashMap<String, String>();
        obj.put("action", "connect");
        obj.put("name", "Rogier");
        JSONObject object = new JSONObject(obj);
        BufferedReader reader = new BufferedReader(new StringReader(object.toJSONString()));
        try {
            System.out.println(reader.readLine());
            System.out.println(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
