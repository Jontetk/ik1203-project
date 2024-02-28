import java.net.*;

import java.io.*;
public class ConcHTTPAsk {
    public static void main( String[] args) throws IOException{
    ServerSocket servsckt = new ServerSocket(Integer.valueOf(args[0]));  
    while(true) {
        Runnable currClient = new MyRunnable(servsckt.accept());
        new Thread(currClient).start();
    }
}
    


}
