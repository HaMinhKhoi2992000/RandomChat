package server;

import server.controller.Client;
import server.controller.ClientManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StartServer {
    public static ServerSocket server;
    public static boolean isTurnOff = false;
    public static volatile ClientManager clientManager;

    private void start(){
        try {
            int port = 5003;
            server = new ServerSocket(port);
            System.out.println("Server is running at port " + port + ".");

            // Tạo client manager
            clientManager = new ClientManager();

            // Tạo threadpool
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    10, // corePoolSize
                    100, // maximumPoolSize
                    10, // thread timeout
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(8) // queueCapacity
            );

            // Vòng lặp while lắng nghe và chấp nhận kết nối từ client.
            while (!isTurnOff) {
                try {
                    System.out.println("Waiting for a client ...");

                    // Dùng biến socket để lưu lại client vừa kết nối
                    Socket socket = server.accept();

                    // Tạo client runable
                    Client client = new Client(socket);
                    clientManager.add(client);

                    // execute client runnable
                    executor.execute(client);

                } catch (IOException ex) {
                    isTurnOff = true;
                }
            }

            System.out.println("Shut down executor...");
            executor.shutdownNow();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new StartServer().start();
    }
}
