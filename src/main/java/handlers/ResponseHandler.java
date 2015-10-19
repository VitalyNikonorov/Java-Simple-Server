package handlers;

import main.Settings;
import server.Generator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

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
        try {
            if (requestMethod.equals("POST")){
                headers = getResponseHeader(null, 0, 405);
            }else {
                String path = Settings.getDirectory() + route;
                if (path.charAt(path.length() - 1) == '/') {
                    path += "index.html";
                }

                String extension = path.substring(path.lastIndexOf('.') + 1);
                prepareResponse(extension, path);
            }
        }catch(IOException e){
            e.printStackTrace();
            headers = getResponseHeader(null, 0, 404);
            content = Generator.generatePage("Not Found").getBytes();

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
            if(isDirectory(path)) {
                headers = getResponseHeader(null, 0, 403);
                content = Generator.generatePage("Forbidden").getBytes();
            }else{
                headers = getResponseHeader(null, 0, 404);
                content = Generator.generatePage("Not Found").getBytes();
            }
        }
    }

    private boolean isDirectory(String path) {
        return path.substring(path.lastIndexOf('/')+1, path.length()).equals("index.html");
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

                result = Generator.generateHeaders(status, "OK" ,cType, contentLength);
            }
            break;

            case 403:{
                result = Generator.generateHeaders(status, "Forbidden", "text/html", null);
                break;
            }

            case 404:{
                result = Generator.generateHeaders(status, "Not Found", "text/html", null);
                break;
            }

            case 405:
                result = Generator.generateHeaders(status, "Method Not Allowed", "text/html", null);
                break;
        }
        return result;
    }

}
