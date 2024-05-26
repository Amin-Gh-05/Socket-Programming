package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Client Class
public class Client {
    public static void connectServer() {
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            socket = new Socket("localhost", 1234);
            // get and give stream from and to socket
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            // wrap in buffer
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            // get user input from keyboard
            Scanner scanner = new Scanner(System.in);

            while (true) {
                // send message to server
                String message = scanner.nextLine();
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                // log server messages
                System.out.println("SERVER: " + bufferedReader.readLine());

                // break the loop
                if (message.equalsIgnoreCase("BYE")) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }

    public static void main(String[] args) {
        connectServer();
    }
}