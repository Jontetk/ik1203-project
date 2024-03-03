import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import tcpclient.TCPClient;

public class MyRunnable implements Runnable {
    private Socket sckt;

    public MyRunnable(Socket sckt) {
        this.sckt = sckt;
    }

    public void run() {

        try {

            byte[] recievedBuffer = new byte[1];
            String[] lastStrings = new String[4];
            ByteArrayOutputStream recievedOutputStream = new ByteArrayOutputStream();
            while (!((lastStrings[0] != null && lastStrings[0].equals("\r"))
                    && (lastStrings[1] != null && lastStrings[1].equals("\n"))
                    && (lastStrings[2] != null && lastStrings[2].equals("\r"))
                    && (lastStrings[3] != null && lastStrings[3].equals("\n")))) {
                recievedBuffer = new byte[1];
                sckt.getInputStream().read(recievedBuffer);
                recievedOutputStream.write(recievedBuffer);
                lastStrings[0] = lastStrings[1];
                lastStrings[1] = lastStrings[2];
                lastStrings[2] = lastStrings[3];
                lastStrings[3] = new String(recievedBuffer);
                recievedBuffer = new byte[1];
            }

            String[] headerData = new String(recievedOutputStream.toByteArray()).split("\r\n");
            String[] getData = headerData[0].split(" ");
            String location = getData[1];
            if (getData[0].equals("GET") && getData[2].equals("HTTP/1.1")) {
                try {
                    if (location.length() > 5 && location.substring(0, 5).equals("/ask?")) {
                        String hostname = null;
                        Integer port = null;
                        byte[] string = null;
                        Boolean shutdown = null;
                        Integer limit = null;
                        Integer timeout = null;
                        boolean errored = false;

                        argFor: for (String argument : location.substring(5).split("&")) {
                            switch (argument.split("=")[0]) {
                                case "hostname":
                                    hostname = argument.split("=")[1];
                                    break;
                                case "port":
                                    port = Integer.parseInt(argument.split("=")[1]);
                                    break;
                                case "string":
                                    string = (argument.split("=")[1] + "\n").getBytes();
                                    break;
                                case "shutdown":
                                    shutdown = Boolean.parseBoolean(argument.split("=")[1]);
                                    break;
                                case "limit":
                                    limit = Integer.parseInt(argument.split("=")[1]);
                                    break;
                                case "timeout":
                                    timeout = Integer.parseInt(argument.split("=")[1]);
                                    break;

                                default:
                                    errored = true;
                                    errorInArgs(sckt, 2);
                                    break argFor;

                            }
                        }
                        if (string == null)
                            string = new byte[0];
                        if (shutdown == null)
                            shutdown = false;
                        if (hostname != null && port != null && !errored) {
                            try {

                                TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                                byte[] tcpResult = tcpClient.askServer(hostname, port, string);
                                String resString = "HTTP/1.1 200 OK\r\n\r\n" + new String(tcpResult);
                                byte[] toClient = resString.getBytes();
                                sckt.getOutputStream().write(toClient);
                            } catch (IOException e) {
                                errorInArgs(sckt, 2);
                            }
                        } else if (!errored)
                            errorInArgs(sckt, 2);
                    } else {
                        errorInArgs(sckt, 2);
                    }
                } catch (Exception e) {
                    errorInArgs(sckt, 2);
                }
            } else {
                errorInArgs(sckt, 1);
            }

            sckt.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void errorInArgs(Socket sckt, int errType) throws IOException {
        String resString;
        byte[] toClient;
        switch (errType) {
            case 1:
                resString = "HTTP/1.1 400 Bad Request\r\n\r\n" + "Error, invalid arguments";
                toClient = resString.getBytes();
                sckt.getOutputStream().write(toClient);
                break;
            case 2:
                resString = "HTTP/1.1 404 Not Found\r\n\r\n" + "Error, not found";
                toClient = resString.getBytes();
                sckt.getOutputStream().write(toClient);
                break;
            default:
                break;
        }

    }
}
