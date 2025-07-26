
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 1234;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    Server.broadcast(message, this);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected.");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clientHandlers.remove(this);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
