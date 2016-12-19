import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Rogier on 16-12-16 in Enschede.
 */
public class Lobby {




    private List<Game> rooms;
    private Lock lock;

    private List<Player> lobbyPlayers;


    public Lobby() {
        rooms = new ArrayList<>();
        this.makeGameRooms(5);
        lobbyPlayers = new CopyOnWriteArrayList<>();
        lock = new ReentrantLock();
    }


    public void makeGameRooms(int size){
        for (int i = 0; i < size; i++) {
            rooms.add(new Game());
        }
    }

    public void addPlayerToLobby(Player player){
        lobbyPlayers.add(player);
    }


    public List<String> getFreeRooms(){
        lock.lock();
        List<String> result = new ArrayList<>();

        for (Game game : rooms){
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
        return rooms.get(roomNumber).getPlayer1Name();
    }

    public Game addPlayerToRoom(Player player, int roomNumber){
        lock.lock();
        boolean roomFull = false;
        Game game = rooms.get(roomNumber);
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
            return null;
        } else {
            return game;
        }
    }

    public void disconnectPlayer(Player player){
        lock.lock();
        for (Game game : rooms){
            if (game.contains(player)){
//                REMOVE FROM GAME
            }
        }
        lock.unlock();
        if (lobbyPlayers.contains(player)){
            lobbyPlayers.remove(player);
        }

    }

}
