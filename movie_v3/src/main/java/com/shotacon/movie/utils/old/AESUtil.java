package com.shotacon.movie.utils.old;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESUtil {
    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();

            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
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

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
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
     * 加密
     *
     * @param content  加密内容
     * @param password 加密密码
     * @return 加密后字符串
     */
    public static String encryptStr(String content, String password) {
        if (password != null && !"".equals(password)) {
            if (content != null && !"".equals(content)) {
                byte[] encryptResult = encrypt(content, password);
                String encryptResultStr = parseByte2HexStr(encryptResult);
                return encryptResultStr;
            }
        }
        return null;
    }

    /**
     * 解密
     *
     * @param encryptResultStr 加密内容
     * @param password         加密密码
     * @return 解密后字符串
     */
    public static String decryptStr(String encryptResultStr, String password) {
        if (encryptResultStr != null && !"".equals(encryptResultStr)) {
            if (password != null && !"".equals(password)) {
                byte[] decryptFrom = parseHexStr2Byte(encryptResultStr);
                byte[] decryptResult = decrypt(decryptFrom, password);
                return new String(decryptResult);
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        String content = "393123";
//        String password = "td7JBp055052";
//        // 加密
//        System.out.println("加密前：" + content);
//        String encryptResultStr = encryptStr(content, password);
//        System.out.println("加密后：" + encryptResultStr);
//
//        System.out.println(decryptStr(encryptResultStr, password));

//        618021421230137 B37F14B21EAE47E9E736286D07E1C0F8    2jf2y0655D80
//        618021421230148 A4256CA4D1AAD3133A762C04A77DC755    9U9bsvP1LMA6
//        618021421230159 069256AEDE927E78E12BA8F52A3A1289    7MP7c23e9UMZ
//        618021421230160 F257F6C320EB3DDEE5A1A8617CB64DF4    6fa2BfK9SS79

        // 解密
        String decryptResult = decryptStr("B37F14B21EAE47E9E736286D07E1C0F8", "2jf2y0655D80");
        System.out.println("解密后：" + new String(decryptResult) + "-- " + "618021421230137");
        decryptResult = decryptStr("A4256CA4D1AAD3133A762C04A77DC755", "9U9bsvP1LMA6");
        System.out.println("解密后：" + new String(decryptResult) + "-- " + "618021421230148");
        decryptResult = decryptStr("069256AEDE927E78E12BA8F52A3A1289", "7MP7c23e9UMZ");
        System.out.println("解密后：" + new String(decryptResult) + "-- " + "618021421230159");
        decryptResult = decryptStr("F257F6C320EB3DDEE5A1A8617CB64DF4", "6fa2BfK9SS79");
        System.out.println("解密后：" + new String(decryptResult) + "-- " + "618021421230160");
//        System.out.println("解密后：" + new String(decryptResult));
    }
}
