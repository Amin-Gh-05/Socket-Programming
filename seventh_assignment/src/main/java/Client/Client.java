package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// Client Class
public class Client {
    private Socket socket;
    private DataOutputStream out;
    private String username;

    Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            closeAll();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("please enter your username: ");
        String username = scanner.nextLine();

        System.out.println("1- chat");
        System.out.println("2- download");
        System.out.print("please enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        Socket socket = new Socket("localhost", 4321);
        Client client = new Client(socket, username);

        if (choice == 1) {
            client.sendMessage();
        } else if (choice == 2) {
            client.downloadFile();
        } else {
            System.out.println("invalid choice");
        }
    }

    public void sendMessage() {
        try {
            out.writeUTF(username);
            out.writeUTF("sm");
            out.flush();

            // create a new thread for listening to others' messages
            MessageHandler messageHandler = new MessageHandler(socket);
            Thread listener = new Thread(messageHandler);
            listener.start();

            // get input from user and send it
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                out.writeUTF("| " + username + ": " + message);
                out.flush();
                // quit chat by typing "exit"
                if (message.equals("exit")) {
                    listener.interrupt();
                    closeAll();
                    break;
                }
            }
        } catch (IOException e) {
            closeAll();
        }
    }

    public void downloadFile() {
        try {
            out.writeUTF(username);
            out.writeUTF("df");
            out.flush();

            // create a new thread for receiving files
            DownloadHandler downloadHandler = new DownloadHandler(socket);
            Thread listener = new Thread(downloadHandler);
            listener.start();

            // get index from user
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                int index = scanner.nextInt();
                scanner.nextLine();
                out.writeInt(index);
                out.flush();
            }
        } catch (IOException e) {
            closeAll();
        }
    }

    public void closeAll() {
        try {
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
}