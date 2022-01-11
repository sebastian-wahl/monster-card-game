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
            this.battleQueueHandler = new BattleQueueHandler(repositoryHelper);
            while (listening) {
                System.out.println("listening on localhost:" + PORT);
                Socket client = serverSocket.accept();
                System.out.println("new Client Accepted, added to ThreadPool");
                executorService.execute(new ClientGameRunner(client, battleQueueHandler, repositoryHelper));
            }
            executorService.shutdown();
        } catch (IOException e) {
            executorService.shutdown();
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
