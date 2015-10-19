package server;

import handlers.RequestHandler;
import handlers.ResponseHandler;

import java.io.*;
import java.net.Socket;

/**
 * Created by vitaly on 17.10.15.
 * JSS - Java simple server =)
 */
public class Server implements Runnable {

    private String requestMethod;
    private RequestHandler requestHandler;
    private ResponseHandler responseHandler;


    public Server(Socket socket) throws IOException {
        this.requestHandler = new RequestHandler(socket);
        this.responseHandler = new ResponseHandler(socket);
    }

    public void run() {
        try {
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
                    responseHandler.sendResponse(null, requestMethod);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}