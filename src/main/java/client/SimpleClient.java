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
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter out;
    private ClientGame clientGame;
    private static int nameCount = 1;


    public SimpleClient(int port) {
        try {
            socket = new Socket(InetAddress.getLocalHost(), port);
            reader = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
//        String name = getUserInput("Enter your name");
        String name = "Rogier" + ((int) (Math.random() * 1000));
        this.connect(name);
        JSONObject object = getServerMessage();

        while (MessageType.fromString(object.get("event").toString(), "") != MessageType.LOBBY) {
            System.out.println(object.get("event").toString());
            connect(name);
            object = getServerMessage();
        }
        JSONArray array = (JSONArray) object.get("free lobbies");
        for (Object anArray : array) {
            JSONObject obj = (JSONObject) anArray;
            System.out.println("Game number: " + obj.get("room number") + " against: " + obj.get("opponent"));
        }


        while (MessageType.fromString(object.get("event").toString(), "") != MessageType.GAME) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int gameNumber = -1;
            while (gameNumber != -1) {

                gameNumber = Integer.parseInt(getUserInput("Choose game number..."));

            }
            this.join(4);
            object = getServerMessage();
            if (MessageType.fromString(object.get("event").toString(), "") == MessageType.ERROR) {
                System.out.println(object);
            }
        }





        System.out.println("Opponent: " + object.get("opponent"));
        boolean start = true;
        while (!start) {
            String answer = getUserInput("Ready to start: Y/N");
            if (answer.equals("Y")) {
                start = true;
            }
        }
        this.startGame();

        object = getServerMessage();
        MessageType type = MessageType.fromString(object.get("event").toString(), "");

        if (type == MessageType.STARTED) {
            System.out.println("Game started... against " + object.get("opponent").toString());
        } else {
            System.out.println("impossible -1");
        }

        object = getServerMessage();
        type = MessageType.fromString(object.get("event").toString(), "");

        if (type == MessageType.MOVE) {
            this.makeMove();
        } else if (type == MessageType.OPPONENT_MOVED) {
            clientGame.enterMove(object.get("move").toString());
            object = getServerMessage();
            if (MessageType.fromString(object.get("event").toString(), "") == MessageType.MOVE) {
                this.makeMove();
            } else {
                System.out.println("impossible 0");
            }
        } else {
            System.out.println(type);
        }
        object = getServerMessage();
        type = MessageType.fromString(object.get("event").toString(), "");
        while (type != MessageType.GAME_OVER) {
            if (type == MessageType.OPPONENT_MOVED) {
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
        this.exitGame();
        this.disconnect();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }


    public void connect(String name) {
        JSONObject obj = new JSONObject();
        obj.put("action", "connect");
        obj.put("name", name);
        nameCount++;
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
        SimpleClient client = new SimpleClient(9090);
        client.run();
//        client.disconnect();

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
