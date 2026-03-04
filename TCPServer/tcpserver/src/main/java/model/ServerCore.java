package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServerCore {

    public interface Listener {
        void onLog(String line);
        void onUsersChanged(List<String> users);
    }

    private final int port;
    private ServerSocket serverSocket;

    private final List<ClientHandler> clients = new ArrayList<>();
    private Listener listener;

    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ServerCore(int port) {
        this.port = port;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            log("Server started on port " + port);
            log("Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this);

                synchronized (clients) {
                    clients.add(client);
                }
                new Thread(client).start();
            }
        } catch (IOException e) {
            log("Server error: " + e.getMessage());
        }
    }

    void onClientJoined(ClientHandler client) {
        log("Welcome " + client.getUsername());
        broadcast("[" + now() + "] SERVER: " + client.getUsername() + " joined the chat");
        usersChanged();
    }

    void onClientLeft(ClientHandler client) {
        removeClient(client);
        log(client.getUsername() + " disconnected");
        broadcast("[" + now() + "] SERVER: " + client.getUsername() + " left the chat");
        usersChanged();
    }

    void onMessage(ClientHandler sender, String msg) {
        String formatted = "[" + now() + "] " + sender.getUsername() + ": " + msg;
        log(formatted);
        broadcast(formatted);
    }

    public String getAllUsers() {
        List<String> names = new ArrayList<>();
        synchronized (clients) {
            for (ClientHandler c : clients) names.add(c.getUsername());
        }
        return "Active users: " + String.join(", ", names);
    }

    private void usersChanged() {
        if (listener == null) return;
        List<String> names = new ArrayList<>();
        synchronized (clients) {
            for (ClientHandler c : clients) names.add(c.getUsername());
        }
        listener.onUsersChanged(names);
    }

    private void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler c : clients) c.sendMessage(message);
        }
    }

    private void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    private void log(String line) {
        if (listener != null) listener.onLog(line);
        System.out.println(line);
    }

    private String now() {
        return LocalTime.now().format(timeFmt);
    }
}
