package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    public TCPClient() {
    }
    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        Socket sckt = new Socket(hostname,port);
        
        ByteArrayOutputStream recievedOutputStream = new ByteArrayOutputStream();
        sckt.getOutputStream().write(toServerBytes); 
        byte[] recievedBuffer = new byte[32];
        int readBytes = sckt.getInputStream().read(recievedBuffer);
        while (readBytes > 0) {
            recievedOutputStream.write(recievedBuffer,0,readBytes);
            recievedBuffer = new byte[32];
            readBytes = sckt.getInputStream().read(recievedBuffer);
        }
        sckt.close();

        return recievedOutputStream.toByteArray();

     
    }
    public byte[] askServer(String hostname, int port) throws IOException {
        return askServer(hostname, port, new byte[0]);
    }

}
