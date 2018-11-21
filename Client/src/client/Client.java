/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.*;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Amos1
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ClientLogin clientLogin = new ClientLogin();

        clientLogin.StartLogin();

        /*Protocol p = new Protocol("22", "user");
        System.out.println(p.getString());

        Protocol p2 = new Protocol("55555", "user2");
        System.out.println(p2.getString());*/

    }

}
