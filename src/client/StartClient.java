package client;

import client.controller.SocketHandler;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartClient {
    private static Socket socket;
    private static BufferedWriter out;
    private static BufferedReader in;
    public static SocketHandler socketHandler;
    private String hostname = "localhost";
    private int port = 5003;
    public StartClient() {
        socketHandler = new SocketHandler();
    }

    public void start() {
        connectToServer(hostname, port);
    }

    public void connectToServer(String hostname, int port) {
        new Thread(() -> {
            // establish connection
            boolean isConnected = StartClient.socketHandler.connect(hostname, port);
        }).start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new StartClient().start();
    }
}

