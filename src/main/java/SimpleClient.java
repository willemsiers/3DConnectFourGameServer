import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by Rogier on 19-12-16.
 */
public class SimpleClient {


    public static void main(String[] args) {
        JSONObject obj = new JSONObject();
        obj.put("action", "connect");
        obj.put("name", "Rogier");


        try {
            Socket socket = new Socket("192.168.1.11",8080);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            out.println(obj.toJSONString());
            out.flush();
            System.out.println(reader.readLine());
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
            JSONObject obj1 = new JSONObject();
            obj1.put("action", "join");
            obj1.put("room number", 0);
            out.println(obj1.toJSONString());
            out.flush();
            System.out.println(reader.readLine());
            obj1 = new JSONObject();
            obj1.put("action", "disconnect");
            out.println(obj1.toJSONString());
            out.flush();

        } catch (IOException e ) {
            e.printStackTrace();
        }


    }
}
