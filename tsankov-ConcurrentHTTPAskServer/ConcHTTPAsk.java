import java.net.*;

import java.io.*;

public class ConcHTTPAsk {
    public static void main( String[] args) {
        if (args.length < 1) {
            System.out.println("no port number provided");
            return;
        }

        Integer port = Integer.parseInt(args[0]);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                Runnable request = new MyRunnable(socket);
                new Thread(request).start();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
        System.out.println("Finished");

        
    }
}

