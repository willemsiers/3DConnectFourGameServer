import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Server implements Runnable {
    private Lobby lobby;

    private boolean isStopped;

    private List<Player> players;

    private ServerSocket serverSocket;


    public Server(int port) {
        players = new ArrayList<>();
        lobby = new Lobby();
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        isStopped = false;
    }

    public void run() {
        while (!isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                this.processClient(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    public boolean isStopped() {
        return isStopped;
    }


    public void processClient(Socket clientSocket){
        Player player = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            player = new Player(clientSocket,in,lobby);
        } catch (IOException e) {
            e.printStackTrace();
        }
        players.add(player);
        Thread t1 = new Thread(player);
        t1.start();
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new Server(8080));
        t1.start();
    }
}
