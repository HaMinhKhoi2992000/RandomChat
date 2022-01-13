package server.controller;

import server.StartServer;
import shared.model.Data;
import shared.model.Message;
import shared.type.DataType;
import shared.type.StringMessage;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ClientThread implements Runnable {
    private Socket clientSocket;
    private ObjectOutput out;
    private ObjectInput in;

    private String nickname;
    ClientThread partner;
    private volatile Set<String> refusedClients = new HashSet<>();

    private String acceptPairUp = "";     // giá trị: "yes", "no", ""
    private boolean isWaiting = false;

    public ClientThread(Socket clientSocket) throws IOException {
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
                   receivedContent = receivedData.getContent();
                    switch (receivedData.getDataType()) {
                        case LOGIN:
                            onReceiveLogin(receivedContent);
                            break;

                        case PAIR_UP:
                            onReceivePairUp(receivedContent);
                            break;

                        case CANCEL_PAIR_UP:
                            onReceiveCancelPairUp(receivedContent);
                            break;

                        case PAIR_UP_RESPONSE:
                            onReceivePairUpResponse(receivedContent);
                            break;

                        case CHAT_MESSAGE:
                            onReceiveChatMessage(receivedContent);
                            break;

                        case LEAVE_CHAT_ROOM:
                            onReceiveLeaveChatRoom(receivedContent);
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

    private void onReceiveLogin(String received) {
        String status = "failed;";

        //Tìm xem có ClientThread nào trong ClientManager dùng nickname này chưa, nếu chưa thì login thành công
        ClientThread existedClientThread = StartServer.clientManager.find(received);

        //Data gửi đi có dạng status;nickname nếu thành công hoặc status;<thông báo lỗi> nếu thất bại
        if (existedClientThread != null) {
            sendData(DataType.LOGIN, status + StringMessage.NICKNAME_ALREADY_IN_USE);
        } else {
            status = "success;";
            this.nickname = received;
            sendData(DataType.LOGIN, status + nickname);
        }
    }

    private void onReceivePairUp(String received) {
        // Tìm một người khác đang ghép cặp
        ClientThread partner = StartServer.clientManager.findWaitingClient(this, refusedClients);

        if (partner == null) {
            // đặt cờ là đang đợi ghép cặp
            this.isWaiting = true;

            // client hiển thị trạng thái đợi ghép cặp
            sendData(DataType.PAIR_UP_WAITING, null);

        } else {
            // nếu có người cũng đang đợi ghép đôi thì bắt đầu hỏi yêu cầu ghép cặp
            // trong lúc hỏi thì phải tắt trạng thái đợi của 2 bên (để nếu client khác ghép đôi thì sẽ tránh việc bị ghép đè)
            this.isWaiting = false;
            partner.isWaiting = false;

            // lưu email đối thủ để dùng khi serverSocket nhận được result-pair-match
            this.partner = partner;
            partner.partner = this;

            // trả thông tin đối phương về cho 2 clients
            this.sendData(DataType.REQUEST_PAIR_UP, partner.nickname);
            partner.sendData(DataType.REQUEST_PAIR_UP, this.nickname);
        }
    }

    private void onReceiveCancelPairUp(String received) {
        // gỡ cờ đang đợi ghép cặp
        this.isWaiting = false;

        // báo cho client để tắt giao diện đang đợi ghép cặp
        sendData(DataType.CANCEL_PAIR_UP, null);
    }

    private void onReceivePairUpResponse(String received) {
        // save accept pair status
        this.acceptPairUp = received;

        // if stranger has left
        if (partner == null) {
            sendData(DataType.RESULT_PAIR_UP, "failed;" + StringMessage.PARTNER_LEFT);
            return;
        }

        // if one decline
        if (received.equals("no")) {
            // if both have no response (both will decline)
            // check the rejected list of the stranger to avoid sending the rejection response twice
            // if this client is on the stranger's rejected list, it means that the stranger refused first
            if (!this.partner.getRefusedClients().contains(this.nickname)) {
                // add rejected client to list
                this.refusedClients.add(partner.getNickname());

                // reset acceptPairUpStatus
                this.acceptPairUp = "";
                partner.acceptPairUp = "";

                // send data
                this.sendData(DataType.RESULT_PAIR_UP, "failed;" + StringMessage.YOU_REFUSE_PAIR);
                partner.sendData(DataType.RESULT_PAIR_UP, "failed;" + StringMessage.PARTNER_REFUSE_PAIR);
            }
        }

        // if both accept
        if (received.equals("yes") && partner.acceptPairUp.equals("yes")) {
            // send success pair match
            this.sendData(DataType.RESULT_PAIR_UP, "success");
            partner.sendData(DataType.RESULT_PAIR_UP, "success");

            // send join chat room status to client
            sendData(DataType.JOIN_CHAT_ROOM, partner.nickname);
            partner.sendData(DataType.JOIN_CHAT_ROOM, this.nickname);

            // reset acceptPairMatchStatus
            this.acceptPairUp = "";
            partner.acceptPairUp = "";
        }
    }

    private void onReceiveChatMessage(String received) {
        Message message = Message.parse(received);
        ClientThread stranger = StartServer.clientManager.find(message.getRecipient());

        if (stranger != null) {
            // send message to stranger
            stranger.sendData(DataType.CHAT_MESSAGE, received);
        }
    }

    private void onReceiveLeaveChatRoom(String received) {
        // reset rejected clients of both
        this.refusedClients.clear();
        this.partner.refusedClients.clear();

        // notify the stranger that you have exited
        this.partner.sendData(DataType.CLOSE_CHAT_ROOM, this.nickname + " đã thoát phòng");

        // TODO leave chat room
        sendData(DataType.LEAVE_CHAT_ROOM, null);
    }

    private void onReceiveLogout(String received) {
        // remove this client nickname from the rejected list of all clients
        StartServer.clientManager.removeRejectedClient(this.nickname);

        // reset all infos
        this.nickname = null;
        this.isWaiting = false;
        this.refusedClients.clear();

        sendData(DataType.LOGOUT, null);
    }

    private void onReceiveExit(String received) {
        // reset nickname and waiting status
        this.nickname = null;
        this.isWaiting = false;

        sendData(DataType.EXIT, null);
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public ClientThread getPartner() {
        return partner;
    }
    public boolean isWaiting() {
        return isWaiting;
    }
    public Set<String> getRefusedClients() {
        return refusedClients;
    }
    public void setPartner(ClientThread stranger) {
        this.partner = partner;
    }
}