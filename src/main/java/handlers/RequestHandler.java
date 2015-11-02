package handlers;

import main.Settings;

import java.io.*;
import java.net.Socket;

/**
 * Created by vitaly on 19.10.15.
 */
public class RequestHandler{

    private InputStream is;

    public RequestHandler(Socket socket) throws IOException {
        this.is = socket.getInputStream();
    }

    public String getRequest() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();

        while (true) {
            String s = br.readLine();
            sb.append(s);
            if (s == null || s.trim().length() == 0) {
                break;
            }
        }
        return sb.toString();
    }

    public String findMethod(String request){
        int index = request.indexOf(' ');
        if (index != -1) {
            return request.substring(0, index);
        }else{
            return "/";
        }
    }

    public String getRoute(String request) throws UnsupportedEncodingException {
        String result = null;

        result = getURI(request);

        result = parseEsc(result);

        result = removeGETArgs(result);

        result = getCanonicURI(result);

        return result;
    }

    private String removeGETArgs(String result) {
        if (result.indexOf('?') != -1){
            result = result.substring(0, result.indexOf('?'));
        }
        return result;
    }

    private String getURI(String request) {
        String result;
        int index = request.indexOf(" ");

        result = request.substring(index + 1);
        result = result.substring(0, result.indexOf(' '));

        result = Settings.getDirectory() + result;

        return result;
    }

    private String getCanonicURI(String result) {
        if (isDirectory(result)){
            if (result.charAt(result.length()-1) != '/') {
                result += "/index.html";
            }else{
                result += "index.html";
            }
        }else{
            if (result.charAt(result.length()-1) == '/') {
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }

    private boolean isDirectory(String result) {
        File file = new File(result);
        return file.isDirectory();
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
