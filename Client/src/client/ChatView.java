package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Package;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatView {
    private JTextField textField1;
    private JButton sendButton;
    public static JFrame jframe;
    private JPanel panel1;
    private JTextArea messageView;
    private JTextArea userView;

    private String usernameHere;
    private String ip = "127.0.0.1"; //"192.168.113.85";
    private int port = 13001;//13000
    private int nameServerPort = 13500;
    public OutputStreamWriter out;
    java.net.Socket socket;

    public ChatView(String username){

        messageView.setEnabled(false);
        userView.setEnabled(false);

        this.usernameHere = username;
        jframe = new JFrame("Chat " + usernameHere);
        jframe.setContentPane(this.panel1);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.pack();
        jframe.setVisible(true);
        textField1.requestFocus();

        //UDP connection to NameServer
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            try {
                InetAddress IPAddress = InetAddress.getByName("localhost");
                String askForPort = "0";
                byte[] sendData = askForPort.getBytes();
                byte[] receiveData = new byte[1024];

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip), nameServerPort);
                clientSocket.send(sendPacket);


                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String modifiedSentence = new String(receivePacket.getData());
                System.out.println("Problem STRING: " + modifiedSentence);

                port = Integer.parseInt(modifiedSentence.replaceAll("\\p{C}", ""));
                System.out.println("FROM NAMESERVER:" + modifiedSentence);
                clientSocket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        //Got UDP Server port

        try {
            socket = new java.net.Socket(ip, port);
            System.out.println("Socket done");
        } catch (IOException ex) {
            Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Szal inditasa, amely varja az uzeneteket a szervertol
        Thread im = new Thread(new IncomingMessagesThread(this, usernameHere, socket, messageView, userView));
        im.start();

        try {
            out = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Protocol pr = new Protocol(usernameHere);
        PrintWriter pw = new PrintWriter(out, true);
        pw.println(pr.getString());
        pw.flush();

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    out = new OutputStreamWriter(socket.getOutputStream());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                if(textField1.getText().isEmpty()) {
                    textField1.requestFocus();
                    return;
                }
                String msg = textField1.getText();
                System.out.println("Sending msg as: " + usernameHere);
                Protocol pr = new Protocol(usernameHere, msg);
                System.out.println("sending: " + pr.getString());
                textField1.setText("");
                textField1.requestFocus();

                PrintWriter pw = new PrintWriter(out, true);
                pw.println(pr.getString());
                pw.flush();
            }
        });
    }

    public String getUsername() {
        return usernameHere;
    }

    public void setUsername(String username){
        this.usernameHere = username;
        System.out.println("new username set: " + username);
    }

    public void changeTitle(String title){
        jframe.setTitle(title);
    }
}
