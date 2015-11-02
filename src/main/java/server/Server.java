package server;

import handlers.RequestHandler;
import handlers.ResponseHandler;
import main.Settings;

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
    private Socket socket;
    private OutputStream os;
    private InputStream is;


    public Server(Socket socket) throws IOException {
        this.socket = socket;
        is = socket.getInputStream();
        os = socket.getOutputStream();
        this.requestHandler = new RequestHandler(is);
        this.responseHandler = new ResponseHandler(os, socket);
        System.out.println(Settings.threadCount.incrementAndGet());
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
        } finally {
            try {
                is.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}