package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageHandler implements Runnable {
    private final Socket socket;
    private final DataInputStream in;

    MessageHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        String message;

        while (socket.isConnected()) {
            try {
                message = in.readUTF();
                System.out.println(message);
            } catch (IOException e) {
                closeAll();
            }
        }
    }

    public void closeAll() {
        try {
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}