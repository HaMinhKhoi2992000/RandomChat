package shared.type;

public enum DataType {
    //    Server Response
    PUBLIC_KEY,
    RECEIVED_SECRET_KEY,
    PAIR_UP_WAITING,
    REQUEST_PAIR_UP,
    RESULT_PAIR_UP,
    JOIN_CHAT_ROOM,
    CLOSE_CHAT_ROOM,
    ERROR,

    //    Client Request
    LOGIN,
    SECRET_KEY,
    PAIR_UP,
    CANCEL_PAIR_UP,
    PAIR_UP_RESPONSE,
    CHAT_MESSAGE,
    LEAVE_CHAT_ROOM,
    LOGOUT,
    EXIT,
}
