package server.controller;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    private Socket clientSocket;
    private ObjectOutput out;
    private ObjectInput in;

    public Client(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;

        this.out = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        this.out.flush();
        this.in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
    }

    @Override
    public void run() {

    }
}