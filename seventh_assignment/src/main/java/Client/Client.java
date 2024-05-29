package Client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

// Client Class
public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private String username;

    Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeAll();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("please enter your username: ");
        String username = scanner.nextLine();

        Socket socket = new Socket("localhost", 4321);
        Client client = new Client(socket, username);
        client.sendMessage();
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // create a new thread for listening to others' messages
            MessageHandler messageHandler = new MessageHandler(socket);
            Thread listener = new Thread(messageHandler);
            listener.start();

            // get input from user and send it
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                bufferedWriter.write("| " + username + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
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

    public void closeAll() {
        try {
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
}