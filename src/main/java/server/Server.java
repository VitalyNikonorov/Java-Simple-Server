package server;

import handlers.RequestHandler;
import handlers.ResponseHandler;
import main.Settings;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * Created by vitaly on 17.10.15.
 * JSS - Java simple server =)
 */
public class Server implements Runnable {

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private String requestMethod;
    private RequestHandler requestHandler;
    private ResponseHandler responseHandler;


    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();
        this.requestHandler = new RequestHandler(socket);
    }

    public void run() {
        try {
            System.out.println("Someone connect to me!");

            String request = requestHandler.getRequest();

            requestMethod = findMethod(request);

            switch (requestMethod){
                case "HEAD":
                case "GET":{
                    String route = getRoute(request);
                    sendResponse(route);
                    break;
                }
                case "POST":{
                    String header = getResponseHeader(null, 0, 405);
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

    private String getRoute(String s) throws UnsupportedEncodingException {
        String result = null;
        int index;

        index = s.indexOf(" ");

        int i;

        for (i = index + " ".length(); i < s.length(); i++) {
            if (s.charAt(i) == ' ') break;
        }

        result = s.substring(index + " ".length(), i);

        result = parseEsc(result);

        if (result.indexOf('.') == -1){
            if (result.charAt(result.length()-1) != '/')
                result += "/";
        }else{
            if (result.charAt(result.length()-1) == '/') {
                result = result.substring(0, result.length() - 1);
                int o = 0;
            }
        }

        if (result.indexOf('?') != -1){
            result = result.substring(0, result.indexOf('?'));
        }

        System.out.println("It asks this:\n" + s);
        System.out.println("\n" + "Work Directory:\n" + result + "\n");

        return result;
    }

    private void sendResponse(String route) throws IOException {

        String path = Settings.getDirectory() + route;
        String headers = null;
        byte[] content = null;

        if (path.charAt(path.length()-1) == '/'){
            path += "index.html";
        }

        try {
            String extension = path.substring(path.lastIndexOf('.')+1);

            content = getContent(extension, path);

            String o = path.substring(path.lastIndexOf('/')+1, path.length()-1);
            System.out.println("its o: " + o);

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

    private String getResponseHeader(String extension, int contentLength, int status){

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

    private byte[] getContent(String extension, String path) throws IOException {
        byte[] result = null;
        FileInputStream fileIS;
        BufferedReader br;

        InputStream inStream = null;
        BufferedInputStream bis = null;

        if (isSubDirectory(new File(Settings.getDirectory()), new File(path))) {

            switch (extension) {
                case "txt":
                case "png":
                case "gif":
                case "jpg":
                case "jpeg":
                case "css":
                case "js":
                case "html":
                case "":
                case "swf": {
                    try {
                        inStream = new FileInputStream(path);
                        bis = new BufferedInputStream(inStream);

                        int numByte = bis.available();
                        result = new byte[numByte];

                        bis.read(result);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (inStream != null)
                            inStream.close();
                        if (bis != null)
                            bis.close();
                    }
                }
            }
        }
        return result;
    }

    public static String parseEsc(String s) throws UnsupportedEncodingException {
        int i = s.indexOf('%');

        while (i != -1){
            byte bs[] = new byte[1];
            bs[0] = (byte) Integer.parseInt(s.substring(i+1, i+3), 16);

            s = s.replaceFirst("%..", new String(bs, "UTF8"));
            i = s.indexOf('%');
        }
        return s;
    }

    private String findMethod(String request){
        String method = null;

        int index = request.indexOf(' ');

        method = request.substring(0, index);

        return method;
    }


    public boolean isSubDirectory(File root, File path)
            throws IOException {
        root = root.getCanonicalFile();
        path = path.getCanonicalFile();

        File parentFile = path;
        while (parentFile != null) {
            if (root.equals(parentFile)) {
                return true;
            }
            parentFile = parentFile.getParentFile();
        }
        return false;
    }

}
