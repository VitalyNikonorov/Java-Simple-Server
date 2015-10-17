/**
 * Created by vitaly on 17.10.15.
 */
public class Settings {
    private static boolean serverOnWork = true;

    public static boolean isServerOnWork() {
        return serverOnWork;
    }

    public static void setServerOnWork(boolean serverOnWork) {
        Settings.serverOnWork = serverOnWork;
    }
}
