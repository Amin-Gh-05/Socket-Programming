package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server Class
public class Server {
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private final ServerSocket serverSocket;

    Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(4321);
        Server server = new Server(socket);
        server.runServer();
    }

    public void runServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("| a new client is connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                // create a thread for each client
                Thread thread = new Thread(clientHandler);
                pool.execute(thread);
            }
        } catch (IOException e) {
            System.out.println("| " + e);
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("| " + e);
        }
    }
}