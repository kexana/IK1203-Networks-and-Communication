package tcpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;;

public class TCPClient {
    private final int BufferByteSize = 1024;
    private Socket clientSocket;

    public TCPClient() {
        
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        
        byte[] dataSendBuffer = new byte[BufferByteSize];
        ByteArrayOutputStream dataReceiveBuffer = new ByteArrayOutputStream();

        try {

            this.clientSocket = new Socket(hostname, port); //open the connection

            clientSocket.getOutputStream().write(toServerBytes, 0, toServerBytes.length); //send the user input

            int fromServerLength;
            while ((fromServerLength = this.clientSocket.getInputStream().read(dataSendBuffer)) != -1) {
                dataReceiveBuffer.write(dataSendBuffer,0,fromServerLength); //add the consecutive reads
            }

            clientSocket.close();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        return dataReceiveBuffer.toByteArray(); //return the buffer as byte array
    }

    public byte[] askServer(String hostname, int port) throws IOException {

        byte[] dataSendBuffer = new byte[BufferByteSize];
        ByteArrayOutputStream dataReceiveBuffer = new ByteArrayOutputStream();

        try {

            this.clientSocket = new Socket(hostname, port);

            int fromServerLength;
            while ((fromServerLength = this.clientSocket.getInputStream().read(dataSendBuffer)) != -1) {
                dataReceiveBuffer.write(dataSendBuffer,0,fromServerLength);
            }

            clientSocket.close();

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        return dataReceiveBuffer.toByteArray();
    }

}
