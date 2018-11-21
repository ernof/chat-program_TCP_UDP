package server;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class ServerClass {
    ServerSocket myServerSocket;
    boolean ServerOn = true;
    List<userClass> userList = new ArrayList<userClass>();
    private int port = 13002;
    private int nameServerPort = 13500;
    BufferedWriter output;
    java.net.Socket socket;
    private String ip = "127.0.0.1";
    public OutputStreamWriter out;

    public ServerClass() {
        try {
            myServerSocket = new ServerSocket(port);
        } catch(IOException ioe) {
            System.out.println("Could not create server socket on port 13001. Quitting.");
            System.exit(-1);
        }

        try {
            socket = new java.net.Socket(ip, nameServerPort);
            System.out.println("Socket done");
        } catch (IOException ex) {
            Logger.getLogger(ServerClass.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            out = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String outString = "" + port;
        PrintWriter pw = new PrintWriter(out, true);
        pw.println(outString);
        pw.flush();

        while(ServerOn) {
            try {
                Socket clientSocket = myServerSocket.accept();
                ClientServiceThread cliThread = new ClientServiceThread(clientSocket);
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

    public static void main (String[] args) {
        //File file = new File("../database.txt");
        //Scanner input = new Scanner(file);

        new ServerClass();
    }

    class ClientServiceThread extends Thread {
        Socket myClientSocket;
        boolean m_bRunThread = true;
        public ClientServiceThread() {
            super();
        }

        ClientServiceThread(Socket s) {
            myClientSocket = s;
        }

        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;
            System.out.println(
                    "Accepted Client Address - " + myClientSocket);

            userClass newUser = new userClass(myClientSocket);
            String tempUser = "";

            try {
                in = new BufferedReader(
                        new InputStreamReader(myClientSocket.getInputStream()));
                /*out = new PrintWriter(
                        new OutputStreamWriter(myClientSocket.getOutputStream()));*/

                while(m_bRunThread) {
                    String clientCommand = in.readLine();
                    System.out.println("Client Says :" + clientCommand);
                    char[] buffer = clientCommand.toCharArray();

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
                        if(clientCommand.charAt(0) == '0'){
                            int i = 2, j, userLen;
                            String userString = "", userLenString = "", users = "";
                            //System.out.println("Public message");

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

                            File file = new File("../database.txt");
                            Scanner scan = new Scanner(file);
                            char[] linechar;
                            String userName = "", oldName = userString;

                            while(scan.hasNext()){
                                String str = scan.next();
                                //System.out.println(str);

                                linechar = str.toCharArray();
                                i = 6;
                                while(linechar[i] != '|'){
                                    userName += linechar[i];
                                    ++i;
                                }
                                //System.out.println(userName + " " + userString);
                                while(userName.equals(userString)){
                                    //System.out.println("egyenlo");
                                    userString += '1';
                                }
                                userName = "";
                                //System.out.println(userName);
                            }
                            if(!oldName.equals(userString)){
                                out = new PrintWriter(
                                        new OutputStreamWriter(myClientSocket.getOutputStream()));
                                out.println("3|" + userString + "|");
                                out.flush();
                            }
                            //System.out.println("kilepettttttttttttt");
                            newUser.setUsername(userString);
                            userList.add(newUser);

                            output = new BufferedWriter(new FileWriter("../database.txt", true));
                            output.append(port + "|" + userString + "|" + myClientSocket.getPort());
                            output.newLine();
                            output.close();

                            users = "0";
                            for(Iterator<userClass> k = userList.iterator(); k.hasNext();){
                                userClass oneUser = k.next();
                                //System.out.print(oneUser.getUsername());
                                users +=  "|" + oneUser.getUsername().length() + "|" + oneUser.getUsername();
                            }
                            System.out.println();
                            users += '\0';

                            for(Iterator<userClass> p = userList.iterator(); p.hasNext();){
                                userClass oneUser = p.next();
                                out = new PrintWriter(
                                        new OutputStreamWriter(oneUser.getSocket().getOutputStream()));
                                out.println(users);
                                out.flush();
                            }
                        }

                        if(clientCommand.charAt(0) == '1'){
                            for(Iterator<userClass> i = userList.iterator(); i.hasNext();){
                                userClass oneUser = i.next();
                                out = new PrintWriter(
                                        new OutputStreamWriter(oneUser.getSocket().getOutputStream()));
                                out.println(clientCommand);
                                out.flush();
                            }
                        }


                    }
                }
            } catch(Exception e) {
                //e.printStackTrace();

            }
            finally {
                for(Iterator<userClass> k = userList.iterator(); k.hasNext();){
                    userClass oneUser = k.next();
                    if(oneUser.getSocket().getPort() == (myClientSocket.getPort())){
                        //System.out.println("elkaptaaaaaaaaaaaa");
                        tempUser = oneUser.getUsername();
                        k.remove();
                        break;
                    }
                }
                File inputFile = new File("../database.txt");
                File tempFile = new File("../myTempFile.txt");

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(inputFile));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                    int i;
                    char[] fileChar;
                    String fileUser = "", currentLine;

                    while((currentLine = reader.readLine()) != null) {
                        // trim newline when comparing with lineToRemove
                        i = 6; fileUser = "";
                        System.out.println("hereeeeeeeeeeeeeeee");
                        fileChar = currentLine.toCharArray();
                        while(fileChar[i] != '|'){
                            fileUser += fileChar[i];
                            ++i;
                        }
                        System.out.println("userFromFile: " + fileUser + " " + "tempUser: " + tempUser);

                        String trimmedLine = currentLine.trim();
                        if(tempUser.equals(fileUser)) continue;
                        writer.write(currentLine + " NEW " + System.getProperty("line.separator"));
                    }
                    writer.close();
                    reader.close();
                    boolean success = tempFile.renameTo(inputFile);
                    if(success == true) System.out.println("Renamed");
                    //tempFile.delete();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }






                try {
                    in.close();
                    out.close();
                    myClientSocket.close();
                    System.out.println("...Stopped");
                } catch(IOException ioe) {
                    //ioe.printStackTrace();
                }
            }
        }
    }
}