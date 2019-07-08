package richinfo.attendance.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能描述：该类提供一些常用的加密算法,如md5加密,des加密等
 * 
 * 
 */
public final class EncryptionUtil
{
    /** 日志对象 */
    private static final Logger LOG = LoggerFactory
        .getLogger(EncryptionUtil.class);

    private EncryptionUtil()
    {
    }

    /**
     * 获取md5加密串,字符串转换成字节采用GBK编码. 加密失败将返回空字符串
     * 
     * @param src 待加密的字符串
     * @return 加密过后的字符串
     */
    public static String getMD5ByGBK(String src)
    {
        return getMD5ByEncoding(src, "GBK");
    }

    /**
     * 获取md5加密串,字符串转换成字节采用utf-8编码. 加密失败将返回空字符串
     * 
     * @param src 待加密的字符串
     * @return 加密过后的字符串
     */
    public static String getMD5ByUtf8(String src)
    {
        return getMD5ByEncoding(src, "UTF-8");
    }

    /**
     * 获取md5加密串 加密失败将返回空字符串
     * 
     * @param src 待加密的字符串
     * @param encoding 编码
     * @return 加密过后的字符串
     */
    private static String getMD5ByEncoding(String src, String encoding)
    {
        String md5 = "";
        byte[] rBytes = new byte[0];
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = src.getBytes(encoding);
            rBytes = md.digest(bytes);
            md5 = byte2Hexs(rBytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOG.error("Failed encryp md5.", e);
        }
        catch (UnsupportedEncodingException e)
        {
            LOG.error("Failed encryp md5.", e);
        }
        catch (Exception e)
        {
            LOG.error("Failed encryp md5.", e);
        }
        return md5;
    }

    /**
     * HAMC-MD5 加密,字符串转换为字节采用GBK编码.
     * 
     * @param key 密钥
     * @param src 待加密的字符串
     * @return 加密后的字符串
     */
    public static String getHamcMD5(String key, String src)
    {
        String hmd5 = "";
        byte[] hmd5s = new byte[0];
        try
        {
            byte[] keyBytes = key.getBytes("GBK");
            byte[] dataBytes = src.getBytes("GBK");
            hmd5s = getHmacMd5Bytes(keyBytes, dataBytes);
            hmd5 = byte2Hexs(hmd5s);
        }
        catch (Exception e)
        {
            LOG.error("Failed encryp HAMC-MD5.", e);
        }
        return hmd5;
    }

    /**
     * 将待加密数据data，通过密钥key，使用hmac-md5算法进行加密，然后返回加密结果。 参照rfc2104 HMAC算法介绍实现。
     * @param key 密钥
     * @param data 待加密数据
     * @return 加密结果
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getHmacMd5Bytes(byte[] key, byte[] data)
        throws NoSuchAlgorithmException
    {
        /*
         * HmacMd5 calculation formula: H(K XOR opad, H(K XOR ipad, text))
         * HmacMd5 计算公式：H(K XOR opad, H(K XOR ipad, text))
         * H代表hash算法，本类中使用MD5算法，K代表密钥，text代表要加密的数据 ipad为0x36，opad为0x5C。
         */
        int length = 64;
        byte[] ipad = new byte[length];
        byte[] opad = new byte[length];
        for (int i = 0; i < 64; i++)
        {
            ipad[i] = 0x36;
            opad[i] = 0x5C;
        }
        byte[] actualKey = key; // Actual key.
        byte[] keyArr = new byte[length]; // Key bytes of 64 bytes length
        /*
         * If key's length is longer than 64,then use hash to digest it and use
         * the result as actual key. 如果密钥长度，大于64字节，就使用哈希算法，计算其摘要，作为真正的密钥。
         */
        if (key.length > length)
        {
            actualKey = digest(key, "MD5");
        }
        for (int i = 0; i < actualKey.length; i++)
        {
            keyArr[i] = actualKey[i];
        }

        /*
         * append zeros to K 如果密钥长度不足64字节，就使用0x00补齐到64字节。
         */
        if (actualKey.length < length)
        {
            for (int i = actualKey.length; i < keyArr.length; i++)
            {
                keyArr[i] = 0x00;
            }
        }

        /*
         * calc K XOR ipad 使用密钥和ipad进行异或运算。
         */
        byte[] kIpadXorResult = new byte[length];
        for (int i = 0; i < length; i++)
        {
            kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
        }

        /*
         * append "text" to the end of "K XOR ipad" 将待加密数据追加到K XOR ipad计算结果后面。
         */
        byte[] firstAppendResult = new byte[kIpadXorResult.length + data.length];
        for (int i = 0; i < kIpadXorResult.length; i++)
        {
            firstAppendResult[i] = kIpadXorResult[i];
        }
        for (int i = 0; i < data.length; i++)
        {
            firstAppendResult[i + keyArr.length] = data[i];
        }

        /*
         * calc H(K XOR ipad, text) 使用哈希算法计算上面结果的摘要。
         */
        byte[] firstHashResult = digest(firstAppendResult, "MD5");

        /*
         * calc K XOR opad 使用密钥和opad进行异或运算。
         */
        byte[] kOpadXorResult = new byte[length];
        for (int i = 0; i < length; i++)
        {
            kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
        }

        /*
         * append "H(K XOR ipad, text)" to the end of "K XOR opad" 将H(K XOR
         * ipad, text)结果追加到K XOR opad结果后面
         */
        byte[] secondAppendResult = new byte[kOpadXorResult.length
            + firstHashResult.length];
        for (int i = 0; i < kOpadXorResult.length; i++)
        {
            secondAppendResult[i] = kOpadXorResult[i];
        }
        for (int i = 0; i < firstHashResult.length; i++)
        {
            secondAppendResult[i + keyArr.length] = firstHashResult[i];
        }

        /*
         * H(K XOR opad, H(K XOR ipad, text)) 对上面的数据进行哈希运算。
         */
        byte[] hmacMd5Bytes = digest(secondAppendResult, "MD5");
        return hmacMd5Bytes;
    }

    /**
     * 计算参数的md5信息
     * @param str 待处理的字节数组
     * @return md5摘要信息
     * @throws NoSuchAlgorithmException
     */
    private static byte[] digest(byte[] str, String al)
        throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(al);
        md.update(str);
        return md.digest();
    }

    /**
     * 将字节数组转换成16进制字符串
     * 
     * @param bytes 待转换的字节数组
     * @return 16进制的字符串
     */
    public static String byte2Hexs(byte[] bytes)
    {
        StringBuilder buff = new StringBuilder();
        for (int index = 0; index < bytes.length; index++)
        {
            buff.append(byte2Hex(bytes[index]));
        }
        return buff.toString();
    }

    /**
     * 将字节转换成16进制字符串.
     * 
     * @param b 待转换的字节
     * @return 转换后的16进制字符串
     */
    private static String byte2Hex(byte b)
    {
        String hex = "";
        if (b > 0)
        {
            hex = Integer.toHexString(b);
        }
        else
        {
            hex = Integer.toHexString(b & 0xFF);
        }
        if (hex.length() == 1)
        {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 使用sha-1算法签名字符串
     * @param src
     * @return
     */
    public static String getSha1(String src)
    {
        try
        {
            byte[] bytes = digest(src.getBytes("UTF-8"), "sha-1");
            return byte2Hexs(bytes);
        }
        catch (Exception e)
        {
            LOG.error("sha1 error.", e);
        }
        return null;
    }    
}
