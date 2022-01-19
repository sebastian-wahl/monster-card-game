package game.server;

import game.controller.battlecontroller.BattleQueueHandler;
import game.db.DatabaseConnectionProvider;
import game.helper.RepositoryHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static final int PORT = 10001;

    public boolean listening = true;
    private BattleQueueHandler battleQueueHandler;
    private RepositoryHelper repositoryHelper;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] strings) {
        new Server().listen();
    }

    public void listen() {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            if (!isDatabaseAvailable()) {
                System.out.println("Database not reachable!");
                return;
            }
            this.repositoryHelper = new RepositoryHelper();
            this.battleQueueHandler = new BattleQueueHandler();
            while (listening) {
                System.out.println("Listening on localhost:" + PORT);
                Socket client = serverSocket.accept();
                System.out.println("New Client Accepted, added to ThreadPool");
                executorService.execute(new ClientGameRunner(client, battleQueueHandler, new RepositoryHelper()));
            }
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isDatabaseAvailable() {
        try {
            DatabaseConnectionProvider.getConnection().close();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

}
