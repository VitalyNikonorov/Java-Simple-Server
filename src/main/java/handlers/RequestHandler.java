package handlers;

import java.io.*;
import java.net.Socket;

/**
 * Created by vitaly on 19.10.15.
 */
public class RequestHandler{

    private InputStream is;
    String result;

    public RequestHandler(Socket socket) throws IOException {
        this.is = socket.getInputStream();;
    }

    public String getRequest() throws IOException {
        System.out.println("Someone connect to me\n");
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
        result = sb.toString();
        System.out.print("with this request: \n" + result);
        return result;
    }

    public String findMethod(String request){
        String method = null;

        int index = request.indexOf(' ');

        method = request.substring(0, index);

        return method;
    }

    public String getRoute(String request) throws UnsupportedEncodingException {
        String result = null;
        int index;

        index = request.indexOf(" ");

        int i;

        for (i = index + " ".length(); i < request.length(); i++) {
            if (request.charAt(i) == ' ') break;
        }

        result = request.substring(index + " ".length(), i);

        result = parseEsc(result);

        if (result.indexOf('.') == -1){
            if (result.charAt(result.length()-1) != '/')
                result += "/";
        }else{
            if (result.charAt(result.length()-1) == '/') {
                result = result.substring(0, result.length() - 1);
            }
        }

        if (result.indexOf('?') != -1){
            result = result.substring(0, result.indexOf('?'));
        }

        System.out.println("It asks this:\n" + request);
        System.out.println("\nWork Directory:\n" + result + "\n");

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
