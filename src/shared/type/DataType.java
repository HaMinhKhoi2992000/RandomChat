package shared.type;

public enum DataType {
    //    Server Response
    PAIR_UP_WAITING,
    REQUEST_PAIR_UP,
    RESULT_PAIR_UP,
    JOIN_ROOM,
    CLOSE_ROOM,

    //    Client Request
    LOGIN,
    PAIR_UP,
    CANCEL_PAIR_UP,
    PAIR_UP_RESPONSE,
    CHAT_MESSAGE,
    LEAVE_ROOM,
    LOGOUT,
    EXIT,
}
