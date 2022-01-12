package client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SendMessage implements Runnable {
    private BufferedWriter out;
    private Socket socket;
    public SendMessage(Socket s, BufferedWriter o) {
        this.socket = s;
        this.out = o;
    }
    public void run() {
        try {
            while(true) {
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String data = stdIn.readLine();
                System.out.println("Input from client: " + data);
                out.write(data+'\n');
                out.flush();
                if(data.equals("bye"))
                    break;
            }
            System.out.println("Client closed connection");
            out.close();
            socket.close();
        } catch (IOException e) {}
    }
}

class ReceiveMessage implements Runnable {
    private BufferedReader in;
    private Socket socket;
    public ReceiveMessage(Socket s, BufferedReader i) {
        this.socket = s;
        this.in = i;
    }
    public void run() {
        try {
            while(true) {
                String data = in.readLine();
                System.out.println("Receive: " + data);
            }
        } catch (IOException e) {}
    }
}
public class StartClient {

    private static Socket socket;

    private static BufferedWriter out;
    private static BufferedReader in;

    public static void main(String[] args) throws IOException, InterruptedException {
        String hostname = "localhost";
        int port = 5003;
        socket = new Socket(hostname, port);
        System.out.println("Client connected");
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ExecutorService executor = Executors.newFixedThreadPool(2);
        SendMessage send = new SendMessage(socket, out);
        ReceiveMessage recv = new ReceiveMessage(socket, in);
        executor.execute(send);
        executor.execute(recv);
    }
}

