package game.server;

import game.db.DatabaseConnectionProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    public static final int PORT = 10001;

    public boolean listening = true;
    protected BlockingQueue<String> battleQueue;

    public static void main(String[] strings) {
        new Server().listen();
    }

    public void listen() {

        this.battleQueue = new LinkedBlockingQueue<>();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            if (!isDatabaseAvailable()) {
                System.out.println("Database not reachable!");
                return;
            }
            while (listening) {
                System.out.println("listening on localhost:" + PORT);
                Socket client = serverSocket.accept();
                System.out.println("new Client Accepted");
                new Thread(new ClientGameRunner(client, battleQueue)).start();
            }

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
