package com.csdaa.wsn.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class SHAutil {
    public static String getFileSHA(File file){
        String str="";
        try {
            str=getHash(file,"SHA-256");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }
    public static String getSHA(String s){
        String str="";
        try {
            str=getHash(s,"SHA-256");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }
    private static String getHash(String s,String hashType) throws Exception{
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        byte[] decode = s.getBytes();
        md5.update(decode,0,decode.length);
        return toHexString(md5.digest());
    }
    private static String getHash(File file, String hashType) throws Exception {
        InputStream fis = new FileInputStream(file);
        byte []buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        for (int numRead = 0; (numRead = fis.read(buffer)) > 0; ) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }
    private static String toHexString(byte []b) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            String hex=Integer.toHexString(aB & 0xFF);
            if(hex.length()==1) hex="0"+hex;
            sb.append(hex);
        }
        return sb.toString();
    }
}
