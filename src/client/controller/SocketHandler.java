package client.controller;

import shared.model.Data;
import shared.type.DataType;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
                System.out.println(".");
                receivedData = (Data) in.readObject();

                if (receivedData != null) {
                    if (receivedData.getContent() != null)
                        receivedContent = receivedData.getContent();

                    switch (receivedData.getDataType()) {
                        case PUBLIC_KEY:

                            break;

                        case RECEIVED_SECRET_KEY:
                            //onReceivedSecretKey(receivedContent);
                            break;

                        case LOGIN:
                            //onReceiveLogin(receivedContent);
                            break;

                        case PAIR_UP_WAITING:
                           // onReceivePairUpWaiting(receivedContent);
                            break;

                        case CANCEL_PAIR_UP:
                            //onReceiveCancelPairUp(receivedContent);
                            System.out.println(receivedContent);
                            break;

                        case REQUEST_PAIR_UP:
                           // onReceiveRequestPairUp(receivedContent);
                            break;

                        case RESULT_PAIR_UP:
                           // onReceiveResultPairUp(receivedContent);
                            break;

                        case JOIN_CHAT_ROOM:
                            //onReceiveJoinChatRoom(receivedContent);
                            break;

                        case CHAT_MESSAGE:
                          //  onReceiveChatMessage(receivedContent);
                            break;

                        case LEAVE_CHAT_ROOM:
                            //onReceiveLeaveChatRoom(receivedContent);
                            break;

                        case CLOSE_CHAT_ROOM:
                           // onReceiveCloseChatRoom(receivedContent);
                            break;

                        case LOGOUT:
                            //onReceiveLogout(receivedContent);
                            break;

                        case EXIT:
                           // onReceiveExit(receivedContent);
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

    public void login(String nickname) {
        sendData(DataType.LOGIN, nickname);
    }
}
