package FileTransfer;

import util.NetworkUtil;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

public class ReadFileClient implements Runnable {

    NetworkUtil nc;
    Thread thread;
    volatile boolean shutdown=false;
    String fileId;
    File fileToReceive;
    int totalLenght;


    ReadFileClient(NetworkUtil nc){
        this.nc=nc;
        this.thread=new Thread(this);
        thread.start();
    }


    @Override
    public void run() {
        try {
            while (!shutdown && !nc.isClosed){
                nc.isReceiving=true;


                //Object o=nc.read();
                Object o=nc.readForReceive(nc.isReceiving);

                if(o==null){
                    //continue;
                    nc.isReceiving=false;
                    Shutdown();
                    continue;
                    //return;
                    //System.out.println("hi");
                }


                if(o instanceof TransmittedFile){
                    TransmittedFile description=(TransmittedFile)o;
                    fileId=description.getFileId();

                    String message="User "+description.getItem().senderId+" wants to " +
                            "send you a file named "+description.getItem().fileName +"(" +
                            description.getItem().getFileLength()/1024+"kb). Press y to " +
                            "accept n to reject";
                    System.out.println(message);

                    //System.out.println("here");

                    Scanner scanner=new Scanner(System.in);
                    String response=scanner.nextLine();

                    if(!response.equals("y")){
                        nc.write(new FileItem("n",0));
                        nc.isReceiving=false;
                        Shutdown();
                    }
                    else {

                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                        File folderPath=null;

                        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            //System.out.println(fileChooser.getSelectedFile());

                            folderPath=fileChooser.getSelectedFile();
                            System.out.println(folderPath.getPath());
                        }




                        fileToReceive=new File(description.getItem().getFileName());
                        totalLenght=description.getItem().getFileLength();

                        System.out.println(totalLenght);

                        System.out.println(fileToReceive.length());

                        nc.write(new FileItem("y",(int)fileToReceive.length()));

                        FileOutputStream fo=new FileOutputStream(fileToReceive);
                        fileId=description.getFileId();

                        if((int)fileToReceive.length()>0) {
                            BufferedInputStream bi = new BufferedInputStream(
                                    new FileInputStream(fileToReceive));

                            byte[] temp = new byte[(int)fileToReceive.length()];
                            bi.read(temp,0,temp.length);
                            bi.close();

                            fo.write(temp);
                        }

                        /*
                        * may also be done by using offset field of fo.write();
                        * */


                        while (fileToReceive.length()<totalLenght && !nc.isClosed){
                            Object data=nc.read();

                            //System.out.println(data);

                            if(data!=null && data instanceof TransmittedFile){
                                TransmittedFile myBytes=(TransmittedFile)data;
                                if(myBytes.getFileId().equals(fileId)){

                                    fo.write(myBytes.getFileData());
                                    System.out.println(fileToReceive.length()+"/"+totalLenght);

                                }

                            }

                        }

                        if(totalLenght<=fileToReceive.length()){
                            System.out.println("file Received");
                            fileToReceive.renameTo(new File(folderPath.getPath()+"//"+
                            fileToReceive.getName()));
                            nc.isReceiving=false;
                            Shutdown();

                        }

                    }



                    }

                }


            }catch (Exception e){
                System.out.println(e);
        }
    }

    public void Shutdown(){
        shutdown=true;
    }
}
