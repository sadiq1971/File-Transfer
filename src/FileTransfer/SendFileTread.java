package FileTransfer;

import util.NetworkUtil;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import static FileTransfer.TimerThread.TIME_OUT_SIGNAL;

public class SendFileTread implements Runnable{

    private Thread thr;
    private NetworkUtil nc;
    public static int response;
    volatile boolean shutDown=false;


    public SendFileTread(NetworkUtil nc) {
        this.nc = nc;
        this.thr = new Thread(this);
        thr.start();
    }


    public void run() {
        try {


            while(!shutDown) {

                nc.isSending=true;
                Scanner sc=new Scanner(System.in);

                System.out.printf("Give Receiver Id: ");


                String ReceiverId=sc.nextLine();


                File file=null;
                JFileChooser fileChooser = new JFileChooser();

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();

                }

                if(file==null){
                    System.out.println("You didn't select any file");
                    continue;
                }


                FileItem fileItem=new FileItem(file.getName(),(int)file.length(),ReceiverId);
                nc.write(fileItem);

                String result=(String) nc.read();
                System.out.println(result);

                if(result.equals("Receiver Found")){
                    Object o=nc.read();

                    if(o!=null){
                        if(o instanceof TransmittedFile){
                            TransmittedFile t1=(TransmittedFile)o;

                            FileInputStream fis= new FileInputStream(file);
                            BufferedInputStream bi=new BufferedInputStream(fis);

                            //byte []temp=new byte[t1.getChunkSize()];

                            int totalRead=0;
                            int remaining=(int)file.length();
                            System.out.println(remaining);


                            //File test=new File(file.getName());
                            //ileOutputStream fout=new FileOutputStream(test);


                            int j=0;
                            response=0;



                            while (totalRead<file.length() && response==j){

                                byte [] temp =new byte[t1.getChunkSize()];

                                //should be here
                                //TimerThread timeout=new TimerThread(nc);

                                if((remaining<t1.getChunkSize())) {
                                    byte []temp1 =new byte[remaining];
                                    bi.read(temp1, 0, remaining);
                                    nc.write(new TransmittedFile(t1.getFileId(),temp1.clone()));

                                }

                                else{

                                    bi.read(temp,0,t1.getChunkSize());
                                    nc.write(new TransmittedFile(t1.getFileId(),temp.clone()));
                                }

                                //TimerThread timeout=new TimerThread(nc);


                                //need not check this
                                if(!nc.isClosed) {
                                    Object ob=nc.readWithTimeOut(TIME_OUT_SIGNAL);
                                    if(ob instanceof Integer )
                                    response = (int) ob;
                                }

                                //timeout.shutDown();

                                //System.out.println(response);

                                if(response==TIME_OUT_SIGNAL){
                                    System.out.println("Time Out");
                                    nc.isSending=false;
                                    ShutDown();
                                    continue;
                                }


                                System.out.println("part "+response+ " has been sent");

                                //fout.write(temp);
                                totalRead=totalRead+temp.length;
                                remaining=(int)file.length()-totalRead;
                                System.out.println("sending:"+((totalRead*100)/file.length())+" percent");
                                j++;

                            }


                            if(response==j) {
                                System.out.println(response);
                                System.out.println("Successful");
                                nc.isSending=false;
                                ShutDown();
                            }

                        }
                    }
                }
                else {

                    nc.isSending=false;
                    ShutDown();
                }
            }
        } catch(Exception e) {
            System.out.println (e);
        }
       // nc.closeConnection();
    }

    public void ShutDown(){
        shutDown=true;
    }
}


