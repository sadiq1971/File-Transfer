package FileTransfer;

import util.NetworkUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SendToReceiver implements Runnable{

    String receiverId;
    String senderId;
    String fileId;
    File fileToSend;
    NetworkUtil nc;
    int totalRead;
    static final int RATE=10;
    volatile boolean shutdown = false;
    BufferedInputStream bi;
    Thread thread;


    SendToReceiver(File file,String fileId,String senderId,String receiverId){
        this.fileToSend=file;
        this.fileId=fileId;
        this.senderId=senderId;
        this.receiverId=receiverId;
        totalRead=0;
        thread=new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown){
                Thread.sleep(1000);
                //check id is in online?
                if(!Server.table.containsKey(receiverId)){
                    //Thread.sleep(10000);
                    //System.out.println("");
                    continue;
                }

                System.out.println("here");

                //if id found
                nc=Server.table.get(receiverId);
                //System.out.println("receiver found");

                if(!nc.isSending){
                    continue;
                }


               /* String token="User "+ senderId+ "wants to send you a file named "+"" +
                                fileToSend.getName()+"( "+fileToSend.length()/1024  + " " +
                        "kb). Press y to receive n to reject";*/

                nc.write(new TransmittedFile(
                        new FileItem(fileToSend.getName(),senderId,(int)fileToSend.length()),
                        fileId));

                System.out.println("msg written");

                if(!nc.isClosed){
                    Object fileI=nc.read();
                    if(fileI instanceof FileItem){

                        FileItem fileItem=(FileItem)fileI;

                        if(fileItem.getMessage().equals("y")) {
                            bi=new BufferedInputStream(new FileInputStream(fileToSend));

                            byte []unneccessary=new byte[fileItem.getDownloadedSize()];
                            bi.read(unneccessary,0,fileItem.getDownloadedSize());

                            totalRead=fileItem.getDownloadedSize();

                            while (!nc.isClosed && totalRead < fileToSend.length()) {
                                byte[] temp = new byte[RATE];

                                int remaining = (int) fileToSend.length() - totalRead;

                                if (remaining < temp.length) {
                                    bi.read(temp, 0, remaining);
                                } else {
                                    bi.read(temp, 0, temp.length);
                                }

                                nc.write(new TransmittedFile(fileId, temp.clone()));

                                totalRead = totalRead + temp.length;


                                if(totalRead>=fileToSend.length()){
                                    System.out.println("send successful");
                                    Server.CURRENT_BUFFER_SIZE-=fileToSend.length();
                                    fileToSend.delete();
                                    shutdown=true;

                                }
                            }


                        }
                        else {
                            shutdown=true;
                            fileToSend.delete();

                        }


                    }


                }

                nc.isSending=false;


            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
