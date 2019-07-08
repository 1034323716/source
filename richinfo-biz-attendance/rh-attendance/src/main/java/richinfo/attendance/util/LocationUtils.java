package richinfo.attendance.util;

import com.alibaba.fastjson.JSON;
import org.apache.poi.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
/**
 * Created by qiang on 2018/11/2.
 * 经纬度之间距离计算
 *
 */
public class LocationUtils {


    /**
     * 调用高德地图就算两点的距离
     * @param
     * @param
     * @return
     *  //latitude=23.182619||longitude=113.409265||latitude2=23.182619||longitude2=113.409265
     */
    public static Map<String, Object> getDistanceByPoints(String latitude,String longitude, String latitude2 ,String longitude2){


        String url =   String.format("https://restapi.amap.com/v3/distance?origins=" + Double.parseDouble("113.367842")+
            "," + Double.parseDouble("23.172171")+
            "&destination="+Double.parseDouble("113.407096")+
            "," + Double.parseDouble("23.181689")+"&type=0"+
            "&key=cfab19aacf617fa1fcb1fe20ab03ab10");

        String baiURL = String.format("http://api.map.baidu.com/routematrix/v2/driving?"+
            "output=json&origins=" + Double.parseDouble("23.172171")+
            "," + Double.parseDouble("113.367842")+
            "&destinations="+Double.parseDouble("23.181689")+
            "," + Double.parseDouble("113.407094")+
            "&ak=MmABC3Ed8w7jhutRsrc9t06ki2qXiNGW");

        String json = loadJSON(url);
        String baiURLson = loadJSON(baiURL);

        //将返回结果转换成map
        Map<String, Object> reMap = JSON.parseObject(json);
        Map<String, Object> baiURLsonMap = JSON.parseObject(baiURLson);

       System.out.println(reMap);
       System.out.println(baiURLsonMap);
        return reMap;
    }


  /**
     * @Title: loadJSON
     * @Description: 得到请求返回结果
     * @param url 百度API请求地址
     * @return 返回String雷锤
     */
    public static String loadJSON (String url) {   
              StringBuilder json = new StringBuilder();   
              try {   
                   URL oracle = new URL(url);
                   URLConnection yc = oracle.openConnection();
                   BufferedReader in = new BufferedReader(new InputStreamReader(
                                                                       yc.getInputStream()));   
                   String inputLine = null;   
                   while ( (inputLine = in.readLine()) != null) {   
                        json.append(inputLine);   
                   }   
                   in.close();   
              } catch (MalformedURLException e) {
             e.printStackTrace();
              } catch (IOException e) {
             e.printStackTrace();
              }   
              return json.toString();   
        }

    //latitude=23.182619||longitude=113.409265||latitude2=23.182619||longitude2=113.409265
    //latitude=23.182619||longitude=113.409265||latitude2=23.120581||longitude2=113.329784
    public static void main(String[] args) {
        Map<String, Object> distanceByPoints = getDistanceByPoints(null, null,null,null);

    }
}
