package richinfo.attendance.action;



import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import richinfo.attendance.util.HttpClientUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

import static com.alibaba.druid.sql.parser.Token.BY;

/**
 * @author simple
 */
@Controller
@RequestMapping("/transmit")
public class TransmitAction extends BaseAttendanceAction{

    @RequestMapping(value ="/transmitLocation",method = RequestMethod.POST)
    @ResponseBody
    public Object transmitLocation(@RequestBody JSONObject jsonParam) throws Exception{
        logger.debug(jsonParam.toJSONString());
        String url = "https://rcsoa-app1.zone139.com/intelligence-report/report/third/createReport";
        String content = HttpClientUtil.executePost(url, jsonParam.toJSONString());
        return JSONObject.parseObject(content);
    }
}

