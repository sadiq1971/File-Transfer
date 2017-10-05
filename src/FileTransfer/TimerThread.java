package FileTransfer;

import util.NetworkUtil;

import static FileTransfer.SendFileTread.response;

public class TimerThread implements Runnable{

    NetworkUtil nc;
    Thread thread;
    volatile boolean shutdown = false;
    public static int TIME_OUT_SIGNAL=-404;
    static final int TIME_OUT_THRESOLD=30;
    public boolean isTimedOut=false;


    TimerThread(NetworkUtil nc){
        this.nc=nc;
        this.thread =new Thread(this);
        thread.start();

    }


    @Override
    public void run() {
        int i=0;

        while (i<TIME_OUT_THRESOLD && !shutdown) {
            try {
                //System.out.println("count"+ i);
                Thread.sleep(1000);
                i++;

                if(i==TIME_OUT_THRESOLD){
                   // isTimedOut=true;
                    //System.out.println("Timeout");
                    //nc.stopTransmission();
                    //nc.closeConnection();
                    //nc.esTablishConnection();
                    //nc.write(TIME_OUT_SIGNAL);
                    //response=TIME_OUT_SIGNAL;

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void shutDown(){
        shutdown=true;
    }
}
