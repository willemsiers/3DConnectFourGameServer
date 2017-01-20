import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Rogier on 17-01-17.
 */
public class GameServlet extends HttpServlet {
    private GameServer gameServer;


    public GameServlet() {
        this.gameServer = new GameServer();
        Thread t1 = new Thread(gameServer);
        t1.start();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        if (req.getParameterMap().size() == 0) {
//
//
//
//            RequestDispatcher rd = req
//                    .getRequestDispatcher("index.jsp");
//            rd.forward(req, resp);
//
//        } else
        if (req.getParameterMap().containsKey("action")) {
            resp.addHeader("Access-Control-Allow-Origin", "*");
            if (req.getParameterValues("action")[0].equals("game")) {
                PrintWriter out = resp.getWriter();
                int id = 0;
                try {
                    id = Integer.parseInt(req.getParameterValues("id")[0]);
                    String json = gameServer.getLobby().getGame(id);
                    out.write(json);
                    out.flush();
                } catch (NumberFormatException e) {
                    out.write("NaN");
                    out.flush();
                }

            } else if (req.getParameterValues("action")[0].equals("lobby")) {
                PrintWriter out = resp.getWriter();
                out.write(gameServer.getLobby().getLobby());
                out.flush();
            }


        }
    }
}
