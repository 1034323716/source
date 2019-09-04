package richinfo.attendance.util;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class AesUtils {

    public static String encrypt(String input, String key) {
        byte[] crypted = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(),"AES");
            Cipher cipher  = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            crypted = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            System.out.println(e);
        }
        return new String(Base64.encodeBase64(crypted));
    }

    public static String decrypt(String input, String key) {
        byte[] output = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(),"AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
            output = cipher.doFinal(Base64.decodeBase64(input));
            return new String(output, "UTF-8");
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String beforeName = "Ke3++Ec+pfqjfcPRfbQBjQ==";
        String afterName = AesUtils.decrypt(beforeName, "ca7dc22b57fa45a7");
        System.out.println(afterName);
    }
}
