/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author fogarasiee
 */
public class Protocol {
    private String request;
    private String message;
    private String userFrom;

    public Protocol(String userFrom) {
        this.userFrom = userFrom;
        this.request = "0";
    }

    public Protocol(String userFrom, String message) {
        this.userFrom = userFrom;
        this.message = message;
        this.request = "1";
    }

    public String getString(){
        if(request.equals("0")){
            return request + "|" + userFrom.length() + "|" + userFrom;
        }
        if(request.equals("1")){
            return request + "|" + userFrom.length() + "|" + userFrom + "|" + message.length() + "|" + message;
        }
        else return null;
    }


}
