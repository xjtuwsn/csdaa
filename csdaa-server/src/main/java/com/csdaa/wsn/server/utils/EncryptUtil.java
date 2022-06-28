package com.csdaa.wsn.server.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class EncryptUtil {
    //    private final static String DES = "DES";
//    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
//        // 生成一个可信任的随机数源
//        SecureRandom sr = new SecureRandom();
//        // 从原始密钥数据创建DESKeySpec对象
//        DESKeySpec dks = new DESKeySpec(key);
//        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
//        SecretKey securekey = keyFactory.generateSecret(dks);
//        // Cipher对象实际完成加密操作
//        Cipher cipher = Cipher.getInstance(DES);
//        // 用密钥初始化Cipher对象
//        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
//        return cipher.doFinal(data);
//    }
//    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
//        // 生成一个可信任的随机数源
//        SecureRandom sr = new SecureRandom();
//
//        // 从原始密钥数据创建DESKeySpec对象
//        DESKeySpec dks = new DESKeySpec(key);
//
//        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
//        SecretKey securekey = keyFactory.generateSecret(dks);
//
//        // Cipher对象实际完成解密操作
//        Cipher cipher = Cipher.getInstance(DES);
//
//        // 用密钥初始化Cipher对象
//        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
//
//        return cipher.doFinal(data);
//    }
    private final static String AES = "AES";
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception{
        KeyGenerator kg=KeyGenerator.getInstance(AES);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key);
        kg.init(128, secureRandom);
        SecretKey secretKey = kg.generateKey();
        SecretKey secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        return cipher.doFinal(data);
    }
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception{
        KeyGenerator kg=KeyGenerator.getInstance(AES);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key);
        kg.init(128, secureRandom);
        SecretKey secretKey = kg.generateKey();
        SecretKey secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
        return cipher.doFinal(data);
    }
}
