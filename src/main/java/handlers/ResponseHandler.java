package handlers;

import org.apache.commons.io.IOUtils;
import server.Generator;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created by vitaly on 19.10.15.
 */
public class ResponseHandler {

    private Socket socket;
    private OutputStream os;
    private String headers = null;
    private BufferedInputStream content = null;
    private byte[] content400 = null;
    private FileSystemHandler fileSystem;

    public ResponseHandler(Socket socket) throws IOException {
        this.fileSystem = new FileSystemHandler();
        this.os = socket.getOutputStream();
        this.socket = socket;
    }

    public void sendResponse(String path, String requestMethod) throws IOException {
        try {
            if (requestMethod.equals("POST")){
                headers = getResponseHeader(null, 0, 405);
            }else {

                String extension = path.substring(path.lastIndexOf('.') + 1);
                prepareResponse(extension, path);
            }
        }catch(IOException e){
            e.printStackTrace();
            headers = getResponseHeader(null, 0, 404);
            content400 = Generator.generatePage("Not Found").getBytes();

        }finally{
            os.write(headers.getBytes());

            if (requestMethod.equals("GET")) {
                if (content400 == null) {
                    IOUtils.copyLarge(content, os);
                }else{
                    os.write(content400);
                    os.flush();
                }
            }

            if (content != null){
                content.close();
            }

            os.flush();
            socket.close();
        }

    }

    private void prepareResponse(String extension, String path) throws IOException {

        File file = new File(path);

        content = fileSystem.getContent(extension, path);
        if (content != null) {
            headers = getResponseHeader(extension, file.length(), 200);
        }else{
            if(isDirectory(path)) {
                headers = getResponseHeader(null, 0, 403);
                content400 = Generator.generatePage("Forbidden").getBytes();
            }else{
                headers = getResponseHeader(null, 0, 404);
                content400 = Generator.generatePage("Not Found").getBytes();
            }
        }
    }

    private boolean isDirectory(String path) {
        return path.substring(path.lastIndexOf('/')+1, path.length()).equals("index.html");
    }


    public String getResponseHeader(String extension, long contentLength, int status){

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

                result = Generator.generateHeaders(status, "OK" ,cType, (long) contentLength);
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
