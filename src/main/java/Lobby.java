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
    private Map<ServerPlayer, Integer> roomPlayers;
    private Lock lock;

    private List<ServerPlayer> serverPlayers;

    public Lobby(int gameAmount) {
        games = new ArrayList<>();
        gameThreads = new ArrayList<>();
        this.makeGameRooms(gameAmount);
        serverPlayers = new CopyOnWriteArrayList<>();
        roomPlayers = new HashMap<>();
        lock = new ReentrantLock();
    }


    public void makeGameRooms(int size){
        for (int i = 0; i < size; i++) {
            Game game = new Game();
            games.add(game);
            Thread thread = new Thread(game);
            thread.start();
            System.out.println("Game " + games.indexOf(game) + " is running!");
            gameThreads.add(thread);
        }
    }

    public boolean addPlayerToLobby(ServerPlayer serverPlayer) {
        lock.lock();
        for (Player player : serverPlayers) {
            if (player.getName().equals(serverPlayer.getName())) {
                lock.unlock();
                return false;
            }
        }
        serverPlayers.add(serverPlayer);
        lock.unlock();
        return true;
    }


    public List<String> getFreeRooms(){
        lock.lock();
        List<String> result = new ArrayList<>();

        for (Game game : games){
            result.add(game.getPlayer1Name());
        }
        lock.unlock();
        return result;
    }

    public String getOpponentName(int roomNumber){
        return games.get(roomNumber).getPlayer1Name();
    }

    public boolean addPlayerToRoom(ServerPlayer serverPlayer, int roomNumber) {
        lock.lock();
        boolean roomFull = false;
        Game game = games.get(roomNumber);
        int numberOfPlayers = game.numberOfPlayers();
        if (numberOfPlayers == 0){
            game.setPlayer1(serverPlayer);
        } else if (numberOfPlayers == 1) {
            game.setPlayer2(serverPlayer);
        } else {
            roomFull = true;
        }
        lock.unlock();
        if (roomFull){
            return false;
        } else {
            roomPlayers.put(serverPlayer, roomNumber);
            return true;
        }
    }

    public void disconnectPlayer(ServerPlayer serverPlayer) {
        lock.lock();
        for (Game game : games){
            if (game.contains(serverPlayer)) {
                this.exitGame(serverPlayer);
            }
        }
        lock.unlock();
        if (serverPlayers.contains(serverPlayer)) {
            serverPlayers.remove(serverPlayer);
        }
        System.out.println("ServerPlayer " + serverPlayer.getName() + " disconnected");

    }

    public void exitGame(ServerPlayer serverPlayer) {
        lock.lock();
        int gameIndex = roomPlayers.get(serverPlayer);
        Thread thread = gameThreads.get(gameIndex);
        thread.interrupt();
        Game game = games.get(gameIndex);
        if (game.isStarted()){
            game.otherPlayer(serverPlayer).announceWinner(game.otherPlayer(serverPlayer).getName());
            if (game.otherPlayer(serverPlayer) instanceof ServerPlayer) {
                roomPlayers.remove(game.otherPlayer(serverPlayer));
            }
        }
        roomPlayers.remove(serverPlayer);
        game = new Game();
        thread = new Thread(game);
        games.set(gameIndex, game);
        gameThreads.set(gameIndex, thread);
        lock.unlock();
    }




}
