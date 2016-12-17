import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Server implements Runnable {
    private Lobby lobby;

    private boolean isStopped;

    private List<Socket> players;

    private ServerSocket serverSocket;


    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isStopped = false;
    }

    public void run() {
        while (isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                ClientMessage message = new ClientMessage(in.readUTF());
                if (message.getAction() == ClientAction.CONNECT){
                    this.processClient(clientSocket,message.getName());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    public boolean isStopped() {
        return isStopped;
    }


    public void processClient(Socket clientSocket, String name){
        Thread t1 = new Thread(new Player(clientSocket, name));
    }
}
