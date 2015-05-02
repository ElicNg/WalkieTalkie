package com.elicng.walkietalkie.net;

import android.util.Log;

import com.elicng.walkietalkie.Properties;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Create a ServerSocket to listen to connecting clients.
 * Transfers Audio bytes to clients.
 */
public class Server {

    private boolean running = true;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private Collection<Socket> connectedClients = new ArrayList<>();

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public Server() {
        try {
            serverSocket = new ServerSocket(Properties.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
            while (running) {
                try {
                    // 1. Accept new client connection.
                    Socket newClientSocket = serverSocket.accept();

                    // 2. Check if it is a new client.
                    String newClientSocketAddress = newClientSocket.getInetAddress().getHostAddress();
                    boolean isNewClient = true;
                    for (Socket connectedClientSocket : connectedClients) {
                        if (connectedClientSocket.getInetAddress().getHostAddress().equals(newClientSocketAddress)) {
                            // Not a new client. Continue.
                            isNewClient = false;
                            break;
                        }
                    }

                    // 3. Add the client to our list if new client.
                    if (isNewClient) {
                        connectedClients.add(newClientSocket);
                        Log.d("com.elicng.walkietalkie", "Client connected : " + newClientSocket.getInetAddress().getHostAddress());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
        });

        serverThread.start();
    }

    public void writeByte(byte[] bytes) {
        for (Socket clientSocket : connectedClients) {
            try {
                if (clientSocket.isConnected()) {
                    clientSocket.getOutputStream().write(bytes);
                } else {
                    connectedClients.remove(clientSocket);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
    }

}
