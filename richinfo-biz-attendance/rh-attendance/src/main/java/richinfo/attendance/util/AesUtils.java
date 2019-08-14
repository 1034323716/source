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
        } catch (Exception e) {
            System.out.println(e);
        }
        return new String(output);
    }

    public static void main(String[] args) throws Exception {
//        String s = "y7hj5xgtvGLjAcZxMrkIgbR5zv4Rc0gPt2gpcEtigsuvtK1HzGoINhd65BLH4+LRwY49jS1AyodkZCh7o3Fk+MR14mQZv5aiYoJ+rAnE8fpIXNxYWtbaB0HAe8HkDBjOx0K3ga4mU0xVxPYakvtytiQ5Nb7gS31fzpUlT+OhRleIpXLwHXVRW6qZbO0jhgU+56nd/ktpgBqbVB58pSKS/6ddB7iiToeUOfknDBrSi2eQmv2HhK9532PwVqotE2b57+S1a+7RzqCVtPDBBeHTwxNRPlm4IL16ifynLRlVyyzvAc4rRXtX3BcWds5D/wymVTf76ZNYMA0J5itXYWoG/3r592d227GDNatwFZMQH23QzJMMlEwxFQKjCYiE6WkLBj+hYE2hfWrNK/EFmoa1W7N8HVHOlSE5TobQ4FheQkkPMJxC7YC9vKtoK9mJBmV2uhRvfD+n+1gkpRCXjXlnRqRqPdFReENS4CiYNSf5b6TMY6zT23ydFxz8MtZM58+i1fGXbCpzfnt2UWp09JIEp3nxrcc8BqDgvmrSQ4KiLfgisG7wxUC/Nr9ANRZlsi38H300BWrFNt/HZsEH17xpynT0wNRDoxWi7aefqXMrMCE5R5IhbGWBbXdy0S5pJSZuDXS8zzhwpCF/RMlg7NkfESmc2nE+5saUbMv9tGeRy91Xuntr5cqZjRbnM9ijXEqVD/MqlLT2RVoEvEXk/pFVdgii+ELv0mVuQZrpRwT1LK8=";
//        String de = "PKx89raPBGNkLCkWHdnmng==";
//        String s1 = decrypt(de, "258e5059518c4f56");
//        System.out.println(s1);
//        s1 = s1.substring(1,s1.length()-1);
//        System.out.println(s1);
//        Map maps = (Map) JSON.parseObject(s1,Map.class);
//        System.out.println(s1);
//        System.out.println(maps.get("id"));
//        System.out.println(maps.get("name"));
        HashMap<String, String> map = new HashMap<>();
        map.put("ff","asd");
//        String jsonObject = JSON.toJSONString(map);
//        System.out.println(jsonObject);
//
//        Map maps = (Map)JSON.parse(jsonObject);
//        System.out.println(maps.get("ff"));
//        String signature = null;
//        Map<String, String> sortedParams = new TreeMap<String, String>(map);
//        Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();
//        StringBuilder basestring = new StringBuilder();
//        for (Map.Entry<String, String> param : entrys) {
//            basestring.append(param.getKey())
//                .append("=")
//                .append(param.getValue() == null ? "" : param.getValue());
//        }
//        basestring.append("zxc");
//        try {
//            signature = DigestUtils.shaHex(basestring.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(signature);
//        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong))
        long st = 132458l;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String str = "10:08";
        Date parse = dateFormat.parse(str);
//        String time = String.valueOf(st);
        System.out.println(parse.getTime());
//        System.out.println(s.format(new Date(st)));
    }
}
