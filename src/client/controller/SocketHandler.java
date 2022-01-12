package client.controller;

import java.io.*;
import java.net.Socket;

public class SocketHandler {
    private Socket socket;
    private ObjectOutput out;
    private ObjectInput in;

    String nickname = null; // lưu nickname hiện tại

    Thread listener = null;

    public boolean connect(String hostname, int port) {
        try {
            // Khởi tạo kết nối với port của server
            socket = new Socket(hostname, port);
            System.out.println("Connected to " + hostname + ":" + port + ", localport: " + socket.getLocalPort());

            // Nhận input và output stream
            this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.out.flush();
            this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
//            listener = new Thread(this::listen);
//            listener.start();

            // connect successfully
            return true;

        } catch (IOException e) {
            // connect failed
            return false;
        }
    }
}
