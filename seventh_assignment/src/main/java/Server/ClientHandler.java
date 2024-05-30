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
    private final DataInputStream in;
    private final DataOutputStream out;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        this.username = in.readUTF();
        clientHandlers.add(this);
        System.out.println("| SERVER: client " + this.username + " connected");
    }

    @Override
    public void run() {
        try {
            String mode = in.readUTF();
            if (mode.equals("sm")) {
                messageMenu();
            } else if (mode.equals("df")) {
                downloadMenu();
            } else {
                System.out.println("invalid mode");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            closeAll();
        }
    }

    public void messageMenu() {
        for (String message : messages) {
            try {
                out.writeUTF(message);
                out.flush();
            } catch (IOException e) {
                closeAll();
            }
        }

        String message;

        while (socket.isConnected()) {
            try {
                message = in.readUTF();
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
                    clientHandler.out.writeUTF(message);
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeAll();
            }
        }
    }

    public void downloadMenu() {
        File[] files = new File("data").listFiles();
        if (files == null) {
            System.out.println("no files found");
            return;
        }

        try {
            out.writeInt(files.length);
        } catch (IOException e) {
            closeAll();
        }
        int i = 1;
        for (File file : files) {
            try {
                out.writeUTF(i + "- " + file.getName());
                out.flush();
                i++;
            } catch (IOException e) {
                closeAll();
            }
        }

        int index;

        while (socket.isConnected()) {
            try {
                index = in.readInt();
                downloadFile(index);
            } catch (IOException e) {
                closeAll();
                break;
            }
        }
    }

    public void downloadFile(int fileIndex) throws IOException {
        File[] files = new File("data").listFiles();
        assert files != null;

        int bytes;
        File file = files[fileIndex - 1];
        FileInputStream fileInputStream = new FileInputStream(file);

        out.writeUTF(file.getName());
        out.writeInt((int) file.length());
        out.flush();

        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);
            out.flush();
        }

        fileInputStream.close();
    }

    public void closeAll() {
        removeClient();
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
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