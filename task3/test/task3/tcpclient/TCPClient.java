package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private Integer timeout;
    private boolean shutdown;
    private Integer limit;
    public TCPClient() {
        this.timeout = null;
        this.shutdown = false;
        this.limit = null;
    }
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        Socket sckt = new Socket(hostname,port);
        ByteArrayOutputStream recievedOutputStream = new ByteArrayOutputStream();
        sckt.getOutputStream().write(toServerBytes); 
        if(shutdown) {
            sckt.shutdownOutput();
        }
        byte[] recievedBuffer = new byte[32];
        int readBytes = 0;
        int remLimit;
        int actLimit;
    try {
        if (timeout !=null) 
            sckt.setSoTimeout(timeout);
            

        if (limit == null) {
            remLimit = -1;
            readBytes = sckt.getInputStream().read(recievedBuffer);
        }
        else {
            remLimit = limit;
            actLimit = remLimit;
            if(remLimit > recievedBuffer.length)
                actLimit = recievedBuffer.length;
            readBytes = sckt.getInputStream().read(recievedBuffer,0,actLimit);
        }
        
        while (readBytes > 0) {
            recievedOutputStream.write(recievedBuffer,0,readBytes);
            recievedBuffer = new byte[32];
            if (limit ==null)
                readBytes = sckt.getInputStream().read(recievedBuffer);
            else {
                remLimit-=readBytes;
                actLimit = remLimit;
                if(remLimit > recievedBuffer.length)
                    actLimit = recievedBuffer.length;
                readBytes = sckt.getInputStream().read(recievedBuffer,0,actLimit);
            }

            
            
        }
    }
    catch(SocketTimeoutException e) {
        sckt.close();
        return recievedOutputStream.toByteArray();
    }

        sckt.close();

        return recievedOutputStream.toByteArray();

     
    }
    public byte[] askServer(String hostname, int port) throws IOException {
        return askServer(hostname, port, new byte[0]);
    }

}
