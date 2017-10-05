package FileTransfer;

import tcpobject.Data;

import java.io.Serializable;

public class Signal implements Serializable {
    int data;

    Signal(int data){
        this.data=data;
    }

    public int getData() {
        return data;
    }
}
