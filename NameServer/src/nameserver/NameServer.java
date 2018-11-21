package nameserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NameServer {
    ServerSocket myServerSocket;
    boolean ServerOn = true;
    private int port = 13500;
    private int nextServer = 0;
    BufferedWriter output;
    private List<Servers> serverList = new ArrayList<Servers>();

    public static void main(String[] args) {
        new NameServer();
    }

    public NameServer() {
        try {
            myServerSocket = new ServerSocket(port);
            System.out.println("Server done with port: " + myServerSocket.getLocalPort());
        } catch(IOException ioe) {
            System.out.println("Could not create server socket on port 13500. Quitting.");
            System.exit(-1);
        }

        Thread im = new Thread(new HandleClients(this));
        im.start();

        while(ServerOn) {
            try {
                System.out.println("Waiting to accept server");
                Socket serverSocket = myServerSocket.accept();
                System.out.println("Server accepted: " + serverSocket);

                Servers serv = new Servers(serverSocket); //csatlakozott server tarolasa osztalyban
                serverList.add(serv); //Server socketek tarolasa

                /*in = new BufferedReader(
                        new InputStreamReader(serverSocket.getInputStream()));*/


                ClientServiceThread cliThread = new ClientServiceThread(serverSocket, serv);
                cliThread.start();
            } catch(IOException ioe) {
                System.out.println("Exception found on accept. Ignoring. Stack Trace :");
                ioe.printStackTrace();
            }
        }
        try {
            myServerSocket.close();
            System.out.println("Server Stopped");
        } catch(Exception ioe) {
            System.out.println("Error Found stopping server socket");
            System.exit(-1);
        }
    }

    class ClientServiceThread extends Thread {
        Socket myClientSocket;
        boolean m_bRunThread = true;
        Servers serve;
        public ClientServiceThread() {
            super();
        }

        ClientServiceThread(Socket s, Servers serv) {
            myClientSocket = s;
            serve = serv;
        }

        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;

            try {
                in = new BufferedReader(
                        new InputStreamReader(myClientSocket.getInputStream()));
                /*out = new PrintWriter(
                        new OutputStreamWriter(myClientSocket.getOutputStream()));*/

                System.out.println("Egy uj server lepett fel: " + myClientSocket);
                while(m_bRunThread) {
                    String clientCommand = in.readLine();
                    System.out.println("Server Says :" + clientCommand);
                    //char[] buffer = clientCommand.toCharArray();
                    serve.setLocalPort(clientCommand);

                    if(!ServerOn) {
                        System.out.print("Server has already stopped");
                        out.println("Server has already stopped");
                        out.flush();
                        m_bRunThread = false;
                    }
                    if(clientCommand.equalsIgnoreCase("quit")) {
                        m_bRunThread = false;
                        System.out.print("Stopping client thread for client : ");
                    } else if(clientCommand.equalsIgnoreCase("end")) {
                        m_bRunThread = false;
                        System.out.print("Stopping client thread for client : ");
                        ServerOn = false;
                    } else {

                        if(clientCommand.charAt(0) == '1'){

                    }


                    }
                }
            } catch(Exception e) {
                //e.printStackTrace();

            }
            finally {


                try {
                    in.close();
                    //out.close();
                    myClientSocket.close();
                    System.out.println("...Stopped");
                } catch(IOException ioe) {
                    //ioe.printStackTrace();
                }
            }
        }
    }

    public String getNextServer(){
        int i, mini = 100, at = 0;
        for(i=0; i<serverList.size(); ++i){
            System.out.println("getNextServer, server: " + serverList.get(i).getLocalPort() + " " + serverList.get(i).getClients());
            if(serverList.get(i).getClients() < mini) {
                mini = serverList.get(i).getClients();
                at = i;

            }
        }
        serverList.get(at).addClient();
        return serverList.get(at).getLocalPort();
    }
}
