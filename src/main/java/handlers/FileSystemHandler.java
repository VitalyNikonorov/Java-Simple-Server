package handlers;

import main.Settings;

import java.io.*;

/**
 * Created by vitaly on 19.10.15.
 */
public class FileSystemHandler {


    public byte[] getContent(String extension, String path) throws IOException {
        byte[] result = null;

        InputStream inStream = null;
        BufferedInputStream bis = null;

        if (isSubDirectory(new File(Settings.getDirectory()), new File(path))) {

            switch (extension) {
                case "txt":
                case "png":
                case "gif":
                case "jpg":
                case "jpeg":
                case "css":
                case "js":
                case "html":
                case "":
                case "swf": {
                    try {
                        inStream = new FileInputStream(path);
                        bis = new BufferedInputStream(inStream);

                        int numByte = bis.available();
                        result = new byte[numByte];

                        bis.read(result);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (inStream != null)
                            inStream.close();
                        if (bis != null)
                            bis.close();
                    }
                }
            }
        }
        return result;
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