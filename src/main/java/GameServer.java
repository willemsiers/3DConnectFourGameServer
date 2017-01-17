import server.Lobby;
import server.ServerPlayer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameterMap().size() == 0) {
            RequestDispatcher rd = req
                    .getRequestDispatcher("home.jsp");
            rd.forward(req, resp);
        } else if (req.getParameterMap().containsKey("action")) {
            if (req.getParameterValues("action")[0].equals("game")) {
                PrintWriter out = resp.getWriter();
                int id = 0;
                try {
                    id = Integer.parseInt(req.getParameterValues("id")[0]);
                    String json = lobby.getGame(id);
                    out.write(json);
                    out.flush();
                } catch (NumberFormatException e) {
                    out.write("NaN");
                    out.flush();
                }

            } else if (req.getParameterValues("action")[0].equals("lobby")) {
                PrintWriter out = resp.getWriter();
                out.write(lobby.getLobby());
                out.flush();
            }


        }
    }
}
