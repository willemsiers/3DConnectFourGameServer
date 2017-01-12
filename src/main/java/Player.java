import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

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
    private ServerEvents state;
    private Game currentGame;
    private ClientMessage lastMessage;

    private Semaphore semaphore;

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
        this.semaphore = new Semaphore(1);
    }

    public void run() {

        while(isConnected()){
            String userInput;
            try {
                while ((userInput = in.readLine()) != null && isConnected()) {
                    System.out.println(userInput);
                    lastMessage = new ClientMessage(userInput);
                    switch (lastMessage.getAction()){
                        case CONNECT:
                            System.out.println("Player connected: " + lastMessage.getName());
                            this.name = lastMessage.getName();
                            lobby.addPlayerToLobby(this);
                            this.sendLobbyStatus();
//                            TODO: same name error
                            state = ServerEvents.LOBBY;
                            break;
                        case JOIN:
                            int roomNumber = lastMessage.getLobbyNumber();
                            String opponent = lobby.getOpponentName(roomNumber);
                            currentGame = lobby.addPlayerToRoom(this,roomNumber);

                            if (currentGame != null){
                                System.out.println(this.name + " added to a game");
                                this.sendGameStatus(opponent);
                                state = ServerEvents.GAME;
                            } else {
                                this.sendError("room full");
//                                TODO: Send Error
                            }
                            break;
                        case START:
                            state = ServerEvents.STARTED;
                            break;
                        case MOVE:
                            state = ServerEvents.MAKE_MOVE;

                            break;
                        case RESTART:
                            state = ServerEvents.STARTED;
                            break;
                        case EXIT_GAME:
                            state = ServerEvents.LOBBY;
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

    private void sendError(String message){
        String json = ServerMessage.sendError(message);
        this.out.println(json);
        this.out.flush();
    }

    public void sendMoveRequest(){
        String json = ServerMessage.sendMakeMove();
        this.out.println(json);
        this.out.flush();
    }

    public void sendOpponentMoved(String move){
        String json = ServerMessage.sendOpponentMoved(move);
        this.out.println(json);
        this.out.flush();
    }

    public String requestMove() {
        this.sendMoveRequest();


        return lastMessage.getMove();
    }

    public void announceWinner(String winner) {
        String json = ServerMessage.sendGameOver(winner);
        this.out.println(json);
        this.out.flush();
    }



    public ServerEvents getState() {
        return state;
    }
}
