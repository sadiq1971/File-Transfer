package FileTransfer;

import java.io.Serializable;

public class FileItem implements Serializable {
    String fileName;
    int fileLength;
    String receiverId;
    String message;
    int downloadedSize;
    String senderId;

    public FileItem (String name,int size,String receiverId){
        fileName=name;
        fileLength=size;
        this.receiverId=receiverId;
    }

    FileItem(String message,int downloadedSize){
         this.message=message;
         this.downloadedSize=downloadedSize;

    }

    FileItem (String name,String senderId,int size){
        fileName=name;
        fileLength=size;
        this.senderId=senderId;
    }

    public String getSenderId() {
        return senderId;
    }


    public int getDownloadedSize() {
        return downloadedSize;
    }

    public String getMessage() {
        return message;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileLength() {
        return fileLength;
    }

    public String getReceiverId() {
        return receiverId;
    }
}
