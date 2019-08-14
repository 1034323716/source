package richinfo.attendance.SMS;

import com.alibaba.druid.util.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 类名称:AESUtil
 * 类描述:AES加密工具类
 * 创建人:hetianhuai
 * 创建时间:2019/6/26 11:06
 * Version 1.0
 */
public class AESUtil {

    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    /**
     * base 64 encode
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes){
        return Base64.encodeBase64String(bytes);
    }

    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception{
        return StringUtils.isEmpty(base64Code) ? null : Base64.decodeBase64(base64Code);
    }

    /**
     * AES加密
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgenInit(kgen, encryptKey.getBytes());

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }

    public static byte[] aesEncryptToBytes(String content, byte[] encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgenInit(kgen, encryptKey);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }

    /**
     * AES加密为base 64 code
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    public static String aesEncrypt(String content, byte[] encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    /**
     * AES解密
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey 解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgenInit(kgen, decryptKey.getBytes());

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes,"utf-8");
    }

    public static String aesDecryptByBytes(byte[] encryptBytes, byte[] decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgenInit(kgen, decryptKey);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);

        return new String(decryptBytes,"utf-8");
    }

    public static String deCodeAES(String password, String key) throws Exception {

        byte[] keybBytes = DigestUtils.md5(key);

        byte[] debase64Bytes = Base64.decodeBase64(password.getBytes());

        return new String(decrypt(debase64Bytes, keybBytes));
    }

    private static byte[] decrypt(byte[] text, byte[] key) throws Exception {

        SecretKeySpec aesKey = new SecretKeySpec(key, "AES");


        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, aesKey);

        return cipher.doFinal(text);

    }

    /**
     * 将base 64 code AES解密
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

    public static String aesDecrypt(String encryptStr, byte[] decryptKey) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

    public static byte[] getAESKey(String encodingAESKey){
        byte[] array = Base64.decodeBase64(encodingAESKey + "=");
        return array;
    }

    /**防止在linux下随机生成key
     * @throws NoSuchAlgorithmException */
    public static void kgenInit(KeyGenerator kgen, byte[] bytes)
        throws NoSuchAlgorithmException {
        //1.防止linux下 随机生成key
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(bytes);
        //2.根据密钥初始化密钥生成器
        kgen.init(128, secureRandom);
    }

    private static byte[] Decrypt(byte[] text, byte[] key) throws Exception {
        SecretKeySpec aesKey = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return cipher.doFinal(text);
    }
    /**
     * 解密手机号专用 aes方法
     * @param password
     * @param key
     * @return
     * @throws Exception
     */
    public static String DeCodeMobileAES(String password, String key) throws Exception{
        byte[] keybBytes = MD5STo16Byte.encrypt2MD5toByte16(key);
        byte[] debase64Bytes = Base64.decodeBase64(password);
        return new String(Decrypt(debase64Bytes, keybBytes));
    }
}
