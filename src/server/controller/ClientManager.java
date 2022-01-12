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
}

