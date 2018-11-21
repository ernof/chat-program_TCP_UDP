package nameserver;

import java.net.Socket;

public class Servers {
    private Socket socket;
    private int clients;
    private String localPort;

    public Servers(Socket socket){
        this.socket = socket;
        clients = 0;
    }

    public Socket getSocket(){
        return this.socket;
    }

    public int getClients(){
        return clients;
    }

    public String getLocalPort(){
        return localPort;
    }

    public void setLocalPort(String localPort) {
        this.localPort = localPort;
    }

    public void addClient(){
        ++this.clients;
    }

    public void removeClient(){
        --this.clients;
    }
}
