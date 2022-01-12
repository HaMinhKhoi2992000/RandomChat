package server.controller;

import java.util.ArrayList;
import java.util.Set;

public class ClientManager {
    ArrayList<Client> clients;

    public ClientManager() {
        clients = new ArrayList<>();
    }

    public boolean add(Client client) {
        if (!clients.contains(client)) {
            clients.add(client);
            return true;
        }
        return true;
    }

    public boolean remove(Client c) {
        if (clients.contains(c)) {
            clients.remove(c);
            return true;
        }
        return false;
    }

    public int getSize() {
        return clients.size();
    }

    public Client find(String nickname) {
        for (Client client : clients) {
            if (client.getNickname() != null && client.getNickname().equals(nickname)) {
                return client;
            }
        }
        return null;
    }

    public void removeRejectedClient (String nickname) {
        for (Client client : clients) {
            if (client.getRejectedClients().contains(nickname)) {
                client.getRejectedClients().remove(nickname);
            }
        }
    }

    public Client findWaitingClient(Client currentClient, Set<String> excludedNicknames) {
        for (Client client : clients) {
            if (client != currentClient && client.isWaiting()) {
                if (excludedNicknames.contains(client.getNickname()))
                    continue;
                else if (client.getRejectedClients().contains(currentClient.getNickname()))
                    continue;
                else
                    return client;
            }
        }

        return null;
    }
}

