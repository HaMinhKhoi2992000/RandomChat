package client.controller;

import client.StartClient;
import client.view.guiEnums.GUIName;
import client.view.guiEnums.MainMenuState;
import shared.model.Data;
import shared.model.Message;
import shared.type.DataType;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            //listen to server
            listener = new Thread(this::listen);
            listener.start();

            // connect successfully
            return true;

        } catch (IOException e) {
            // connect failed
            return false;
        }
    }

    private void listen() {
        boolean isRunning = true;
        Data receivedData;
        String receivedContent = null;

        try {
            while (isRunning) {
                receivedData = (Data) in.readObject();

                if (receivedData != null) {
                    if (receivedData.getContent() != null)
                        receivedContent = receivedData.getContent();

                    switch (receivedData.getDataType()) {
                        case LOGIN:
                            onReceiveLogin(receivedContent);
                            break;

                        case PAIR_UP_WAITING:
                            onReceivePairUpWaiting(receivedContent);
                            break;

                        case CANCEL_PAIR_UP:
                            onReceiveCancelPairUp(receivedContent);
                            System.out.println(receivedContent);
                            break;

                        case REQUEST_PAIR_UP:
                            onReceiveRequestPairUp(receivedContent);
                            break;

                        case RESULT_PAIR_UP:
                            onReceiveResultPairUp(receivedContent);
                            break;

                        case JOIN_CHAT_ROOM:
                            onReceiveJoinChatRoom(receivedContent);
                            break;

                        case CHAT_MESSAGE:
                            onReceiveChatMessage(receivedContent);
                            break;

                        case LEAVE_CHAT_ROOM:
                            onReceiveLeaveChatRoom(receivedContent);
                            break;

                        case CLOSE_CHAT_ROOM:
                            onReceiveCloseChatRoom(receivedContent);
                            break;

                        case LOGOUT:
                            onReceiveLogout(receivedContent);
                            break;

                        case EXIT:
                            onReceiveExit(receivedContent);
                            isRunning = false;
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            isRunning = false;
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // closing resources
                in.close();
                out.close();
                socket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void onReceiveLogin(String received) {
        String[] splitted = received.split(";");
        String status = splitted[0];

        if (status.equals("failed")) {
            String failedMsg = splitted[1];
            StartClient.loginGUI.onFailed(failedMsg);

        } else if (status.equals("success")) {
            this.nickname = splitted[1];

            // tắt Login GUI khi client đăng nhập thành công
            StartClient.closeGUI(GUIName.LOGIN);
            // mở Main Menu GUI
            StartClient.openGUI(GUIName.MAIN_MENU);
        }
    }

    private void sendData(DataType dataType, String content) {
        Data data;
        String encryptedContent = null;

        if (content != null) {
            data = new Data(dataType, content);
        } else {
            data = new Data(dataType, content);
        }

        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onReceiveResultPairUp(String received) {
        String[] splitted = received.split(";");
        String status = splitted[0];

        if (status.equals("failed")) {
            String failedMsg = splitted[1];
            int option = JOptionPane.showConfirmDialog(StartClient.mainMenuGUI, failedMsg + ". Tiếp tục ghép đôi?", "Ghép đôi thất bại",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

            // stop pairing
            // reset display state of main menu
            StartClient.mainMenuGUI.setDisplayState(MainMenuState.DEFAULT);

            if(option == JOptionPane.YES_OPTION) {
                // continue pairing
                pairUp();
                return;
            }
        } else if (status.equals("success")) {
            // reset display state of main menu
            StartClient.mainMenuGUI.setDisplayState(MainMenuState.DEFAULT);

            System.out.println("Ghép đôi thành công");
        }
    }

    private void onReceivePairUpWaiting(String received) {
        StartClient.mainMenuGUI.setDisplayState(MainMenuState.FINDING_STRANGER);
    }

    private void onReceiveCancelPairUp(String received) {
        StartClient.mainMenuGUI.setDisplayState(MainMenuState.DEFAULT);
    }

    private void onReceiveRequestPairUp(String received) {
        // show stranger found state
        StartClient.mainMenuGUI.foundStranger(received);
    }

    private void onReceiveJoinChatRoom(String received) {
        // change GUI
        StartClient.closeGUI(GUIName.MAIN_MENU);
        StartClient.openGUI(GUIName.CHAT_ROOM);
        StartClient.chatRoomGUI.setClients(this.nickname, received);
    }

    private void onReceiveChatMessage(String received) {
        // convert received JSON message to Message
        Message message = Message.parse(received);
        StartClient.chatRoomGUI.addChatMessage(message);
    }

    private void onReceiveLeaveChatRoom(String received) {
        // change GUI
        StartClient.closeGUI(GUIName.CHAT_ROOM);
        StartClient.openGUI(GUIName.MAIN_MENU);
    }

    private void onReceiveCloseChatRoom(String received) {
        // change GUI
        StartClient.closeGUI(GUIName.CHAT_ROOM);
        StartClient.openGUI(GUIName.MAIN_MENU);

        // show notification
        JOptionPane.showMessageDialog(
                StartClient.mainMenuGUI,
                "Kết thúc trò chuyện do " + received, "Đóng",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void onReceiveLogout(String received) {
        // xóa nickname
        this.nickname = null;

        // chuyển sang login GUI
        StartClient.closeGUI(GUIName.MAIN_MENU);
        StartClient.openGUI(GUIName.LOGIN);
    }

    private void onReceiveExit(String received) {
        // đóng tất cả GUIs
        StartClient.closeAllGUIs();
    }

    public void login(String nickname) {
        sendData(DataType.LOGIN, nickname);
    }

    public void pairUp() {
        sendData(DataType.PAIR_UP, null);
    }

    public void acceptPairUp() {
        sendData(DataType.PAIR_UP_RESPONSE, "yes");
    }

    public void declinePairUp() {
        sendData(DataType.PAIR_UP_RESPONSE, "no");
    }

    public void cancelPairUp() {
        sendData(DataType.CANCEL_PAIR_UP, null);
    }

    public void sendChatMessage(Message message) {
        sendData(DataType.CHAT_MESSAGE, message.toJSONString());
    }

    public void leaveChatRoom() {
        sendData(DataType.LEAVE_CHAT_ROOM, null);
    }

    public void logout() {
        sendData(DataType.LOGOUT, null);
    }

    public void exit() {
        sendData(DataType.EXIT, null);
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
