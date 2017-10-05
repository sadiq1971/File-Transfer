package FileTransfer;

import util.NetworkUtil;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

import static FileTransfer.Server.CURRENT_BUFFER_SIZE;
import static FileTransfer.Server.MAX_BUFFER_SIZE;
import static FileTransfer.Server.table;

public class ReadFile implements Runnable {


    private Thread thr;
    private NetworkUtil nc;
    private static int MIN_CHUNK_SIZE=10000;
    private static int MAX_CHUNK_SIZE=1000000;
    int byteReceived=0;
    String id;
    String receiverId;

    FileItem item=null;
    File file=null;
    volatile boolean shutDown=false;

    public ReadFile(NetworkUtil nc,String id) {
        this.nc = nc;
        this.thr = new Thread(this);
        this.id=id;
        thr.start();
        System.out.println(id);
    }


    public  String generateRandomChars(int length) {

        String candidateChars="ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars
                    .length())));
        }

        return sb.toString();
    }

    public  int generateInt() {

        Random rand=new Random();

        int randomNum = rand.nextInt((MAX_CHUNK_SIZE - MIN_CHUNK_SIZE) + 1) + MIN_CHUNK_SIZE;

        return Math.abs(randomNum);
    }

    public void run() {
        try {
            while(!shutDown) {

                if(nc.isClosed){
                    //table.remove(nc);
                    //System.out.println("here");
                    if(file!=null && item !=null && byteReceived<item.fileLength){

                        System.out.println("-0");
                        nc.isReceiving=false;
                        CURRENT_BUFFER_SIZE-=file.length();
                        file.delete();
                        ShutDown();
                        continue;
                    }
                }

                nc.isReceiving=true;
                System.out.println("receiving ");

                Object o = nc.read();


                if(o!= null) {

                    if(o instanceof FileItem) {

                        item=(FileItem)o;
                        System.out.println(item.fileName);
                        System.out.println(item.fileLength);

                        /*
                            check maximum size of all buffer and take decision
                        */

                        int cz=CURRENT_BUFFER_SIZE+=item.fileLength;


                        receiverId=item.getReceiverId();
                        System.out.println(receiverId);
                        System.out.println(table.containsKey(receiverId) && cz>MAX_BUFFER_SIZE);

                        System.out.println(cz);
                        System.out.println(MAX_BUFFER_SIZE);

                        //[cheack whether receiver is logged In ]

                        if(table.containsKey(receiverId) && cz<=MAX_BUFFER_SIZE){
                            nc.write("Receiver Found");
                            //generate file ID;

                            String fileId=generateRandomChars(20);
                            //System.out.println(FileId);
                            int chunkSize=generateInt();

                            //send fileId and chunkSize
                            nc.write(new TransmittedFile(fileId,chunkSize));
                            file=new File("//home//sadiq//Desktop//"+item.fileName);
                            //File file=new File(item.fileName);
                            FileOutputStream fo=new FileOutputStream(file);
                            int i=0;


                            System.out.println(item.fileLength);
                            System.out.println(byteReceived);

                            boolean timeout=false;
                            byteReceived=0;

                            while (byteReceived<item.fileLength && !nc.isClosed && !timeout) {

                                //System.out.println("5");
                                Object data = nc.read();
                                //System.out.println("6");

                                if (data != null) {

                                    if(data instanceof Integer){
                                        int signal=(int)data;
                                        System.out.println(signal);
                                        //nc.write(signal);
                                        file.delete();
                                        timeout=true;
                                        CURRENT_BUFFER_SIZE-=item.fileLength;
                                        continue;
                                    }

                                    if (data instanceof TransmittedFile) {
                                        TransmittedFile tempData = (TransmittedFile) data;

                                        if (tempData.getFileId().equals(fileId)) {
                                            byte[] bytes = tempData.getFileData();
                                            fo.write(bytes);
                                            byteReceived=byteReceived+bytes.length;
                                            i++;
                                            nc.write(i);
                                            System.out.println(byteReceived);

                                            if(byteReceived==item.fileLength){
                                                System.out.println("received");
                                                new SendToReceiver(file,fileId,id,receiverId);

                                                nc.isReceiving=false;
                                                ShutDown();
                                                //continue;


                                            }

                                       //     System.out.println("1");
                                        }
                                     //   System.out.println("2");
                                    }

                                   // System.out.println("3");
                                }

                            }
                            //System.out.println("4");

                        }

                        else {
                            if(cz>MAX_BUFFER_SIZE){
                                nc.write("There is Not enough space in Server");
                                CURRENT_BUFFER_SIZE-=item.fileLength;
                            }

                            else {
                                nc.write("Receiver Not Logged In");
                                //can improve here
                            }

                        }

                    }

                }

                //nc.isReceiving=false;
                //file.delete();
                //ShutDown();
                //CURRENT_BUFFER_SIZE-=item.fileLength;

            }
        } catch(Exception e) {
            //System.out.println (e+ "Thread dead");
            nc.isReceiving=false;

        }
        //nc.closeConnection();
        }

    public void ShutDown() {
        shutDown=true;
    }
}



