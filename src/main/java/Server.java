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


    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();
    }

    public void run() {
        try {
            System.out.println("Someone connect to me!");

            String request = readInput();

            String route = getRoute(request);

            sendResponse(route);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readInput() throws IOException {
        System.out.println("Someone connect to me!");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s = null;

        StringBuilder sb = new StringBuilder();

        while (true) {
            s = br.readLine();

            sb.append(s);
            if (s == null || s.trim().length() == 0) {
                break;
            }
        }
        return sb.toString();
    }

    private String getRoute(String s) {
        String result = null;
        int index;
        int methodPointer;

        methodPointer = s.indexOf(' ');

        switch (s.substring(0, methodPointer)){
            case "GET":{
                index = s.indexOf("GET ");

                int i;

                for (i = index + "GET ".length(); i < s.length(); i++) {
                    if (s.charAt(i) == ' ') break;
                }

                result = s.substring(index + "GET ".length(), i);

                if (result.indexOf('.') == -1){
                    if (result.charAt(result.length()-1) != '/')
                        result += "/";
                }

                System.out.println("It asks this:\n" + s);
                System.out.println("\n" + "Work Directory:\n" + result + "\n");
                break;
            }

            case "POST":{
                System.out.println("POST METHOD!!!!");

            }

        }

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

            if (content != null) {
                headers = getResponseHeader(extension, content.length, 200);
            }else{
                headers = getResponseHeader(null, 0, 404);
                content = "<!DOCTYPE html><html><head></head><body><h1>Not found</h1></body></html>".getBytes();
            }
        }catch(IOException e){
            e.printStackTrace();
            headers = getResponseHeader(null, 0, 404);
            content = "<!DOCTYPE html><html><head></head><body><h1>Not found</h1></body></html>".getBytes();

        }finally{
            os.write(headers.getBytes());
            os.write(content);
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

            case 404:{
                result = "HTTP/1.1 404 Not Found\r\n" +
                        "Server: JSS\r\n"+
                        "Content-Type: text/html\r\n" +
                        "Connection: close\r\n\r\n";
                break;
            }
        }


        return result;
    }

    private byte[] getContent(String extension, String path) throws IOException {
        byte[] result = null;
        FileInputStream fileIS;
        BufferedReader br;

        path = parseEsc(path);

        switch (extension){
            case "txt":
            case "png":
            case "gif":
            case "jpg":
            case "jpeg":
            case "css":
            case "js":
            case "html":
            case "":
            case "swf":{
                InputStream inStream = null;
                BufferedInputStream bis = null;

                try{
                    inStream = new FileInputStream(path);
                    bis = new BufferedInputStream(inStream);

                    int numByte = bis.available();
                    result = new byte[numByte];

                    bis.read(result);

                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    if(inStream!=null)
                        inStream.close();
                    if(bis!=null)
                        bis.close();
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
}
