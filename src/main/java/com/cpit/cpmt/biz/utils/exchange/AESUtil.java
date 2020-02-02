package com.cpit.cpmt.biz.utils.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 优科加密规范
 * 无论是请求数据还是返回的数据，都需要对data的内容进行AES对称加密
 * Created by taoliang on 2019/3/21.
 */
public class AESUtil {

    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";


    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     * @throws
     * @method parseByte2HexStr
     * @since v1.0
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     * @throws
     * @method parseHexStr2Byte
     * @since v1.0
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

//    public static String Encrypt(String str, String key, String iv) {
//        String resultObj = null;
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//            //使用加密模式初始化 密钥
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes("UTF-8")));
//            //按单部分操作加密或解密数据，或者结束一个多部分操作。
//            byte[] encode = cipher.doFinal(str.getBytes("UTF-8"));
//            resultObj = new BASE64Encoder().encode(encode);
//        } catch (Exception e) {
//            logger.error("AESUtil Encrypt1 : ", e);
//        }
//        return resultObj;
//    }

    //加密
    /* str 源字符串
     * key 秘钥
     * iv 向量秘钥
     */
    public static String encrypt(String str, String key, String iv) {
        String resultObj = null;
        try {
            if (key == null) {
                logger.error("AES加密的key为空");
                return null;
            }
            // 判断Key是否为16位
            if (key.length() != 16) {
                logger.error("AES加密的key长度不是16位");
                return null;
            }
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), KEY_ALGORITHM);
            //使用加密模式初始化 密钥
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes("UTF-8")));
            //按单部分操作加密或解密数据，或者结束一个多部分操作。
            byte[] encode = cipher.doFinal(str.getBytes("UTF-8"));
//            resultObj = parseByte2HexStr(encode);
            resultObj = new BASE64Encoder().encode(encode);
//            resultObj = new BASE64Encoder().encode(encode).replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
        } catch (Exception e) {
            logger.error("AES加密错误: ", e);
        }
        String re = resultObj.replaceAll("(\\\\r\\\\n|\\\\r|\\\\n|\\\\n\\\\r)", "");
        re = re.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
        return re;
    }

    // 解密
    /* sSrc 源字符串
     * sKey 秘钥
     * ivStr 向量秘钥
     */
    public static String decrypt(String sSrc, String sKey, String ivStr) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                logger.error("AES解密的key为空");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                logger.error("AES解密的key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            //过滤 \r\n
            sSrc = sSrc.replaceAll("(\\\\r\\\\n|\\\\r|\\\\n|\\\\n\\\\r)", "");
            sSrc = sSrc.replaceAll("\\r\\n", "").replaceAll("\\r", "").replaceAll("\\n", "");
            sSrc = sSrc.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
            //
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
//            byte[] encrypted1 = parseHexStr2Byte(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "UTF-8");
//                 originalString= unEscapeChar(originalString);
                if("null".equals(originalString)) {
                	originalString = null;
                }
                return originalString;
            } catch (Exception e) {
                logger.error("base64解密错误", e);
                return null;
            }
        } catch (Exception ex) {
            logger.error("AES解密错误", ex);
            return null;
        }
    }

    public static void main(String[] args) {
        String json = "{\"OperatorID\":\"565843400\",\"OperatorSecret\":\"575uFm7cbXNlaDQC\"}";
        System.out.println(encrypt(json, "bC8zTWAMuUkDv7fy", "uhhzaTYBxtoYkmR2"));
    }

}
