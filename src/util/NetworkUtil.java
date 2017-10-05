package util;

import FileTransfer.SendFileTread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkUtil
{
	public boolean isClosed=false;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	public boolean isReceiving=false;
	public boolean isSending=false;

	public NetworkUtil(String s, int port) {
		try {
			this.socket=new Socket(s,port);
			//this.socket.setSoTimeout(30000);
			this.socket.setSoTimeout(5000);
			oos=new ObjectOutputStream(socket.getOutputStream());
			ois=new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.out.println("In NetworkUtil : " + e.toString());
		}
	}

	public NetworkUtil(Socket s) {
		try {
			this.socket = s;
			//this.socket.setSoTimeout(9000);
			oos=new ObjectOutputStream(socket.getOutputStream());
			ois=new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.out.println("In NetworkUtil : " + e.toString());
		}
	}

	public Object read() {
		Object o = null;
		try {
			o=ois.readObject();

		} catch (Exception e) {
			isClosed=true;
			//isReceiving=false;
		    //System.out.println("Reading Error in network : " + e.toString());
		}
		return o;
	}

	public void write(Object o) {
		try {
			oos.writeObject(o);
			oos.flush();
		} catch (IOException e) {
			isClosed=true;
			//isReceiving=false;
			System.out.println("Writing  Error in network : " + e.toString());
		}
	}

	public void closeConnection() {
		try {
			ois.close();
			oos.close();
			isClosed=true;
			//isReceiving=false;
		} catch (Exception e) {
			System.out.println("Closing Error in network : "  + e.toString());
		}
	}

	public void stopTransmission(){

		try {
			oos.flush();
			//oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public void esTablishConnection(){
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		}catch (Exception e){
			System.out.println(e);
		}
	}


	public Object readWithTimeOut(int code){
		Object o = null;
		try {
			o=ois.readObject();
		} catch (Exception e) {

			SendFileTread.response=code;
			write(code);

		}
		return o;
	}

	public Object readForReceive(boolean flage){
		Object o=null;
		try {
			o=ois.readObject();
		} catch (Exception e) {
			//flage=false;
			//System.out.println("ooooooooooooooooo");
			return o;
		}
		return o;
	}

}

