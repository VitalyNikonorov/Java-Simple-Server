package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.cli.*;
import server.Server;

public class Main {

    private static Options options = new Options();

    static {
        options.addOption(new Option("r", true, "Root directory"));
        options.addOption(new Option("c", true, "CPU number"));
        options.addOption(new Option("p", true, "Port"));
        options.addOption(new Option("h", true, "Host"));
    }

    public static void main(String[] args) throws IOException, ParseException {



        CommandLineParser parser = new DefaultParser();

        CommandLine commandLine = parser.parse(options, args);

        String directory = commandLine.getOptionValue("r", "/Users/vitaly/Documents/technopark/3/TP_Highload/www");
        String host = commandLine.getOptionValue("h", "0.0.0.0");
        int port = Integer.parseInt(commandLine.getOptionValue("p", "80"));

        Settings.setDirectory(directory);
        System.out.print(Settings.getDirectory());

        ServerSocket ss = new ServerSocket(port);
        while (Settings.isServerOnWork()) {
            Socket s = ss.accept();
            new Thread(new Server(s)).start();
        }

    }
}
