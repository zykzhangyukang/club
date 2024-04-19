package com.coderman.club.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author ：zhangyukang
 * @date ：2024/04/19 15:33
 */
public class WechatUtil {

    /**
     * 微信公众号上的token一致，是服务器令牌（token），这里写什么。服务器就填什么
     */
    private static final String TOKEN = "cdjaCAIXNXCsnCRTv";

    /**
     * 校验签名
     *
     * @param signature 签名
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return 布尔值
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String checktext = null;
        if (null != signature) {
            //对ToKen,timestamp,nonce 按字典排序
            String[] paramArr = new String[]{TOKEN, timestamp, nonce};
            Arrays.sort(paramArr);
            //将排序后的结果拼成一个字符串
            String content = paramArr[0].concat(paramArr[1]).concat(paramArr[2]);

            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                //对接后的字符串进行sha1加密
                byte[] digest = md.digest(content.getBytes());
                checktext = byteToStr(digest);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        //将加密后的字符串与signature进行对比
        return checktext != null && checktext.equals(signature.toUpperCase());
    }

    /**
     * 将字节数组转化我16进制字符串
     *
     * @param byteArrays 字符数组
     * @return 字符串
     */
    private static String byteToStr(byte[] byteArrays) {
        StringBuilder str = new StringBuilder();
        for (byte byteArray : byteArrays) {
            str.append(byteToHexStr(byteArray));
        }
        return str.toString();
    }

    /**
     * 将字节转化为十六进制字符串
     *
     * @param myByte 字节
     * @return 字符串
     */
    private static String byteToHexStr(byte myByte) {
        char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tampArr = new char[2];
        tampArr[0] = digit[(myByte >>> 4) & 0X0F];
        tampArr[1] = digit[myByte & 0X0F];
        return new String(tampArr);
    }
}
