import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Rogier on 19-12-16.
 */
public class SimpleClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter out;
    private static int nameCount;

    public SimpleClient() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), 8080);
            reader = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        JSONObject obj = new JSONObject();
        obj.put("action", "connect");
        obj.put("name", "Rogier" + nameCount);
        nameCount++;
        out.println(obj.toJSONString());
        out.flush();
    }

    public void join() {
        JSONObject obj1 = new JSONObject();
        obj1.put("action", "join");
        obj1.put("room number", 0);
        out.println(obj1.toJSONString());
        out.flush();
    }

    public void startGame() {
        JSONObject obj1 = new JSONObject();
        obj1.put("action", "start");
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
        client.connect();
        client.join();
        client.startGame();
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        client.disconnect();

    }
}
