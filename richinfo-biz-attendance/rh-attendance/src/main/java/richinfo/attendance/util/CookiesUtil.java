package richinfo.attendance.util;



import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiang on 2018/7/25.
 */
public class CookiesUtil {


    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Map<String, Object> cookieMap = ReadCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = (Cookie) cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }
    /**
     * 获取cookies
     * @param request
     * @return
     */
    public static Map<String, Object> ReadCookieMap(HttpServletRequest request) {
        Map<String, Object> cookieMap = new HashMap<String, Object>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
}
