package richinfo.attendance.service;

import richinfo.attendance.bean.RegisterCallBackResBean;

import java.util.List;
import java.util.Map;

/**
 * Created by qiang on 2018/10/15.
 * 企业注册通讯录
 */
public interface AttendEnterService {
    //获取企业注册回调信息
    List<List> getCallBack(String enter,String pageNo,String pageCount,String eventType);

    /**
     * 更新企业回调信息
     * @param enter
     * @param eventType
     * @return
     */
    RegisterCallBackResBean updateCallBack(String enter, String method ,String eventType);

    /**
     * 删除数据库登记注册回调
     * @return
     */
    Map delRegister();

    /***
     * 重新注册回调接口
     */
    Map anewRegister();

    /**
     * 校验删除的企业
     * @return
     */
    List checkEnter();

    /**
     * 通过企业ID+应用链接查询企业是否存在该应用接口
     * @return
     */
    Map checkEnterIsExist();

    /**
     * 注册企业删除回调
     * @return
     */
    List registerEnterCallBack();
}
