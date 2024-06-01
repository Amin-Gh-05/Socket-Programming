package Client;

import java.io.*;
import java.net.Socket;

public class DownloadHandler implements Runnable {
    private final Socket socket;
    private final DataInputStream in;

    DownloadHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                System.out.println(in.readUTF());
            }

            String fileName = in.readUTF();

            int bytes;
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);

            int size = in.readInt();
            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = in.read(buffer, 0, Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }

            fileOutputStream.close();
        } catch (IOException e) {
            closeAll();
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