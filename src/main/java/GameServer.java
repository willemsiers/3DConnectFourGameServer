import server.Lobby;
import server.ServerPlayer;

import javax.servlet.http.HttpServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class GameServer extends HttpServlet implements Runnable {
    private Lobby lobby;

    private boolean isStopped;



    private ServerSocket serverSocket;


    public GameServer() {
        lobby = new Lobby(10);
        try {
            serverSocket = new ServerSocket(9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isStopped = false;
    }

    public void run() {
        while (!isStopped()){
            Socket clientSocket;
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
        ServerPlayer serverPlayer = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            serverPlayer = new ServerPlayer(clientSocket, in, lobby);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread t1 = new Thread(serverPlayer);
        t1.start();
    }

    public static void main(String[] args) {
        Thread t1 = new Thread(new GameServer());
        t1.start();
    }

    public Lobby getLobby() {
        return lobby;
    }
}
