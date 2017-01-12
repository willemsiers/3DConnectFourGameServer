import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Lobby {
    private List<Game> games;
    private List<Thread> gameThreads;
    private Map<Player,Integer> roomPlayers;
    private Lock lock;

    private List<Player> lobbyPlayers;


    public Lobby() {
        games = new ArrayList<>();
        gameThreads = new ArrayList<>();
        this.makeGameRooms(5);
        lobbyPlayers = new CopyOnWriteArrayList<>();
        roomPlayers = new HashMap<>();
        lock = new ReentrantLock();
    }


    public void makeGameRooms(int size){
        for (int i = 0; i < size; i++) {
            Game game = new Game();
            games.add(game);
            Thread thread = new Thread(game);
            thread.start();
            gameThreads.add(thread);
        }
    }

    public void addPlayerToLobby(Player player){
        lobbyPlayers.add(player);
    }


    public List<String> getFreeRooms(){
        lock.lock();
        List<String> result = new ArrayList<>();

        for (Game game : games){
            if (game.numberOfPlayers() == 0){
                result.add("No players yet");
            } else if (game.numberOfPlayers() == 1){
                result.add(game.getPlayer1Name());
            }
        }
        lock.unlock();
        return result;
    }

    public String getOpponentName(int roomNumber){
        return games.get(roomNumber).getPlayer1Name();
    }

    public Game addPlayerToRoom(Player player, int roomNumber){
        lock.lock();
        boolean roomFull = false;
        Game game = games.get(roomNumber);
        int numberOfPlayers = game.numberOfPlayers();
        if (numberOfPlayers == 0){
            game.setPlayer1(player);
        } else if (numberOfPlayers == 1) {
            game.setPlayer2(player);
        } else {
            roomFull = true;
        }
        lobbyPlayers.remove(player);
        lock.unlock();
        if (roomFull){
//            TODO: Room full exception
            return null;
        } else {
            roomPlayers.put(player,roomNumber);
            return game;
        }
    }

    public void disconnectPlayer(Player player){
        lock.lock();
        for (Game game : games){
            if (game.contains(player)){
//                REMOVE FROM GAME
            }
        }
        lock.unlock();
        if (lobbyPlayers.contains(player)){
            lobbyPlayers.remove(player);
        }

    }

    public void exitGame(Player player){
        Thread thread = gameThreads.get(roomPlayers.get(player));
        thread.interrupt();
        Game game = games.get(roomPlayers.get(player));
        if (game.isStarted()){
            game.otherPlayer(player).announceWinner(game.otherPlayer(player).getName());
        }


    }




}
