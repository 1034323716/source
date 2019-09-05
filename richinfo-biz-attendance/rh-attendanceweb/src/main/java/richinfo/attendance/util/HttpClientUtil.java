package richinfo.attendance.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * created by cxming on 2019/9/5
 */
public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    public static CloseableHttpClient createSSLClient(){
        try {
            SSLContext sslContext=new SSLContextBuilder().loadTrustMaterial(
                null,new TrustStrategy() {
                    //信任所有
                    public boolean isTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                        return true;
                    }
                }).build();
            SSLConnectionSocketFactory sslsf=new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String executePost(String postUrl, String body) throws Exception{
        CloseableHttpClient httpClient = HttpClientUtil.createSSLClient();
        CloseableHttpResponse response = null;
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(postUrl);

        //json方式
        httpPost.addHeader("Content-type","application/json");
        //设置参数到请求对象中
        httpPost.setEntity(new StringEntity(body, "UTF-8"));

        //默认键值对模式
//        List<NameValuePair> nvps = new ArrayList<>();
//        nvps.add(new BasicNameValuePair("abc", body));
//        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        try {
            response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {    //请求成功
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                return content;
            } else {
                logger.info("请求不成功，code：" + code);
            }
        } catch (Exception e) {
            logger.error("post请求出错", e);
        }finally {
            if (response != null) {
                response.close();
            }
            httpClient.close();
        }
        return null;
    }
}
