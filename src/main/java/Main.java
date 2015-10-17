import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        Settings.setDirectory( System.getProperty("user.dir") + "/www");
        System.out.print(Settings.getDirectory());

        ServerSocket ss = new ServerSocket(port);
        while (Settings.isServerOnWork()) {
            Socket s = ss.accept();
            System.out.println("Client accepted");
            new Thread(new Server(s)).start();
        }

    }
}
