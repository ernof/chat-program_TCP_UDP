package server;
import java.net.Socket;

public class userClass {
    private String username;
    private Socket mySocket;

    public userClass(Socket mySocket) {
        this.mySocket = mySocket;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return mySocket;
    }
}
