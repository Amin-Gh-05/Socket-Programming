package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

// Server Class
public class Server {
    public static void runServer() {
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(1234);
            socket = serverSocket.accept();
            // get and give stream from and to socket
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            // wrap in buffer
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            while (true) {
                String message = bufferedReader.readLine();
                System.out.println("CLIENT: " + message);
                // send message to client
                bufferedWriter.write("message received");
                bufferedWriter.newLine();
                bufferedWriter.flush();

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
        runServer();
    }
}