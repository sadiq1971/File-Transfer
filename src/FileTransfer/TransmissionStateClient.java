package FileTransfer;


import util.NetworkUtil;

import java.util.Scanner;

public class TransmissionStateClient implements Runnable {

    NetworkUtil nc;
    Thread thread;
    volatile boolean shutDown=false;
    TransmissionStateClient(NetworkUtil nc){
        this.nc=nc;
        this.thread=new Thread(this);
        thread.start();
    }


    @Override
    public void run() {
        try{

            while (!shutDown) {

                if(!nc.isSending && !nc.isReceiving) {

                    System.out.println("Press s for sending file r for see Incoming file Request");

                    Scanner scanner = new Scanner(System.in);
                    String token = scanner.nextLine().trim();

                    if ( token.equals("s")) {
                        //send file
                        nc.write("s");
                        new SendFileTread(nc);

                    } else if (token.equals("r")) {
                        //receive file
                        nc.write("r");
                        new ReadFileClient(nc);
                    }
                }

                Thread.sleep(1000);


            }
        }catch (Exception e){
            System.out.println("In Transmission Client"+ e);
        }
    }




    void ShutDown(){
        shutDown=true;
    }
}
