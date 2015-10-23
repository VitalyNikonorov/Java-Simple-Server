package handlers;

import main.Settings;

import java.io.*;

/**
 * Created by vitaly on 19.10.15.
 */
public class FileSystemHandler {


    public InputStream getContent(String extension, String path) throws IOException {

        InputStream inStream = null;

        if (isSubDirectory(new File(Settings.getDirectory()), new File(path))) {

            switch (extension.toLowerCase()) {
                case "txt":
                case "png":
                case "gif":
                case "jpg":
                case "jpeg":
                case "css":
                case "js":
                case "html":
                case "":
                case "zip":
                case "swf": {

                    if (new File(path).exists()) {
                        inStream = new FileInputStream(path);
                    }else{
                        inStream = null;
                    }
                }
            }
        }
        return inStream;
    }

    public boolean isSubDirectory(File root, File path)
            throws IOException {
        root = root.getCanonicalFile();
        path = path.getCanonicalFile();

        File parentFile = path;
        while (parentFile != null) {
            if (root.equals(parentFile)) {
                return true;
            }
            parentFile = parentFile.getParentFile();
        }
        return false;
    }
}