package com.coderman.club.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * @author zhangyukang
 */
public class DesUtil {


    private static final String PASSWORD_CRYPY_KEY = "__Jdlog_";

    private static final String DES = "des";


    /**
     * 加密
     *
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {

        SecureRandom sr = new SecureRandom();

        DESKeySpec dks = new DESKeySpec(key);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(DES);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

        return cipher.doFinal(src);
    }


    /**
     * 解密
     *
     * @param src 原密码
     * @param key 秘钥
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {

        SecureRandom sr = new SecureRandom();

        DESKeySpec dks = new DESKeySpec(key);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(DES);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

        return cipher.doFinal(src);
    }


    /**
     * 密码解密
     *
     * @return
     */
    public static String decrypt(String data) {

        try {
            return new String(decrypt(hex2byte(data.getBytes()), PASSWORD_CRYPY_KEY.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 密码解密
     *
     * @return
     */
    public static String decrypt(String data, String crypyKey) {

        try {
            return new String(decrypt(hex2byte(data.getBytes()), crypyKey.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 密码解密
     *
     * @return
     */
    public static String encrypt(String password) {

        try {
            return byte2hex(encrypt(password.getBytes(), PASSWORD_CRYPY_KEY.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 密码解密
     *
     * @return
     */
    public static String encrypt(String password, String crypyKey) {

        try {
            return byte2hex(encrypt(password.getBytes(), crypyKey.getBytes()));
        } catch (Exception e) {
            return null;
        }
    }


    private static byte[] hex2byte(byte[] b) {

        if (b.length % 2 != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }

        byte[] b2 = new byte[b.length / 2];

        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }

        return b2;
    }


    private static String byte2hex(byte[] b) {

        StringBuilder hs = new StringBuilder();
        String stmp = "";

        for (byte value : b) {

            stmp = (Integer.toHexString(value & 0XFF));

            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }

        }

        return hs.toString().toUpperCase();
    }
}
