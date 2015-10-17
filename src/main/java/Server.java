
import java.io.*;
import java.net.Socket;

/**
 * Created by vitaly on 17.10.15.
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

    public void run(){
        while (Settings.isServerOnWork()) {
            try {
                readInput();
                writeOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void readInput() throws IOException {
        System.out.println("Someone connect to me!");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s = null;
        while(true) {
            s = br.readLine();
            if(s == null || s.trim().length() == 0) {
                break;
            }
        }
        System.out.println("It asks this:\n" + s);
    }

    private void writeOutput(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line).append("\n");
            System.out.println("I get this data: \n" + sb.toString());

            String response = "<html><head><body><h1>It works!</h1></body></head></html>";

            String result = "HTTP/1.1 200 OK\r\n" +
                    "Server: YarServer/2009-09-09\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + response.length() + "\r\n" +
                    "Connection: close\r\n\r\n";

            result += response;
            os.write(result.getBytes());
            os.flush();

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (Throwable t) {
                    /*do nothing*/
            }
        }
    }
}
