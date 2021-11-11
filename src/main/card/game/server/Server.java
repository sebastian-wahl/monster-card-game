package card.game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    public static final int PORT = 9999;

    public boolean listening;
    protected BlockingQueue<String> battleQueue;

    public static void main(String[] strings) {
        new Server().listen();
    }

    public void listen() {
        listening = true;
        this.battleQueue = new LinkedBlockingQueue<>();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (listening) {
                System.out.println("listening");
                Socket client = serverSocket.accept();
                System.out.println("new Client Accepted");
                new Thread(new ClientGameRunner(client, battleQueue)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
