package com.uav.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 */
public final class MD5 {

    /**
     * MD5加密
     * 
     * @param strSrc 需要加密的字符串
     * @return 加密后的32位小写字符串
     */
    public static String encrypt(String strSrc) {
        try {
            char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            byte[] bytes = strSrc.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();
            int j = bytes.length;
            char[] chars = new char[j * 2];
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                chars[k++] = hexChars[b >>> 4 & 0xf];
                chars[k++] = hexChars[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5加密出错！！+" + e);
        }
    }

    /**
     * MD5加密（带盐值）
     * 
     * @param strSrc 需要加密的字符串
     * @param salt 盐值
     * @return 加密后的32位小写字符串
     */
    public static String encryptWithSalt(String strSrc, String salt) {
        return encrypt(strSrc + salt);
    }

    /**
     * 验证MD5加密结果
     * 
     * @param strSrc 原始字符串
     * @param encrypted 加密后的字符串
     * @return 如果匹配返回true，否则返回false
     */
    public static boolean verify(String strSrc, String encrypted) {
        return encrypt(strSrc).equals(encrypted);
    }

    /**
     * 验证MD5加密结果（带盐值）
     * 
     * @param strSrc 原始字符串
     * @param salt 盐值
     * @param encrypted 加密后的字符串
     * @return 如果匹配返回true，否则返回false
     */
    public static boolean verifyWithSalt(String strSrc, String salt, String encrypted) {
        return encryptWithSalt(strSrc, salt).equals(encrypted);
    }
}