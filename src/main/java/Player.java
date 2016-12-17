import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Player implements Runnable {
    private Socket socket;
    private String name;
    private boolean connected;
    private DataInputStream in;
    private DataOutputStream out;

    public Player(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }


    public void run() {
        while(connected){

        }
    }
}
