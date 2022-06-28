package com.csdaa.wsn.server.utils;

import java.io.*;

public class SerializeUtil {

    public static byte[] doSerialize(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oo=new ObjectOutputStream(bos);
        oo.writeObject(o);
        byte[] bytes = bos.toByteArray();
        bos.close();
        oo.close();
        return bytes;
    }
    public static Object doDeserialize(byte[]bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object o = null;
        try {
            o = ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        bis.close();
        ois.close();
        return o;
    }
}
