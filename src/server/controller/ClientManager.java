package server.controller;

import java.util.ArrayList;
import java.util.Set;

public class ClientManager {
    ArrayList<ClientThread> clientThreads;

    public ClientManager() {
        clientThreads = new ArrayList<>();
    }

    public boolean add(ClientThread clientThread) {
        if (!clientThreads.contains(clientThread)) {
            clientThreads.add(clientThread);
            return true;
        }
        return true;
    }

    public boolean remove(ClientThread c) {
        if (clientThreads.contains(c)) {
            clientThreads.remove(c);
            return true;
        }
        return false;
    }

    public int getSize() {
        return clientThreads.size();
    }

    public ClientThread find(String nickname) {
        for (ClientThread clientThread : clientThreads) {
            if (clientThread.getNickname() != null && clientThread.getNickname().equals(nickname)) {
                return clientThread;
            }
        }
        return null;
    }

    public void removeRejectedClient (String nickname) {
        for (ClientThread clientThread : clientThreads) {
            if (clientThread.getRefusedClients().contains(nickname)) {
                clientThread.getRefusedClients().remove(nickname);
            }
        }
    }

    public ClientThread findWaitingClient(ClientThread currentClientThread, Set<String> excludedNicknames) {
        for (ClientThread clientThread : clientThreads) {
            if (clientThread.isWaiting() &&  clientThread != currentClientThread ) {
                if (excludedNicknames.contains(clientThread.getNickname()))
                    continue;
                else if (clientThread.getRefusedClients().contains(currentClientThread.getNickname()))
                    continue;
                else
                    return clientThread;
            }
        }

        return null;
    }
}

