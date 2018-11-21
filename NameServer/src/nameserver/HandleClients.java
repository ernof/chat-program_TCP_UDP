package nameserver;

import javax.naming.Name;
import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class HandleClients implements Runnable{
    private int nameServerPort = 13500;
    DatagramSocket serverSocket;
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];
    NameServer nS;

    public HandleClients(NameServer nS) {
        this.nS = nS;
        try {
            serverSocket = new DatagramSocket(nameServerPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public void run() {

        while(true){
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String sentence = new String( receivePacket.getData());
            System.out.println("RECEIVED: " + sentence);
            if(sentence.charAt(0) != '0'){
                System.out.println("Unknown message from client");
            }

            String serverPort = nS.getNextServer();
            System.out.println("a server: " + serverPort);
            StringBuilder sb = new StringBuilder();
            /*sb.append("");
            sb.append(serverPort);
            String serverPortString = sb.toString();*/
            //System.out.println("server port to client: " + serverPortString);
            sendData = serverPort.getBytes();

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            //String capitalizedSentence = sentence.toUpperCase();
            //sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            try {
                System.out.println("Sending server port to client: " + serverPort);
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
