package model;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ServerCore server;

    private BufferedReader in;
    private PrintWriter out;

    private String username = "Unknown";

    public ClientHandler(Socket socket, ServerCore server) {
        this.socket = socket;
        this.server = server;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        try {
            // First line from client is username (simple protocol)
            String first = in.readLine();
            if (first == null) return;

            username = first.trim().isEmpty() ? "READ-ONLY" : first.trim();
            server.onClientJoined(this);

            String msg;
            while ((msg = in.readLine()) != null) {

                if (msg.equalsIgnoreCase("bye") || msg.equalsIgnoreCase("end")) {
                    break;
                }

                if (msg.equalsIgnoreCase("allUsers")) {
                    out.println(server.getAllUsers());
                    continue;
                }

                // Normal message
                server.onMessage(this, msg);
            }
        } catch (IOException e) {
            // client dropped
        } finally {
            server.onClientLeft(this);
            close();
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    private void close() {
        try { socket.close(); } catch (IOException ignored) {}
    }
}
