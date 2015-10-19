package server;

import handlers.RequestHandler;
import handlers.ResponseHandler;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * Created by vitaly on 17.10.15.
 * JSS - Java simple server =)
 */
public class Server implements Runnable {

    private Socket socket;
    private String requestMethod;
    private RequestHandler requestHandler;
    private ResponseHandler responseHandler;
    private OutputStream os;


    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.requestHandler = new RequestHandler(socket);
        this.responseHandler = new ResponseHandler(socket);
        this.os = socket.getOutputStream();
    }

    public void run() {
        try {
            System.out.println("Someone connect to me!");

            String request = requestHandler.getRequest();

            requestMethod = requestHandler.findMethod(request);

            switch (requestMethod) {
                case "HEAD":
                case "GET": {
                    String route = requestHandler.getRoute(request);
                    responseHandler.sendResponse(route, requestMethod);
                    break;
                }
                case "POST": {
                    String header = responseHandler.getResponseHeader(null, 0, 405);
                    os.write(header.getBytes());
                    os.flush();
                    socket.close();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}