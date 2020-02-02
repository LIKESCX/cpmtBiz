package com.cpit.cpmt.biz.utils.validate;

import org.apache.xmlbeans.impl.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;

public class Encrypt {
    public Encrypt() {
    }

    public static String encrypt(String strKey, String v, String strIn) {
        byte[] encrypted = null;

        try {
            SecretKeySpec skeySpec = getKey(strKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(v.getBytes());
            cipher.init(1, skeySpec, iv);
            encrypted = cipher.doFinal(strIn.getBytes("utf-8"));
        } catch (Exception var7) {
            ;
        }

        return new String(Base64.encode(encrypted));
    }

    public static String decrypt(String strKey, String v, String strIn) {
        String originalString = null;

        try {
            SecretKeySpec skeySpec = getKey(strKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(v.getBytes());
            cipher.init(2, skeySpec, iv);
            byte[] encrypted1 = Base64.decode(strIn.getBytes());
            byte[] original = cipher.doFinal(encrypted1);
            originalString = new String(original);
        } catch (Exception var9) {
            ;
        }

        return originalString;
    }

    private static SecretKeySpec getKey(String strKey) throws Exception {
        byte[] arrBTmp = strKey.getBytes();
        byte[] arrB = new byte[16];

        for (int i = 0; i < arrBTmp.length && i < arrB.length; ++i) {
            arrB[i] = arrBTmp[i];
        }

        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");
        return skeySpec;
    }

    public static String hmacMD5(String keyString, String msg) {
        String digest = null;

        try {
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);
            byte[] bytes = mac.doFinal(msg.getBytes("UTF-8"));
            StringBuffer hash = new StringBuffer();

            for (int i = 0; i < bytes.length; ++i) {
                String hex = Integer.toHexString(255 & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }

                hash.append(hex);
            }

            digest = hash.toString().toUpperCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException var9) {
            ;
        }

        return digest;
    }

    public static void main(String[] args) throws SQLException, ParseException {
        String str = "KOfhNgfWsm1/JMlLZhbXgr6ltaE+F7ZLQ6xsSGp1egOBicpQYcHaxQ9BqwH8LlMHLw1GDwNtIkACT30/9z67wy7VeSoVhX2igWa04uT6ExG2Fy6Bty7mVGtHYVFLRvcf";

    }
}
