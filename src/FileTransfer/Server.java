package FileTransfer;

import util.NetworkUtil;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Created by sadiq on 9/19/17.
 */
public class Server {

    private ServerSocket ServSock;
    public int i = 1;
    public static Hashtable<String, NetworkUtil> table;
    public static final int MAX_BUFFER_SIZE=1024*1024*64;
    public static int CURRENT_BUFFER_SIZE=0;
    //public static Hashtable<String,Boolean> fileQueue;

    Server() {
        table = new Hashtable<>();
        //fileQueue=new Hashtable<>();
        try {
            ServSock = new ServerSocket(33333);
            //new WriteThreadServer(table, "Server");

            while (true) {
                Socket clientSock = ServSock.accept();
                NetworkUtil nc=new NetworkUtil(clientSock);
                //clientSock.setSoTimeout(30);


                String id = (String) nc.read();

                if(!table.containsKey(id)) {
                    table.put(id, nc);
                    nc.write("Connected");
                    System.out.println("id "+id+" Connected");
                    //fileQueue.put(id,false);
                    new TransmissionStateServer(nc, id);

                }

                else {
                    nc.write("ID Already Connected");
                    nc.closeConnection();
                }

            }
        }catch(Exception e) {
            System.out.println("Server starts:"+e);
        }
    }

    public static void main(String args[]) {
        Server objServer = new Server();
    }
}

