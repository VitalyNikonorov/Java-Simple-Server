package handlers;

import main.Settings;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * Created by vitaly on 19.10.15.
 */
public class ResponseHandler {

    private Socket socket;
    private OutputStream os;
    private String headers = null;
    private byte[] content = null;
    private FileSystemHandler fileSystem;

    public ResponseHandler(Socket socket) throws IOException {
        this.fileSystem = new FileSystemHandler();
        this.os = socket.getOutputStream();
        this.socket = socket;
    }

    public void sendResponse(String route, String requestMethod) throws IOException {

        String path = Settings.getDirectory() + route;

        try {
            if (path.charAt(path.length()-1) == '/'){
                path += "index.html";
            }

            String extension = path.substring(path.lastIndexOf('.')+1);

            prepareResponse(extension, path);

        }catch(IOException e){
            e.printStackTrace();
            headers = getResponseHeader(null, 0, 404);
            content = "<!DOCTYPE html><html><head></head><body><h1>Not found</h1></body></html>".getBytes();

        }finally{
            os.write(headers.getBytes());
            if (requestMethod.equals("GET")) {
                os.write(content);
            }
            os.flush();
            socket.close();
        }
    }

    private void prepareResponse(String extension, String path) throws IOException {
        content = fileSystem.getContent(extension, path);

        if (content != null) {
            headers = getResponseHeader(extension, content.length, 200);
        }else{
            if( path.substring(path.lastIndexOf('/')+1, path.length()).equals("index.html")) {
                headers = getResponseHeader(null, 0, 403);
                content = "<!DOCTYPE html><html><head></head><body><h1>Forbidden</h1></body></html>".getBytes();
            }else{
                headers = getResponseHeader(null, 0, 404);
                content = "<!DOCTYPE html><html><head></head><body><h1>Not found</h1></body></html>".getBytes();
            }
        }

    }


    public String getResponseHeader(String extension, int contentLength, int status){

        String result = null;
        switch (status){

            case 200:{
                String cType = null;
                switch (extension){
                    case "png": cType = "image/png";
                        break;
                    case "gif": cType = "image/gif";
                        break;
                    case "jpg":
                    case "jpeg": cType = "image/jpeg";
                        break;
                    case "html":
                    case "": cType = "text/html";
                        break;
                    case "js": cType = "text/javascript";
                        break;
                    case "css": cType = "text/css";
                        break;
                    case "swf": cType = "application/x-shockwave-flash";
                        break;
                    default: cType = "text/html";
                        break;
                }

                result = "HTTP/1.1 "+status+" OK\r\n" +
                        "Date: "+ new Date()+ "\r\n"+
                        "Server: JSS\r\n"+
                        "Content-Type: " +cType+ "\r\n" +
                        "Content-Length: " + contentLength + "\r\n" +
                        "Connection: close\r\n\r\n";
            }
            break;

            case 403:{
                result = "HTTP/1.1 403 Forbidden\r\n" +
                        "Server: JSS\r\n"+
                        "Content-Type: text/html\r\n" +
                        "Connection: close\r\n\r\n";
                break;
            }

            case 404:{
                result = "HTTP/1.1 404 Not Found\r\n" +
                        "Server: JSS\r\n"+
                        "Content-Type: text/html\r\n" +
                        "Connection: close\r\n\r\n";
                break;
            }

            case 405:
                result = "HTTP/1.1 405 Method Not Allowed \r\n" +
                        "Server: JSS\r\n"+
                        "Content-Type: text/html\r\n" +
                        "Connection: close\r\n\r\n";
                break;
        }
        return result;
    }

}
