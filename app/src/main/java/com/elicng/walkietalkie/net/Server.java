package com.elicng.walkietalkie.net;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Elic on 15-04-26.
 */
public class Server {

    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running = true;
    private Collection<Socket> connectedClients = new ArrayList<>();

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public Server() {
        try {
            serverSocket = new ServerSocket(0);
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
                        Socket clientSocket = serverSocket.accept();
                        connectedClients.add(clientSocket);
                        Log.d("com.elicng.walkietalkie", "Client connected : " + clientSocket.getInetAddress().getHostAddress());

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
                clientSocket.getOutputStream().write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
    }

}
