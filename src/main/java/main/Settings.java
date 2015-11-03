package main;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vitaly on 17.10.15.
 */
public class Settings {
    private static boolean serverOnWork = true;
    private static String directory = "";

    public static boolean isServerOnWork() {
        return serverOnWork;
    }

    public static void setServerOnWork(boolean serverOnWork) {
        Settings.serverOnWork = serverOnWork;
    }

    public static String getDirectory() {
        return directory;
    }

    //public static AtomicInteger threadCount;

    public static void setDirectory(String directory) {
        Settings.directory = directory;
    }
}
