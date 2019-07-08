/**
 * 文件名：RcsMsgUtil.java
 * 创建日期： 2018年1月11日
 * 作者：     liyongde
 * Copyright (c) 2009-2011 个邮开发室
 * All rights reserved.
 
 * 修改记录：
 *  1.修改时间：2018年1月11日
 *   修改人：liyongde
 *   修改内容：
 */
package richinfo.attendance.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.entity.HistoryMessage;
import richinfo.attendance.entity.Message;
import richinfo.attendance.util.*;
import richinfo.bcomponet.cache.CachedUtil;
import richinfo.bcomponet.cache.comm.CacheKey;
import richinfo.tools.io.StreamUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 功能描述：和飞信消息封装
 *
 */
public class RcsMsgUtil
{
    private static final Logger log = LoggerFactory.getLogger(RcsMsgUtil.class);

    private static AttendanceConfig conf = AttendanceConfig.getInstance();

    /**
     * 调用创新平台token申请接口
     * @param appId
     * @param consumerKey
     * @param consumerSecret
     * @param cguid
     * @return
     */
    public static String getAccessToken(String appId, String consumerKey,
        String consumerSecret, String cguid)
    {
        String accessToken = null;
        try
        {
            long start = System.currentTimeMillis();

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("appKey", appId);
            params.put("appSecret", conf.getRcsAppSecret());
            params.put("sdkFrom", "java");
            params.put("requestId", getRequestId());
            params.put("version", "1.0");
            params.put("consumerKey", consumerKey);

            String signature = RcsoaplusSignHelper.genSig(params,
                consumerSecret);

            String urlData = RcsoaplusSignHelper.concatParams(params);

            urlData += "&" + "signature" + "=" + signature;
            String requestUrl = conf.getRcsMsgAccessTokenUrl() + "?" + urlData;

            String res = httpPost(requestUrl, null, null, null);
            boolean flag = false;
            // 处理返回结果
            if (AssertUtil.isNotEmpty(res))
            {
                RcsTokenRes rscTokenRes = JsonUtil.jsonToBean(res,
                    RcsTokenRes.class);
                if (AssertUtil.isNotEmpty(rscTokenRes)
                    && 200 == rscTokenRes.getCode()
                    && AssertUtil.isNotEmpty(rscTokenRes.getData()))
                {
                    accessToken = rscTokenRes.getData().getAccessToken();
                    flag = true;
                    // 存储和飞信行业消息凭证
                    saveRcsMsgAccessToken(appId, accessToken);
                }
            }

            log.info(
                "getAccessToken success={}|cguid={}|useTime={}|accessToken={}|requestUrl={}|result={}",
                flag, cguid, (System.currentTimeMillis() - start), accessToken,
                requestUrl, res);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("getAccessToken error. cguid={}|{}", cguid, e);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("getAccessToken error. cguid={}|{}", cguid, e);
        }
        catch (Exception e)
        {
            log.error("getAccessToken error. cguid={}|{}", cguid, e);
        }

        return accessToken;
    }

    private static String getRequestId()
    {
        return String.valueOf(System.currentTimeMillis())
            + RandomUtil.randOfNumber(3);
    }

    /**
     * 调用创新平台消息推送接口
     * @param msg
     * @param consumerKey
     * @param consumerSecret
     * @param token
     * @param cguid
     * @return
     */
    public static HistoryMessage sendMsg(Message msg, String consumerKey,
        String consumerSecret, String token, String cguid)
    {
        HistoryMessage historyMessage = null;
        boolean flag = false;
        String requestId = getRequestId();
        try
        {
            long start = System.currentTimeMillis();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("consumerKey", consumerKey);
            params.put("requestId", requestId);
            params.put("version", "1.0");

            params.put("appKey", msg.getAppId());
            params.put("accessToken", token);
            params.put("senderAddress", conf.getRcsAccessNo());

            // comeFrom 为1表示，通过点击考勤打卡提醒消息跳转过来的
            String thumbLinkUrl = conf.getRcsMsgDetailsPageUrl() + "?"
                + "EUserID=" + msg.getUid() + "&enterId=" + msg.getEnterId()
                + "&isComeFrom=1";
            int msgType = msg.getMsgType();
            if (msgType == Constants.MsgType.authorization.getValue()
                    || msgType == Constants.MsgType.authorizationPass.getValue()
                    ||  msgType == Constants.MsgType.authorizationReject.getValue()){
                thumbLinkUrl = thumbLinkUrl+"&appealId="+msg.getAppealId();
            }else if (msgType == Constants.MsgType.daily.getValue()){
                thumbLinkUrl = thumbLinkUrl+"&dailyDate="+ TimeUtil.formatDateTime(new Date(),TimeUtil.BASE_DATE_FORMAT);
            }
            log.info("信息推送==== 类型msgType={}| thumbLinkUrl={}",msg,thumbLinkUrl);
            String thumbLink = URLEncoder.encode(thumbLinkUrl,
                Constants.ENCODING_UTF8);
            params.put("thumbLink", thumbLink);
            params.put("sdkFrom", "java");
            params.put("enterpriseId", msg.getEnterId());
            params.put("euserIds", msg.getUid());

            String urlData = RcsoaplusSignHelper.concatParams(params);

            String title = msg.getTopic();
            String msgPullType = conf.getMsgPullType();
            title = msgPullType + title;
            String text = msg.getContent();
            String summary = msg.getSummary();
            params.put("title", title);
            params.put("text", "");
            params.put("summary", summary);

            title = URLEncoder.encode(title, Constants.ENCODING_UTF8);
            text = URLEncoder.encode(text, Constants.ENCODING_UTF8);
            summary = URLEncoder.encode(summary, Constants.ENCODING_UTF8);

            String signature = RcsoaplusSignHelper.genSig(params,
                consumerSecret);

            urlData += "&" + "signature" + "=" + signature + "&title=" + title
                + "&text=" + "" + "&summary=" + summary;

            String requestUrl = conf.getRcsMsgSendUrl() + "?" + urlData;

            String sendres = httpPost(requestUrl, null, null, null);

            // 处理返回结果
            if (AssertUtil.isNotEmpty(sendres))
            {
                RcsBaseRes rscBaseRes = JsonUtil.jsonToBean(sendres,
                    RcsBaseRes.class);
                if (AssertUtil.isNotEmpty(rscBaseRes)
                    && 200 == rscBaseRes.getCode())
                {
                    flag = true;
                }
                historyMessage = new HistoryMessage(msg,
                    String.valueOf(rscBaseRes != null ? rscBaseRes.getCode()
                        : ""), requestId, flag);
            }

            log.info(
                "sendMsg success={}|cguid={}|useTime={}|requestUrl={}|result={}|{}",
                flag, cguid, (System.currentTimeMillis() - start), requestUrl,
                sendres, msg);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("sendMsg error. cguid={}|{}", cguid, e);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("sendMsg error. cguid={}|{}", cguid, e);
        }
        catch (Exception e)
        {
            log.error("sendMsg error. cguid={}|{}", cguid, e);
        }

        if (AssertUtil.isEmpty(historyMessage))
        {
            historyMessage = new HistoryMessage(msg, "", requestId, false);
        }
        return historyMessage;
    }

    /**
     * HTTP请求AppSvr模块，返回Json String ，增加 传送cookie字段
     * 
     * @param requestUrl
     * @param
     * @param
     * @param cookie
     * @return
     */
    public static String httpPost(String requestUrl, byte[] data,
        String cookie, Map<String, String> head)
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
            if (AssertUtil.isNotEmpty(data))
            {
                httpconn.setRequestProperty("Content-Length", "" + data.length);
            }
            httpconn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
            // 设置HTTP请求头
            if (!AssertUtil.isNull(head))
            {
                java.util.Set<String> set = head.keySet();
                for (String headKey : set)
                {
                    httpconn.setRequestProperty(headKey, head.get(headKey));
                    log.info(head.get(headKey));
                }
            }
            // 如果有Cookie，Cookie带过去
            if (!AssertUtil.isEmpty(cookie))
            {
                httpconn.setRequestProperty("Cookie", cookie);
            }

            outPs = httpconn.getOutputStream();
            if (AssertUtil.isNotEmpty(data))
            {
                outPs.write(data, 0, data.length);
            }
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
                bytes = StreamUtil.getByteByStream(inPs);
            }

            if (bytes != null)
            {
                result = new String(bytes, Constants.ENCODING_UTF8);
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
            StreamUtil.close(inPs);
            StreamUtil.close(outPs);
            StreamUtil.close(httpconn);
        }
        return result;
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

            StreamUtil.close(out);
            StreamUtil.close(gin);
        }
    }

    /**
     * Attendance使用和飞信行业消息凭证缓存key
     * @param
     * @return
     */
    private static String getRcsMsgAccessTokenCachekey(String appId)
    {
        String skey = String.format(
            CacheKey.Attendance.ATTENDANCE_RCSMSG_ACCESSTOKEN, appId);
        return skey;
    }

    /**
     * 获取和飞信行业消息凭证
     * @param appId
     * @return
     */
    public static String getRcsMsgAccessTokenFromCache(String appId)
    {
        String key = getRcsMsgAccessTokenCachekey(appId);
        return (String) CachedUtil.get(key);
    }

    /**
     * 存储和飞信行业消息凭证
     * @param appId
     * @param accessToken
     */
    public static void saveRcsMsgAccessToken(String appId, String accessToken)
    {
        String key = getRcsMsgAccessTokenCachekey(appId);
        long timeout = conf.getRcsMsgAccessTokenCacheTime();
        CachedUtil.set(key, accessToken, timeout);
    }

}