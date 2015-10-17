
import java.io.*;
import java.net.Socket;
import java.util.Set;

/**
 * Created by vitaly on 17.10.15.
 * JSS - Java simple server =)
 */
public class Server implements Runnable {

    private Socket socket;
    private InputStream is;
    private OutputStream os;
    String[] params;
    String route;


    public Server(Socket socket) throws IOException {
        this.socket = socket;
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();
        String[] params;

    }

    public void run() {
        try {
            System.out.println("Someone connect to me!");
            readInput();
            writeOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readInput() throws IOException {
        System.out.println("Someone connect to me!");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s = null;
        int index;
        StringBuilder sb = new StringBuilder();
        while (true) {
            s = br.readLine();

            index = s.indexOf("GET");

            if (index != -1) {
                int i;
                for (i = index + 4; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') break;
                }
                route = s.substring(index + 4, i);
            }

            sb.append(s);
            System.out.println("Try to read");
            if (s == null || s.trim().length() == 0) {
                break;
            }
        }
        System.out.println("It asks this:\n" + sb.toString());
    }

    private void writeOutput() throws IOException {
        FileInputStream fileIS;
        BufferedReader br;
        String path = Settings.getDirectory() + route;

        if (path.charAt(path.length()-1) == '/'){
            path += "index.html";
        }
        try {
            String response;

            fileIS = new FileInputStream(path);
            br = new BufferedReader(new InputStreamReader(fileIS));

            String strLine;

            StringBuilder sb = new StringBuilder();

            while((strLine = br.readLine())!= null)
            {
                sb.append(strLine);
            }

            response = sb.toString();

                String result = "HTTP/1.1 200 OK\r\n" +
                        "Server: JSS\r\n"+
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + response.length() + "\r\n" +
                        "Connection: close\r\n\r\n";

                result += response;
                os.write(result.getBytes());
                os.flush();

                socket.close();

            }catch(IOException e){
                e.printStackTrace();
            }finally{
                try {
                    socket.close();
                } catch (Throwable t) {
                    /*do nothing*/
                }
            }
        }
    }

