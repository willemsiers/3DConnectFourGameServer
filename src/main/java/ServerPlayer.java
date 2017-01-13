import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class ServerPlayer implements Runnable, Player {
    private Socket socket;
    private String name;
    private Lobby lobby;
    private boolean connected;
    private BufferedReader in;
    private PrintWriter out;
    private ServerEvents state;
    private ClientMessage lastMessage;
    private Lock lock;
    private Condition moveMessageReceived;

    public ServerPlayer(Socket socket, BufferedReader in, Lobby lobby) {
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
        this.lock = new ReentrantLock();
        moveMessageReceived = lock.newCondition();
    }

    public void run() {
        while (isConnected()) {
            String userInput;
            try {
                while ((userInput = in.readLine()) != null && isConnected()) {
//                    System.out.println(userInput);
                    lastMessage = new ClientMessage(userInput);
                    switch (lastMessage.getAction()) {
                        case CONNECT:
                            this.connect();
                            break;
                        case JOIN:
                            this.join();
                            break;
                        case START:
                            state = ServerEvents.STARTED;
                            break;
                        case MOVE:
                            state = ServerEvents.MAKE_MOVE;
                            lock.lock();
                            moveMessageReceived.signal();
                            lock.unlock();
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
            } catch (IOException e) {
                connected = false;
            } catch (WrongMessageException e) {
                System.out.println("Wrong message received");
                this.sendError("missing keys");
            }
        }

        try {
            socket.close();
            System.out.println("Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        System.out.println("ServerPlayer connected: " + lastMessage.getName());
        this.name = lastMessage.getName();
        boolean validName = lobby.addPlayerToLobby(this);
        if (validName) {
            this.sendLobbyStatus();
            state = ServerEvents.LOBBY;
        } else {
            this.sendError("lobby entry denied");
        }
    }

    private void join() {
        int roomNumber = lastMessage.getLobbyNumber();
        String opponent = lobby.getOpponentName(roomNumber);
        boolean validGame = lobby.addPlayerToRoom(this, roomNumber);
        if (validGame) {
            System.out.println(this.name + " added to a game");
            this.sendGameStatus(opponent);
            state = ServerEvents.GAME;
        } else {
            this.sendError("game full");
        }
    }


    private boolean isConnected() {
        return connected && socket.isConnected();
    }

    public String getName() {
        return name;
    }

    public void sendLobbyStatus() {
        String json = ServerMessage.sendLobbyStatus(lobby.getFreeRooms());
        this.sendMessageToClient(json);
    }

    public void sendGameStatus(String opponent) {
        String json = ServerMessage.sendGameStatus(opponent);
        this.sendMessageToClient(json);
    }

    private void sendError(String message) {
        String json = ServerMessage.sendError(message);
        this.sendMessageToClient(json);
    }

    public void sendMoveRequest() {
        String json = ServerMessage.sendMakeMove();
        this.sendMessageToClient(json);
    }

    public void sendOpponentMoved(String move) {
        String json = ServerMessage.sendOpponentMoved(move);
        this.sendMessageToClient(json);
    }

    public void sendGameStarted() {
        String json = ServerMessage.sendGameStarted();
        this.sendMessageToClient(json);
    }

    private void sendMessageToClient(String json) {
        this.out.println(json);
        this.out.flush();
    }

    public String requestMove() {
        lock.lock();
        this.sendMoveRequest();
        try {
            moveMessageReceived.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
        return lastMessage.getMove();
    }

    public String moveDenied() {
        lock.lock();
        this.sendError("move denied");
        this.sendMoveRequest();
        try {
            moveMessageReceived.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
        return lastMessage.getMove();
    }

    public void announceWinner(String winner) {
        String json = ServerMessage.sendGameOver(winner);
        this.out.println(json);
        this.out.flush();
    }

    public boolean wantsToStart() {
        return state == ServerEvents.STARTED;
    }

}
