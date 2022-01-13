package client.controller;

import client.StartClient;
import client.view.guiEnums.GUIName;
import client.view.guiEnums.MainMenuStatus;
import shared.model.Data;
import shared.model.Message;
import shared.type.DataType;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketHandler {
    private Socket socket;
    private ObjectOutput out;
    private ObjectInput in;

    String nickname = null; // lưu nickname hiện tại

    Thread readThread = null;

    public boolean connect(String hostname, int port) {
        try {
            // Khởi tạo kết nối với port của serverSocket
            socket = new Socket(hostname, port);
            System.out.println("Ket noi den " + hostname + ":" + port + " co localport la " + socket.getLocalPort());

            // Nhận input và output stream
            this.out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            this.out.flush();
            this.in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            // đóng readThread cũ nếu có
            if (readThread != null && readThread.isAlive()) {
                readThread.interrupt();
            }

            //readThread lắng nghe server
            readThread = new Thread(this::read);
            readThread.start();

            // connect successfully
            return true;

        } catch (IOException e) {
            // connect failed
            return false;
        }
    }

    private void read() {
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
                            handleLogin(receivedContent);
                            break;

                        case PAIR_UP_WAITING:
                            handlePairUpWaiting(receivedContent);
                            break;

                        case CANCEL_PAIR_UP:
                            handleCancelPairUp(receivedContent);
                            break;

                        case REQUEST_PAIR_UP:
                            handleRequestPairUp(receivedContent);
                            break;

                        case RESULT_PAIR_UP:
                            handlePairUpResult(receivedContent);
                            break;

                        case JOIN_CHAT_ROOM:
                            handleJoinChatRoom(receivedContent);
                            break;

                        case CHAT_MESSAGE:
                            handleChatMessage(receivedContent);
                            break;

                        case LEAVE_CHAT_ROOM:
                            handleLeaveChatRoom(receivedContent);
                            break;

                        case CLOSE_CHAT_ROOM:
                            handleCloseChatRoom(receivedContent);
                            break;

                        case LOGOUT:
                            handleLogout(receivedContent);
                            break;

                        case EXIT:
                            handleExit(receivedContent);
                            isRunning = false;
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            isRunning = false;
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

    private void handleLogin(String received) {
        String[] splitData = received.split(";");
        String status = splitData[0];

        if (status.equals("failed")) {
            String failedMsg = splitData[1];
            StartClient.loginGUI.onFailed(failedMsg);

        } else if (status.equals("success")) {
            this.nickname = splitData[1];

            StartClient.openGUI(GUIName.MAIN_MENU);
            // Đăng nhập thành công thì tắt login GUI
            StartClient.closeGUI(GUIName.LOGIN);


        }
    }

    private void sendData(DataType dataType, String content) {
        Data data;
        data = new Data(dataType, content);
        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePairUpResult(String received) {
        String[] splitted = received.split(";");
        String status = splitted[0];

        if (status.equals("failed")) {
            String failMessage = splitted[1];
            int option = JOptionPane.showConfirmDialog(StartClient.mainMenuGUI, failMessage + ". Tiếp tục ghép đôi?", "Ghép đôi thất bại",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

            // stop pairing
            // reset display state of main menu
            StartClient.mainMenuGUI.setDisplayState(MainMenuStatus.DEFAULT);

            if(option == JOptionPane.YES_OPTION) {
                // continue pairing
                pairUp();
                return;
            }
        } else if (status.equals("success")) {
            // reset display state of main menu
            StartClient.mainMenuGUI.setDisplayState(MainMenuStatus.DEFAULT);

            System.out.println("Ghép đôi thành công");
        }
    }

    private void handlePairUpWaiting(String received) {
        StartClient.mainMenuGUI.setDisplayState(MainMenuStatus.FINDING_PARTNER);
    }

    private void handleCancelPairUp(String received) {
        StartClient.mainMenuGUI.setDisplayState(MainMenuStatus.DEFAULT);
    }

    private void handleRequestPairUp(String received) {
        // show stranger found state
        StartClient.mainMenuGUI.foundPartner(received);
    }

    private void handleJoinChatRoom(String received) {
        // Update giao diện và vào phòng chat
        StartClient.closeGUI(GUIName.MAIN_MENU);
        StartClient.openGUI(GUIName.CHAT_ROOM);
        StartClient.chatRoomGUI.setClients(this.nickname, received);
    }

    private void handleChatMessage(String received) {
        // convert received JSON message to Message
        Message message = Message.parse(received);
        StartClient.chatRoomGUI.addChatMessage(message);
    }

    private void handleLeaveChatRoom(String received) {
        // Update giao diện rời phòng chat
        StartClient.closeGUI(GUIName.CHAT_ROOM);
        StartClient.openGUI(GUIName.MAIN_MENU);
    }

    private void handleCloseChatRoom(String received) {
        // change GUI
        StartClient.closeGUI(GUIName.CHAT_ROOM);
        StartClient.openGUI(GUIName.MAIN_MENU);

        // show notification
        JOptionPane.showMessageDialog(
                StartClient.mainMenuGUI,
                "Trò chuyện kết thúc, " + received, "đã thoát",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleLogout(String received) {
        // Logout thì xóa nickname
        this.nickname = null;

        // quay về giao diện login, đóng menu chính
        StartClient.closeGUI(GUIName.MAIN_MENU);
        StartClient.openGUI(GUIName.LOGIN);
    }

    private void handleExit(String received) {
        // Thoát thì đóng hết giao diện
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
