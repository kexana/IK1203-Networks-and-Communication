package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private final int BufferByteSize = 1024;
    private Socket clientSocket;

    private Boolean shutdown;
    private Integer timeout;
    private Integer limit;

    
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        byte[] dataSendBuffer = new byte[BufferByteSize];
        ByteArrayOutputStream dataReceiveBuffer = new ByteArrayOutputStream();

        try {

            this.clientSocket = new Socket(hostname, port); //open the connection

            clientSocket.getOutputStream().write(toServerBytes, 0, toServerBytes.length); //send the user input
            
            //if there is a time limit set pass it onto the socket
            if (this.timeout != null) {
                this.clientSocket.setSoTimeout(timeout);
            } else {
                this.clientSocket.setSoTimeout(0);
            }
            
            //if shutdown flag is raised close the out stream
            if (this.shutdown) {
                clientSocket.shutdownOutput();
            }
            int fromServerLength;
            Integer acumilatedData = 0;
            while ((fromServerLength = this.clientSocket.getInputStream().read(dataSendBuffer)) != -1) {

                //if there is a limit keep track of how many bytes received
                if (this.limit != null) {
                    acumilatedData += fromServerLength;
                    if (acumilatedData >= this.limit) {
                        //add to the consecutive reads as much as to reach the set limit
                        dataReceiveBuffer.write(dataSendBuffer, 0, fromServerLength - (acumilatedData - this.limit));
                        break;
                    }
                }
                dataReceiveBuffer.write(dataSendBuffer, 0, fromServerLength); //add the consecutive reads
            }

            clientSocket.close();
        } catch (SocketTimeoutException e) {
            clientSocket.close();
            System.out.println(e.getMessage());
            return dataReceiveBuffer.toByteArray(); //return the cut off buffer as byte array
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        return dataReceiveBuffer.toByteArray(); //return the buffer as byte array
    }
}
