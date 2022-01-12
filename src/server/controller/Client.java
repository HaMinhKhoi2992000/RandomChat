package server.controller;

import server.StartServer;
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
        boolean isRunning = true;
        Data receivedData;
        String receivedContent = null;
        sendData(DataType.CANCEL_PAIR_UP, "HELLO");
        try {
            while (isRunning) {
                receivedData = (Data) in.readObject();

                if (receivedData != null) {
                    sendData(DataType.CANCEL_PAIR_UP, "HELLO");
                    receivedContent = receivedData.getContent();
                    System.out.println(receivedContent);
                    switch (receivedData.getDataType()) {
                        case LOGIN:
                            //onReceiveLogin(receivedContent);
                            break;

                        case PAIR_UP:
                           // onReceivePairUp(receivedContent);
                            break;

                        case CANCEL_PAIR_UP:
                           // onReceiveCancelPairUp(receivedContent);
                            break;

                        case PAIR_UP_RESPONSE:
                           // onReceivePairUpResponse(receivedContent);
                            break;

                        case CHAT_MESSAGE:
                            //onReceiveChatMessage(receivedContent);
                            break;

                        case LEAVE_CHAT_ROOM:
                          //  onReceiveLeaveChatRoom(receivedContent);
                            break;

                        case LOGOUT:
                            //System.out.println(nickname + " is logged out");
                           // onReceiveLogout(receivedContent);
                            break;

                        case EXIT:
                           // onReceiveExit(receivedContent);
                            isRunning = false;
                            break;
                    }
                }
            }
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
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
                clientSocket.close();

                // remove from clientManager
                StartServer.clientManager.remove(this);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void sendData(DataType dataType, String content) {
        Data data;
        data = new Data(dataType, content);

        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}