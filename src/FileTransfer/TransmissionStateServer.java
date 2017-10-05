package FileTransfer;

import util.NetworkUtil;

import static FileTransfer.Server.table;

public class TransmissionStateServer implements Runnable{

    NetworkUtil nc;
    Thread thread;
    volatile boolean shutDown=false;
    String id;
    TransmissionStateServer(NetworkUtil nc,String id){
        this.nc=nc;
        this.id=id;
        thread=new Thread(this);
        thread.start();

    }

    @Override
    public void run() {
        try{

            while (!shutDown){

                if(!nc.isReceiving && !nc.isSending) {

                    String token = (String) nc.read();
                    System.out.println(token);

                    if (token.equals("s")) {
                        new ReadFile(nc, id);
                    } else if (token.equals("r")) {
                        nc.isSending=true;
                    }

                }

                if(nc!=null){
                    if( nc.isClosed) {
                        table.remove(id);
                        System.out.println("id " + id + " disconnected");
                        ShutDown();
                    }
                }

                Thread.sleep(1000);

            }



        }catch (Exception e){
            //System.out.println("In TransmissionStateServer");
            table.remove(id);
            System.out.println("id "+ id+" disconnected");
        }
    }

    void ShutDown(){
        shutDown=true;
    }
}
