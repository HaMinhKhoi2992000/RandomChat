package client;

import client.controller.SocketHandler;
import client.view.gui.ChatRoomGUI;
import client.view.gui.LoginGUI;
import client.view.gui.MainMenuGUI;
import client.view.guiEnums.GUIName;

import javax.swing.*;
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

    public static LoginGUI loginGUI;
    public static MainMenuGUI mainMenuGUI;
    public static ChatRoomGUI chatRoomGUI;
    public StartClient() {
        socketHandler = new SocketHandler();
        // Customize LookAndFeel UI
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception ignored){}
        initGUIs();
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

    private void onFailed(String failedMessage) {
        loginGUI.setLoading(false, null);
        JOptionPane.showMessageDialog(loginGUI, failedMessage, "Không thể kết nối đến server", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    public void initGUIs() {
        loginGUI = new LoginGUI();
        mainMenuGUI = new MainMenuGUI();
        chatRoomGUI = new ChatRoomGUI();
    }

    public static void openGUI(GUIName guiName) {
        if (guiName != null) {
            switch (guiName) {
                case LOGIN:
                    loginGUI = new LoginGUI();
                    loginGUI.setVisible(true);
                    break;

                case MAIN_MENU:
                    mainMenuGUI = new MainMenuGUI();
                    mainMenuGUI.setVisible(true);
                    break;

                case CHAT_ROOM:
                    chatRoomGUI = new ChatRoomGUI();
                    chatRoomGUI.setVisible(true);
                    break;

                default:
                    break;
            }
        }
    }

    public static void closeGUI(GUIName guiName) {
        if (guiName != null) {
            switch (guiName) {
                case LOGIN:
                    loginGUI.dispose();
                    break;

                case MAIN_MENU:
                    mainMenuGUI.dispose();
                    break;

                case CHAT_ROOM:
                    chatRoomGUI.dispose();
                    break;

                default:
                    break;
            }
        }
    }

    public static void closeAllGUIs() {
        loginGUI.dispose();
        mainMenuGUI.dispose();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new StartClient().start();
    }
}

