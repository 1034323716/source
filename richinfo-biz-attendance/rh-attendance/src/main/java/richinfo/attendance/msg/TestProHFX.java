package richinfo.attendance.msg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.JsonUtil;

public class TestProHFX
{

    private static final Logger log = Logger.getLogger(TestProHFX.class);

    public static void main(String[] args) throws UnsupportedEncodingException
    {

        String consumerSecret = "0d324f614e88ebb4ab6c6b87b8f7cc0a";
        String accessToken = getAccessToken(consumerSecret);
        System.out.println(accessToken);

        if (AssertUtil.isNotEmpty(accessToken))
        {
            try
            {
                boolean flag = sendMsg(accessToken, consumerSecret);
                System.out.println("sendMsg=" + flag);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }

    }

    private static boolean sendMsg(String token, String consumerSecret)
        throws UnsupportedEncodingException
    {
        boolean flag = false;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("consumerKey", "101");
        long requestId = System.currentTimeMillis();
        String requestIdStr = String.valueOf(requestId) + "123";
        params.put("requestId", requestIdStr);
        params.put("version", "1.0");

        params.put("appKey", "92");
        params.put("accessToken", token);
        params.put("senderAddress", "125600400000053");

        params
            .put(
                "thumbLink",
                URLEncoder
                    .encode(
                    // "http://121.15.167.235:10721/atdc/rcs/rcs_api_demo.html?EUserID=2AAEC46B6D589A15B6996A85F1EBB6DF&enterId=483460&comeFrom=1",
                    // "http://121.15.167.235:10721/atdc/rcs/rcs_api_demo.html?EUserID=4163584018902323A33A507593C12709&enterId=36301&comeFrom=1",
//                        "http://121.15.167.235:10721/atdc/rcs/static/rcs_api_demo.html?EUserID=4163584018902323A33A507593C12709&enterId=36301&comeFrom=1",
                        // 跳转到单点登录接口（单点登录接口会重定向到打卡页面）
                        // "http://121.15.167.235:10721/atdc/login/ssoAttendance?EUserID=4163584018902323A33A507593C12709&enterId=36301&comeFrom=1",
                      "http://120.197.235.114/atdc/login/ssoAttendance?EUserID=B8E85240953336E1B42DEA0D550C6F02&enterId=36301&comeFrom=1",
                        "UTF-8"));
        params.put("sdkFrom", "java");

        // 李永德
        params.put("enterpriseId", "36301");
        params.put("euserIds", "B8E85240953336E1B42DEA0D550C6F02");
        
        // 周一江
//        params.put("enterpriseId", "36301");
//        params.put("euserIds", "FCD248E0C5E5E871878C26E057A5E9C8");
        
        // 王进
//        params.put("enterpriseId", "36301");
//        params.put("euserIds", "6246E94D1F04319A76160E4E8CB8BFF3");
        
//        params.put("enterpriseId", "483460");
//        params.put("euserIds", "E44F41D646FBE2F59083919E9D22B480");
              
        // 唐兴均           
//        params.put("enterpriseId", "483460");
//        params.put("euserIds", "2AAEC46B6D589A15B6996A85F1EBB6DF");

        String dataUrl = RcsoaplusSignHelper.concatParams(params);

        String title = "考勤打卡提醒2_27_3-test";
        String text = "您的打卡时间马上到了，请及时打卡2_27_3-test";
        String summary = "打卡提醒2_27_3-test";
        params.put("title", title);
        params.put("text", text);
        params.put("summary", summary);

        title = URLEncoder.encode(title, "UTF-8");
        text = URLEncoder.encode(text, "UTF-8");
        summary = URLEncoder.encode(summary, "UTF-8");

        String signature = null;
        try
        {
            signature = RcsoaplusSignHelper.genSig(params, consumerSecret);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        dataUrl += "&" + "signature" + "=" + signature + "&title=" + title
            + "&text=" + text + "&summary=" + summary;

        String requestUrl = "http://120.196.212.5/rcsoaplus-gateway/message/sendByEuserId?"
        // 压测地址
//        String requestUrl = "http://120.196.212.5/rcsoaplus-gateway/test/sendByEuserId?"
            + dataUrl;

        System.out.println("requestUrl : " + requestUrl);
        long start = System.currentTimeMillis();
        String sendmres = httpPost(true, requestUrl, null, null, null);
        System.out.println("send useTime = "
            + (System.currentTimeMillis() - start));
        System.out.println("sendmres : " + sendmres);

        if (AssertUtil.isNotEmpty(sendmres))
        {
            RcsBaseRes rscBaseRes = JsonUtil.jsonToBean(sendmres,
                RcsBaseRes.class);
            if (AssertUtil.isNotEmpty(rscBaseRes)
                && 200 == rscBaseRes.getCode())
            {
                flag = true;
            }
        }

        return flag;
    }

    private static String getAccessToken(String consumerSecret)
        throws UnsupportedEncodingException
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appKey", "92");
        params.put("appSecret", "k5x369ato282pl8XfEUh8NQHNu7k549N");
        params.put("sdkFrom", "java");
        long requestId = System.currentTimeMillis();
        String requestIdStr = String.valueOf(requestId) + "123";
        params.put("requestId", requestIdStr);
        params.put("version", "1.0");
        params.put("consumerKey", "101");

        String signature = null;
        try
        {
            signature = RcsoaplusSignHelper.genSig(params, consumerSecret);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        String dataUrl = RcsoaplusSignHelper.concatParams(params);
        dataUrl += "&" + "signature" + "=" + signature;

        String requestUrl = "http://120.196.212.5/rcsoaplus-gateway/message/token?"
            + dataUrl;

        long start = System.currentTimeMillis();
        String res = httpPost(true, requestUrl, null, null, null);
        System.out.println("getAccessToken requestUrl = " + requestUrl);
        System.out.println("getAccessToken useTime = "
            + (System.currentTimeMillis() - start));
        System.out.println(res);
        String accessToken = null;
        if (AssertUtil.isNotEmpty(res))
        {
            RcsTokenRes rscTokenRes = JsonUtil.jsonToBean(res,
                RcsTokenRes.class);
            if (AssertUtil.isNotEmpty(rscTokenRes)
                && 200 == rscTokenRes.getCode()
                && AssertUtil.isNotEmpty(rscTokenRes.getData()))
            {
                accessToken = rscTokenRes.getData().getAccessToken();
            }
        }
        return accessToken;
    }

    /**
     * HTTP请求AppSvr模块，返回Json String ，增加 传送cookie字段
     * 
     * @param requestUrl
     * @param postDate
     * @param remoteIp
     * @param cookie
     * @return
     */
    public static String httpPost(boolean isXml, String requestUrl,
        byte[] data, String cookie, Map<String, String> head)
    {
        HttpURLConnection httpconn = null;
        OutputStream outPs = null;
        InputStream inPs = null;
        int responseCode = -1;
        String result = null;
        try
        {
            URL url = new URL(requestUrl);
            httpconn = (HttpURLConnection) url.openConnection();
            httpconn.setConnectTimeout(Constants.HTTP_CONN_TIMEOUT);
            httpconn.setReadTimeout(Constants.HTTP_READ_TIMEOUT);
            httpconn.setDoInput(true);
            httpconn.setDoOutput(true);
            httpconn.setRequestMethod("POST");
            // httpconn.setRequestProperty("Content-Length", "" + data.length);
            if (isXml)
            {
                httpconn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            }
            // 设置HTTP请求头
            if (!AssertUtil.isNull(head))
            {
                java.util.Set<String> set = head.keySet();
                for (String headKey : set)
                {
                    httpconn.setRequestProperty(headKey, head.get(headKey));
                }
            }
            // 如果有Cookie，Cookie带过去
            if (!AssertUtil.isEmpty(cookie))
            {
                httpconn.setRequestProperty("Cookie", cookie);
            }

            outPs = httpconn.getOutputStream();
            // outPs.write(data, 0, data.length);
            outPs.flush();
            inPs = httpconn.getInputStream();

            responseCode = httpconn.getResponseCode();
            String encoding = httpconn.getHeaderField("Content-Encoding");

            byte[] bytes = null;
            // 读取post之后的返回值,Stream转化为字符串
            // 如果是Content-Encoding=gzip需要用解压方式
            if (!AssertUtil.isEmpty(encoding)
                && encoding.equalsIgnoreCase("gzip"))
            {
                bytes = unzip(inPs);
            }
            else
            {
                // 直接读取输入流转化为字符串
                bytes = getByteByStream(inPs);
            }

            if (bytes != null)
            {
                result = new String(bytes, "utf-8");
                bytes = null;
            }

            return result;
        }
        catch (Exception e)
        {
            log.error("requrl=" + requestUrl + " | Exception", e);
            if (responseCode != 200)
            {
                log.error("Http responseCode=" + responseCode);
            }
        }
        finally
        {
            try
            {
                if (inPs != null)
                {
                    inPs.close();
                    inPs = null;
                }
            }
            catch (IOException e)
            {
                log.error("requrl=" + requestUrl + " | Exception", e);
            }

            try
            {
                if (outPs != null)
                {
                    outPs.close();
                    outPs = null;
                }
            }
            catch (IOException e)
            {
                log.error("requrl=" + requestUrl + " | Exception", e);
            }

            httpconn.disconnect();
            httpconn = null;
        }
        return result;
    }

    /**
     * 普通输入流转化为字节数组
     * 
     * @param is
     * @return
     */
    private static byte[] getByteByStream(InputStream is)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            byte[] buf = new byte[1024];
            int num;
            while ((num = is.read(buf, 0, buf.length)) != -1)
            {
                out.write(buf, 0, num);
            }
            out.close();
            return out.toByteArray();
        }
        catch (IOException e)
        {
            return out.toByteArray();
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                    out = null;
                }
            }
            catch (IOException e)
            {
                log.error("Exception", e);
            }
        }
    }

    /**
     * GZIPInputStream 解压
     * 
     * @param in
     * @return
     */
    private static byte[] unzip(InputStream in)
    {
        GZIPInputStream gin = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            gin = new GZIPInputStream(in);
            byte[] buf = new byte[1024];
            int num;
            while ((num = gin.read(buf, 0, buf.length)) != -1)
            {
                out.write(buf, 0, num);
            }
            return out.toByteArray();
        }
        catch (IOException e)
        {
            return out.toByteArray();
        }
        finally
        {

            try
            {
                if (out != null)
                {
                    out.close();
                    out = null;
                }
            }
            catch (IOException e)
            {
                log.error("Exception", e);
            }

            try
            {
                if (gin != null)
                {
                    gin.close();
                    gin = null;
                }
            }
            catch (IOException e)
            {
                log.error("Exception", e);
            }

        }
    }
}
