/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.in;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author fogarasiee
 */
class IncomingMessagesThread implements Runnable{
    private String username;
    java.net.Socket socket;
    private JTextArea messageView;
    private JTextArea userView;
    private ChatView cV;

    public IncomingMessagesThread(ChatView cV, String username, java.net.Socket socket, JTextArea messageView, JTextArea userView) {
        this.username = username;
        this.socket = socket;
        this.messageView = messageView;
        this.userView = userView;
        this.cV = cV;
    }


    public void run(){
        System.out.println("Message thread");

        InputStreamReader in = null;
        try {
            in = new InputStreamReader(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(IncomingMessagesThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader br = new BufferedReader(in);
        char[] buffer = new char[100];

        while(true){

            try {

                int count = br.read(buffer, 0, 100);
                System.out.println("Client received something: ");
                for(int i=0; i<count; ++i){
                    System.out.print(buffer[i]);
                }
                //System.out.println("\ncount: "+count+"\n");

                if(buffer[0] == '0'){
                    int i = 2, j = 2, userLen;
                    String userString = "", userLenString = "";
                    userView.setText("");

                    while(buffer[i] != '\0' && i < count-2){
                        userString = ""; userLenString = "";

                        System.out.println("buffer[i]: " + buffer[i]);
                        while(buffer[i] != '|'){
                            userLenString += buffer[i];
                            ++i;
                        }
                        ++i;
                        userLen = Integer.parseInt(userLenString);
                        //System.out.println("username length: " + userLen);

                        j = i+userLen;
                        for(; i<j; ++i){
                            userString += buffer[i];
                        }
                        ++i;

                        userView.append(userString + "\n");
                    }


                }
                else if(buffer[0] == '1'){
                    int i = 2, j, userLen, messageLen, reader;
                    String userString = "", messageString = "", messageLenString = "", userLenString = "";
                    System.out.println("Public message");

                    while(buffer[i] != '|'){
                        userLenString += buffer[i];
                        ++i;
                    }
                    ++i;
                    userLen = Integer.parseInt(userLenString);
                    //System.out.println("username length: " + userLen);

                    j = i+userLen;
                    for(; i<j; ++i){
                        userString += buffer[i];
                    }
                    ++i;
                    //System.out.println("username: " + userString);

                    while(buffer[i] != '|'){
                        messageLenString += buffer[i];
                        ++i;
                    }
                    //System.out.println("message length1: " + messageLenString);
                    ++i;

                    messageLen = Integer.parseInt(messageLenString);

                    //System.out.println("message length2: " + messageLen);

                    j = i + messageLen;
                    for(; i<j; ++i){
                        messageString += buffer[i];
                    }
                    //System.out.println("message: " + messageString);

                    messageView.append(userString + ": " + messageString + "\n");
                    //JScrollBar vertical = ViewMessages.getVerticalScrollBar();
                    //vertical.setValue( vertical.getMaximum() );
                }
                else if(buffer[0] == '3'){
                    int i = 2;
                    String newName = "";
                    while(buffer[i] != '|'){
                        newName += buffer[i];
                        ++i;
                    }
                    this.username = newName;
                    cV.setUsername(newName);
                    cV.changeTitle(newName);

                    System.out.println("new name: " + newName);
                }
                else{
                    System.out.println("Incoming package not recognized");
                }

                System.out.println(buffer);

            } catch (IOException ex) {
                Logger.getLogger(IncomingMessagesThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                Thread.sleep(1000);

            } catch (InterruptedException ex) {
                Logger.getLogger(IncomingMessagesThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
