import java.net.*;
import java.io.*;

import tcpclient.*;

public class HTTPAsk {
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }

        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            while (true) {
                Socket socket = serverSocket.accept();

                InputStream input = socket.getInputStream();

                OutputStream output = socket.getOutputStream();

                StringBuilder bldr = new StringBuilder();


                //send a simple http response for debugging
                //output.write("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<h1> Hello world </h1>\r\n<p>".getBytes(StandardCharsets.UTF_8));
                
                //read input from client
                int br = input.read();
                boolean flag = false;
                int flag2 = 0;
                while (true) {
                    //output.write((br));
                    bldr.append((char) br);
                    if (br == '\n' && flag) {
                        flag2++;
                        if (flag2 == 2) {
                            break;
                        }
                    } else if (br == '\r') {
                        flag = true;
                    } else {
                        flag = false;
                        flag2 = 0;
                    }
                    br = input.read();
                }
                System.out.println(bldr.toString());
                //output.write("</p>".getBytes(StandardCharsets.UTF_8));

                boolean goodRequest = true;

                if (!bldr.toString().startsWith("GET")) {
                    goodRequest = false;
                }
                
                String getHeader = bldr.toString().split("\r\n")[0].split(" ")[1];

                if (!getHeader.startsWith("/ask")) {
                    System.out.println("Resource Not Found");
                    output.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                    serverSocket.close();
                    continue;              
                }
                
                if (!bldr.toString().split("\r\n")[0].endsWith("HTTP/1.1")) {
                    goodRequest = false;
                }
                try {
                    URI uri = new URI(getHeader);
                    String[] getArgs = uri.getQuery().split("&");

                    
                    //-> required params
                    String host = null;
                    Integer port = 0;
                    
                    //-> optional params
                    byte[] str = new byte[0];
                    boolean shutdown = false;
                    Integer limit = null;
                    Integer timeout = null;
                    
                    //dismantle the querry into parameters
                    for (String arg : getArgs) {
                        switch (arg.split("=")[0]) {
                            case "hostname":
                                host = arg.split("=")[1];
                                break;
                            case "port":
                                port = Integer.parseInt(arg.split("=")[1]);
                                break;
                            case "string":
                                str = arg.split("=")[1].getBytes();
                                break;
                            case "shutdown":
                                shutdown = Boolean.parseBoolean(arg.split("=")[1]);
                                break;
                            case "limit":
                                limit = Integer.parseInt(arg.split("=")[1]);
                                break;
                            case "timeout":
                                timeout = Integer.parseInt(arg.split("=")[1]);
                                break;
                            default:
                                goodRequest = false;
                                break;
                        }
                    }

                    if (host == null || port == 0) {
                        goodRequest = false;
                    }
                    if(goodRequest){

                        //print some main parameters for debug
                        System.out.println("host: " + host);
                        System.out.println("port: " + port);
                        System.out.println("limit: " + limit);
                        System.out.println("timeout: " + timeout);

                        try{
                            TCPClient client = new TCPClient(shutdown, timeout, limit);
                            byte[] result = client.askServer(host, port, str);

                            System.out.println("Result: " + new String(result));

                            //output.write("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n<h2> Response </h2>\r\n<p>".getBytes(StandardCharsets.UTF_8));
                            output.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                            output.write(result);
                            //output.write("</p></p>".getBytes(StandardCharsets.UTF_8));
                            serverSocket.close();
                        } catch (Exception e) {
                            System.out.println("Not Found Exception: " + e.getMessage());
                            output.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                            serverSocket.close();
                            continue;
                        }
                    } else {
                        System.out.println("Bad Request");
                        output.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                        serverSocket.close();
                        continue;
                    }

                } catch (URISyntaxException e) {
                    System.out.println("Bad Request URIException: " + e.getMessage());
                    output.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                    serverSocket.close();
                    continue;
                }
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
        System.out.println("Finished");
    }
}
