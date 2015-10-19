package server;

import java.util.Date;

/**
 * Created by vitaly on 19.10.15.
 */
public class Generator {

    public static String generatePage(String message){
        String result = "<!DOCTYPE html><html><head></head><body><h1>"+message+"</h1></body></html>";
        return result;
    }

    public static String generateHeaders(int status, String caption,String cType, Integer contentLength){

        StringBuilder sb = new StringBuilder();



            sb.append("HTTP/1.1 " + status + " " + caption + "\r\n")
                    .append("Date: "+ new Date()+ "\r\n")
                    .append("Server: JSS\r\n")
                    .append("Content-Type: " + cType + "\r\n");

        if (contentLength != null){
            sb.append("Content-Length: " + contentLength + "\r\n");
        }
        sb.append("Connection: close\r\n\r\n");

        return sb.toString();
    }
}
