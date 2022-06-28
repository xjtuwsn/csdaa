package com.csdaa.wsn.server.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpUtil {
    private static int WIN_SIZE=1024;
    public static byte[] getData(HttpServletRequest request) throws IOException{
        int contentLength = request.getContentLength();
        ByteArrayOutputStream os=new ByteArrayOutputStream(contentLength);
        InputStream inputStream = null;

        inputStream = request.getInputStream();
        byte []buf=new byte[WIN_SIZE];
        int n=0;
        while ((n=inputStream.read(buf))!=-1){
            os.write(buf,0,n);
        }
        return os.toByteArray();

    }
}
