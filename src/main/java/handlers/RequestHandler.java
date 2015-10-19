package handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by vitaly on 19.10.15.
 */
public class RequestHandler{

    private Socket socket;
    private InputStream is;

    public RequestHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.is = socket.getInputStream();;
    }

    public String getRequest() throws IOException {
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
}
