package client;

import game.Move;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Rogier on 19-12-16.
 */
public class SimpleClient implements Runnable {
    private static final int CONNECT = 1;
    private static final int JOIN = 2;
    private static final int START = 3;
    private static final int RESTART = 4;
    private static final int EXIT = 5;
    private static final int DISCONNECT = 6;

    private ClientState state;


    private Socket socket;
    private BufferedReader reader;
    private PrintWriter out;
    private ClientGame clientGame;


    public SimpleClient() {
        state = ClientState.DISCONNECTED;
    }

    public JSONObject getServerMessage() {
        JSONObject object = null;
        JSONParser parser = new JSONParser();
        String userInput = null;
        try {
            while (userInput == null && isConnected()) {
                userInput = reader.readLine();
                object = (JSONObject) parser.parse(userInput);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return object;
    }

    private String getUserInput(String message) {

        System.out.println(message);
        String result = null;
        try {
            result = new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void run() {
        boolean running = true;
        JSONObject response;
        MessageType type;
        while (running) {
            int choice = typeCommand();
            switch (choice) {
                case CONNECT:
//                    String name = this.getUserInput("Choose")
                    if (state == ClientState.DISCONNECTED) {
                        String name = "RogierBot" + ((int) (Math.random() * 1000));
                        String host = this.getUserInput("Input host");
                        int port = Integer.parseInt(this.getUserInput("Input port"));
                        this.connect(name, host, port);
                    } else {
                        this.connectForLobby();
                    }

                    response = getServerMessage();
                    type = MessageType.fromString(response.get("event").toString(), "");
                    if (type == MessageType.LOBBY) {
                        JSONArray array = (JSONArray) response.get("free lobbies");
                        for (Object anArray : array) {
                            JSONObject obj = (JSONObject) anArray;
                            System.out.println("Game number: " + obj.get("room number") + " against: " + obj.get("opponent"));
                        }
                        state = ClientState.LOBBY;
                    } else {
                        System.out.println(response);
                    }
                    break;
                case JOIN:
                    int gameNumber = -1;
                    while (gameNumber == -1) {
                        try {
                            gameNumber = Integer.parseInt(getUserInput("Choose game number..."));
                        } catch (NumberFormatException e) {
                            System.out.println("Not a number");
                        }
                    }
                    this.join(gameNumber);
                    response = getServerMessage();
                    type = MessageType.fromString(response.get("event").toString(), "");
                    if (type == MessageType.GAME) {
                        System.out.println("Opponent: " + response.get("opponent"));
                        state = ClientState.GAME;
                    } else if (type == MessageType.ERROR) {
                        if (MessageType.fromString(response.get("event").toString(), response.get("reason").toString())
                                == MessageType.GAME_FULL) {
                            System.out.println("Room full");
                            this.connectForLobby();
                        }
                        state = ClientState.LOBBY;
                    }
                    break;
                case START:

                    this.startGame();

                    response = getServerMessage();
                    type = MessageType.fromString(response.get("event").toString(), "");

                    if (type == MessageType.STARTED) {
                        System.out.println("Game started... against " + response.get("opponent").toString());
                        this.play();
                        state = ClientState.GAME_OVER;
                    } else if (type == MessageType.LOBBY) {
                        JSONArray array = (JSONArray) response.get("free lobbies");
                        for (Object anArray : array) {
                            JSONObject obj = (JSONObject) anArray;
                            System.out.println("Game number: " + obj.get("room number") + " against: " + obj.get("opponent"));
                        }
                        state = ClientState.LOBBY;
                    } else {
                        System.out.println("impossible -1");
                    }


                    break;
                case RESTART:
                    this.restartGame();


                    response = getServerMessage();
                    type = MessageType.fromString(response.get("event").toString(), "");

                    if (type == MessageType.STARTED) {
                        System.out.println("Game restarted... against " + response.get("opponent").toString());
                        this.play();
                        state = ClientState.GAME_OVER;
                    } else if (type == MessageType.LOBBY) {
                        JSONArray array = (JSONArray) response.get("free lobbies");
                        for (Object anArray : array) {
                            JSONObject obj = (JSONObject) anArray;
                            System.out.println("Game number: " + obj.get("room number") + " against: " + obj.get("opponent"));
                        }
                        state = ClientState.LOBBY;
                    } else {
                        System.out.println("impossible -1");
                    }
                    break;
                case EXIT:
                    this.exitGame();
                    response = getServerMessage();
                    type = MessageType.fromString(response.get("event").toString(), "");

                    if (type == MessageType.LOBBY) {
                        JSONArray array = (JSONArray) response.get("free lobbies");
                        for (Object anArray : array) {
                            JSONObject obj = (JSONObject) anArray;
                            System.out.println("Game number: " + obj.get("room number") + " against: " + obj.get("opponent"));
                        }
                        state = ClientState.LOBBY;
                    }
                    break;
                case DISCONNECT:
                    this.disconnect();
                    running = false;
                    break;
            }


        }
    }

    public void play() {
        JSONObject object = getServerMessage();
        MessageType type = MessageType.fromString(object.get("event").toString(), "");

        if (type == MessageType.MOVE) {
            this.makeMove();
        } else if (type == MessageType.OPPONENT_MOVED) {
            clientGame.enterMove(object.get("move").toString());
            object = getServerMessage();
            if (MessageType.fromString(object.get("event").toString(), "") == MessageType.MOVE) {
                this.makeMove();
            } else {
                System.out.println("impossible 00");
            }
        } else {
            System.out.println(type);
        }
        int moveCount = 0;
        object = getServerMessage();
        type = MessageType.fromString(object.get("event").toString(), "");
        while (type != MessageType.GAME_OVER) {
            moveCount++;
            if (type == MessageType.OPPONENT_MOVED) {
//                System.out.println(object);
                clientGame.enterMove(object.get("move").toString());
            } else {
                System.out.println("impossible 1: " + type);
            }

            object = getServerMessage();
            type = MessageType.fromString(object.get("event").toString(), "");

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (type == MessageType.GAME_OVER) {
                System.out.println("Game over");
                break;
            } else if (type == MessageType.MOVE) {


                this.makeMove();
            } else {
                System.out.println("impossible 2");
            }
            object = getServerMessage();
            type = MessageType.fromString(object.get("event").toString(), "");
        }
        System.out.println("Game over!");
        System.out.println("The winner is: " + object.get("winner"));
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public int typeCommand() {
        boolean correctInput = false;
        int choice = 0;
        String command = "";
        while (!correctInput) {
            command = getUserInput("Choose command: \n" + getPossibleCommands());
            correctInput = correctCommand(command);
        }
        switch (command) {
            case "connect":
                choice = CONNECT;
                break;
            case "join":
                choice = JOIN;
                break;
            case "start":
                choice = START;
                break;
            case "restart":
                choice = RESTART;
                break;
            case "exit":
                choice = EXIT;
                break;
            case "disconnect":
                choice = DISCONNECT;
                break;
            default:
                break;

        }
        return choice;
    }

    private String getPossibleCommands() {
        switch (state) {
            case DISCONNECTED:
                return "[connect]";
            case LOBBY:
                return "[connect,join,disconnect]";
            case GAME:
                return "[start,exit,disconnect]";
            case GAME_OVER:
                return "[restart,exit,disconnect]";
        }
        return "no command";
    }

    private boolean correctCommand(String command) {
        switch (state) {
            case DISCONNECTED:
                return command.equals("connect");
            case LOBBY:
                return command.equals("connect") || command.equals("join") || command.equals("disconnect");
            case GAME:
                return command.equals("start") || command.equals("exit") || command.equals("disconnect");
            case GAME_OVER:
                return command.equals("restart") || command.equals("exit") || command.equals("disconnect");

        }
        return false;
    }

    private void connectForLobby() {
        JSONObject obj = new JSONObject();
        obj.put("action", "connect");
        out.println(obj.toJSONString());
        out.flush();
    }


    public void connect(String name, String host, int port) {
        try {
            InetAddress address;
            if (host == null) {
                address = InetAddress.getLocalHost();
            } else {
                address = InetAddress.getByName(host);
            }
            socket = new Socket(address, port);
            reader = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject();
        obj.put("action", "connect");
        obj.put("name", name);
        out.println(obj.toJSONString());
        out.flush();
    }

    public void join(int gameNumber) {
        JSONObject obj1 = new JSONObject();
        obj1.put("action", "join");
        obj1.put("room number", gameNumber);
        out.println(obj1.toJSONString());
        out.flush();
    }

    public void startGame() {
        clientGame = new ClientGame();
        JSONObject obj1 = new JSONObject();
        obj1.put("action", "start");
        out.println(obj1.toJSONString());
        out.flush();

    }

    public void restartGame() {
        clientGame = new ClientGame();
        JSONObject obj1 = new JSONObject();
        obj1.put("action", "restart");
        out.println(obj1.toJSONString());
        out.flush();

    }

    public void makeMove() {
        JSONObject obj1 = new JSONObject();
        Move move = clientGame.getRandomMove();
        System.out.println("Move: " + move.toString());
        obj1.put("action", "move");
        obj1.put("move", move.toString());
        out.println(obj1.toJSONString());
        out.flush();


    }

    public void exitGame() {
        JSONObject obj1 = new JSONObject();
        obj1.put("action", "exit game");
        out.println(obj1.toJSONString());
        out.flush();
    }


    public void disconnect() {
        JSONObject obj1 = new JSONObject();
        obj1.put("action", "disconnect");
        out.println(obj1.toJSONString());
        out.flush();
    }

    public BufferedReader getReader() {
        return reader;
    }


    public static void main(String[] args) {
        SimpleClient client = new SimpleClient();
        client.run();

    }


    enum MessageType {
        LOBBY, GAME, STARTED, MOVE, OPPONENT_MOVED, GAME_OVER, ERROR,
        TIME_ELAPSED, GAME_FULL, MISSING_KEYS, NO_SUCH_LOBBY, INVALID_MOVE, LOBBY_ENTRY_DENIED;

        public static MessageType fromString(String message, String error) {
            switch (message) {
                case "lobby":
                    return LOBBY;
                case "game":
                    return GAME;
                case "started":
                    return STARTED;
                case "make move":
                    return MOVE;
                case "opponent moved":
                    return OPPONENT_MOVED;
                case "game over":
                    return GAME_OVER;
                case "error":
                    switch (error) {
                        case "time elapsed":
                            return TIME_ELAPSED;
                        case "game full":
                            return GAME_FULL;
                        case "missing keys":
                            return MISSING_KEYS;
                        case "no such lobby":
                            return NO_SUCH_LOBBY;
                        case "invalid move":
                            return INVALID_MOVE;
                        case "lobby entry denied":
                            return LOBBY_ENTRY_DENIED;
                        default:
                            return ERROR;
                    }
                default:
                    return ERROR;
            }
        }

    }

}
