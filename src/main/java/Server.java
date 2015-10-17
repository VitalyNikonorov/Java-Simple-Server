
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
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
        String result;
        int index;

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
        return result;
    }

    private void sendResponse(String route) throws IOException {

        String path = Settings.getDirectory() + route;

        if (path.charAt(path.length()-1) == '/'){
            path += "index.html";
        }
        try {

            String headers = null;
            byte[] content = null;

            String extension = path.substring(path.indexOf('.')+1);

            content = getContent(extension, path);

            headers = getResponseHeader(extension, content.length);

            os.write(headers.getBytes());
            os.write(content);
            os.flush();

        }catch(IOException e){
            e.printStackTrace();
        }finally{
            socket.close();
        }
    }

    private String getResponseHeader(String extension, int contentLength){

        String cType = null;

        switch (extension){
            case "png": cType = "image/png";
                break;
            case "gif":
            case "jpg":
            case "jpeg": cType = "image/jpeg";
                break;
            case "html":
            case "": cType = "text/html";
        }

        String result = "HTTP/1.1 200 OK\r\n" +
                "Server: JSS\r\n"+
                "Content-Type: " +cType+ "\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "Connection: close\r\n\r\n";

        return result;
    }

    private byte[] getContent(String extension, String path) throws IOException {
        byte[] result = null;
        FileInputStream fileIS;
        BufferedReader br;

        switch (extension){
            case "png":
            case "gif":
            case "jpg":
            case "jpeg":{
                BufferedImage originalImage = ImageIO.read(new File(path));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write( originalImage, extension, baos );
                baos.flush();
                result = baos.toByteArray();
                break;
            }

            case "html":
            case "": {
                fileIS = new FileInputStream(path);
                br = new BufferedReader(new InputStreamReader(fileIS));

                String strLine;

                StringBuilder sb = new StringBuilder();

                while ((strLine = br.readLine()) != null) {
                    sb.append(strLine);
                }
                result = sb.toString().getBytes();
                break;
            }
        }
        return result;
    }
}
