package FileTransfer;

import java.io.File;
import java.io.Serializable;

public class TransmittedFile implements Serializable,Cloneable{
    private String fileId;
    private byte[] fileData;
    private int chunkSize;
    FileItem item;

    public TransmittedFile(String fileId, byte[] fileData){
        this.fileId=fileId;
        this.fileData=fileData;
    }

    public TransmittedFile(String fileId, int chunkSize){
        this.fileId=fileId;
        this.chunkSize=chunkSize;
    }

    public TransmittedFile(FileItem item,String FileId){
        this.fileId=FileId;
        this.item=item;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }

    public synchronized FileItem getItem() {
        return item;
    }

    public synchronized byte[] getFileData() {
        return fileData;
    }

    public synchronized String getFileId() {
        return fileId;
    }

    public synchronized int getChunkSize() {
        return chunkSize;
    }
}
