package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();
    private static final List<String> messages = new ArrayList<>();
    private final Socket socket;
    private final String username;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.username = bufferedReader.readLine();
        clientHandlers.add(this);
        System.out.println("| SERVER: client " + this.username + " connected");
    }

    @Override
    public void run() {
        for (String message : messages) {
            try {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                closeAll();
            }
        }

        String message;

        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                sendMessage(message);
            } catch (IOException e) {
                closeAll();
                break;
            }
        }
    }

    public void sendMessage(String message) {
        messages.add(message);

        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.username.equals(username)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeAll();
            }
        }
    }

    public void closeAll() {
        removeClient();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removeClient() {
        clientHandlers.remove(this);
        sendMessage("| SERVER: client " + this.username + " disconnected");
    }
}