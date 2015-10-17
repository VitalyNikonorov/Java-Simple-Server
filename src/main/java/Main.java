import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        ServerSocket ss = new ServerSocket(port);
        while (true) {
            Socket s = ss.accept();
            System.out.println("Client accepted");
            new Thread(new Server(s)).start();
        }

    }
}
