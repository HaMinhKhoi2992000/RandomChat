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


    private String acceptPairUp = "";     // giá trị: "yes", "no", ""
    private boolean isWaiting = false;
    private volatile Set<String> refusedClients = new HashSet<>();

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
                            handleLogin(receivedContent);
                            break;
                        case CHAT_MESSAGE:
                            handleChatMessage(receivedContent);
                            break;

                        case LEAVE_ROOM:
                            handleLeaveChatRoom(receivedContent);
                            break;
                        case PAIR_UP:
                            handlePairUp(receivedContent);
                            break;

                        case CANCEL_PAIR_UP:
                            handleCancelPairUp(receivedContent);
                            break;

                        case PAIR_UP_RESPONSE:
                            handlePairUpResponse(receivedContent);
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
        } catch (IOException e) {
            e.printStackTrace();
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


    private void handleLogin(String received) {
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

    private void handlePairUp(String received) {
        // Tìm một người khác đang ghép cặp
        ClientThread partner = StartServer.clientManager.findWaitingClient(this, refusedClients);

        if (partner == null) {
            // set trạng thái đang chờ ghép cặp
            this.isWaiting = true;

            // Gửi lệnh hiện giao diện đợi cho client
            sendData(DataType.PAIR_UP_WAITING, null);

        } else {
            // Khi tìm thấy có người cũng đang đợi thì bắt đầu hỏi yêu cầu ghép cặp
            // trong lúc hỏi thì phải tắt trạng thái đợi của 2 bên (để nếu client khác ghép đôi thì sẽ tránh việc bị ghép đè)
            this.isWaiting = false;
            partner.isWaiting = false;

            // lưu socket của đối phương để dùng khi serverSocket nhận được result-pair-match
            this.partner = partner;
            partner.partner = this;

            // trả thông tin người đã pair về cho 2 clients
            this.sendData(DataType.REQUEST_PAIR_UP, partner.nickname);
            partner.sendData(DataType.REQUEST_PAIR_UP, this.nickname);
        }
    }

    private void handleCancelPairUp(String received) {
        // gỡ cờ đang đợi ghép cặp
        this.isWaiting = false;

        // báo cho client để tắt giao diện đang đợi ghép cặp
        sendData(DataType.CANCEL_PAIR_UP, null);
    }

    private void handlePairUpResponse(String received) {
        // save accept pair status
        this.acceptPairUp = received;

        // if partner has left
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
            sendData(DataType.JOIN_ROOM, partner.nickname);
            partner.sendData(DataType.JOIN_ROOM, this.nickname);

            // reset acceptPairMatchStatus
            this.acceptPairUp = "";
            partner.acceptPairUp = "";
        }
    }

    private void handleChatMessage(String received) {
        Message message = Message.parse(received);
        ClientThread partner = StartServer.clientManager.find(message.getRecipient());

        if (partner != null) {
            // send message to partner
            partner.sendData(DataType.CHAT_MESSAGE, received);
        }
    }

    private void handleLeaveChatRoom(String received) {
        // xóa danh sách client đã từ chối
        this.refusedClients.clear();
        this.partner.refusedClients.clear();

        // Thông báo cho người kia là bạn đã thoát
        this.partner.sendData(DataType.CLOSE_ROOM, this.nickname + " đã thoát phòng");

        sendData(DataType.LEAVE_ROOM, null);
    }

    private void handleLogout(String received) {
        // xóa tên này khỏi tên bị từ chối của tất cả client khác
        StartServer.clientManager.removeRefusedClient(this.nickname);

        // xóa và thiết lập lại tất cả thông tin
        this.nickname = null;
        this.isWaiting = false;
        this.refusedClients.clear();

        sendData(DataType.LOGOUT, null);
    }

    private void handleExit(String received) {
        // reset nickname and waiting status
        this.nickname = null;
        this.isWaiting = false;

        sendData(DataType.EXIT, null);
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
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


}