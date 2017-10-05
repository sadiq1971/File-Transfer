package FileTransfer;

import sun.rmi.log.LogInputStream;
import tcpdiff.ReadThread;
import tcpdiff.WriteThreadClient;
import util.NetworkUtil;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.Scanner;

/**
 * Created by sadiq on 9/19/17.
 */
public class Client {

    static boolean  LogIn(String Id,NetworkUtil nc){

        nc.write(Id);
        String result=(String) nc.read();
        System.out.println(result);

        if(result.equals("Connected")){
            return true;
        }

        return false;
    }



    public static void main(String args[]) {
        String id;
        NetworkUtil nc;
        Scanner scanner =new Scanner(System.in);
        System.out.printf("Id: ");
        id=scanner.nextLine();


        try {

            String hostAdress="127.0.0.1";
            //String hostAdress="172.16.193.21";
            int serverPort=33333;
            nc = new NetworkUtil(hostAdress,serverPort);


            //[Try to LogIn]

            boolean LogInResult;
            LogInResult=LogIn(id,nc);


            if(!LogInResult){
                nc.closeConnection();
            }
            else {
                new TransmissionStateClient(nc);
            }


        } catch(Exception e) {
            System.out.println ("In main Client: "+e);
            //return;

        }
    }
}
