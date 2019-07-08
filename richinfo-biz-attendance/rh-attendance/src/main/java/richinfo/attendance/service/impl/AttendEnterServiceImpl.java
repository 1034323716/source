package richinfo.attendance.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import richinfo.attendance.bean.RegisterCallBackResBean;
import richinfo.attendance.dao.AttendGroupDao;
import richinfo.attendance.dao.EnterDao;
import richinfo.attendance.service.AttendEnterService;
import richinfo.attendance.util.AssertUtil;
import richinfo.attendance.util.QytxlUtil;

import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by qiang on 2018/10/15.
 * 企业注册通讯录模块
 */
public class AttendEnterServiceImpl implements AttendEnterService{
    private Logger logger = LoggerFactory.getLogger(AttendEnterServiceImpl.class);
    private EnterDao enterDao = new EnterDao();
    private AttendGroupDao attendGroupDao = new AttendGroupDao();
    /**
     * 获取企业注册通讯录回调信息
     * @return
     */
    @Override
    public List<List> getCallBack(String enter,String pageNo,String pageCount,String eventType) {
        List<List> callBackList = new ArrayList<>();
        if (StringUtils.isNotBlank(enter)){
            List<Map<String,Object>> registerList = callBackMsg(enter,eventType);
            callBackList.add(registerList);
        }/*else {
            pageNo = pageNo == null ? "0": pageNo;
            pageCount = pageCount == null ? "5": pageCount;
            List<String> enters = enterDao.queryEnterCallBack(1,Integer.parseInt(pageNo),Integer.parseInt(pageCount));
            logger.info("queryEnterCallBack data enters={}",enters);
            if (AssertUtil.isEmpty(enters)) {
                return null;
            }
            for (String enterpriseId : enters) {
                List<Map<String,Object>> registerList = callBackMsg(enterpriseId,eventType);
                callBackList.add(registerList);
            }
        }*/
        return callBackList;
    }

    private List<Map<String,Object>> callBackMsg(String enterpriseId,String eventType) {
        Map<String, Object> callBackMap = new HashMap<>();
        try {
            callBackMap = QytxlUtil.getInstance().getCallBack(enterpriseId);
        } catch (Exception e) {
            logger.error("getCallBack 一次失败 enter={}|e={}",enterpriseId,e);
            try {
                callBackMap = QytxlUtil.getInstance().getCallBack(enterpriseId);
            } catch (Exception e1) {
                e1.printStackTrace();
                logger.error("getCallBack 二次失败 enter={}|e={}",enterpriseId,e);
            }
        }
        if (!callBackMap.isEmpty()){
            if ((int)callBackMap.get("error_code") == 0 && callBackMap.get("registerList") != null){
                List<Map<String,Object>> registerList = (List<Map<String,Object>>)callBackMap.get("registerList");
                if (StringUtils.isBlank(eventType)){
                    return registerList;
                }
                List<Map<String,Object>> callBackMsgList = new ArrayList<>();
                for (Map<String,Object> map :registerList){
                    if (eventType.equals(String.valueOf(map.get("eventType")))){
                        callBackMsgList.add(map);

                    }
                }
            }
        }
        return null;
    }

    /**
     * 更新企业回调信息
     * @param enter
     * @param eventType
     * @return
     */
    @Override
    public RegisterCallBackResBean updateCallBack(String enter,String method, String eventType) {
        try {
            return QytxlUtil.getInstance().registerCallBack(enter,method,eventType);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除数据库登记的注册回调
     * @return
     */
    @Override
    public Map delRegister() {
        //查询注册的登记
        List<String> enters = enterDao.queryEnterCallBack(1);
        //获取正在使用考勤组的企业
        List<String>groupEnters = attendGroupDao.queryGroupEnter();
        enters.addAll(groupEnters);
        List<String> listNew=new ArrayList<>(new TreeSet<String>(enters));
        logger.info("==================listNew={}",listNew);
        Map<String,String>delFailureEnter = new HashMap<>();

        RegisterCallBackResBean registerCallBackResBean = null;
        Map<String, Object> callBack = new HashMap<>();
        for (String enterId : listNew) {
            try {
                 callBack = QytxlUtil.getInstance().getCallBack(enterId);
            } catch (Exception e) {
                logger.error("getCallBack create event_user_remove 一次失败 enterId={}|e={}",enterId,e);
                try {
                    callBack = QytxlUtil.getInstance().getCallBack(enterId);
                } catch (Exception e1) {
                    logger.error("getCallBack create event_user_remove 二次失败 enterId={}|e1={}",enterId,e1);
                }
            }
            if (callBack.isEmpty() || (int)callBack.get("error_code") != 0 || AssertUtil.isEmpty(callBack.get("registerList"))){
                continue;
            }
            List<Map<String,Object>> mapList = (List<Map<String,Object>>)callBack.get("registerList");
            for (Map map : mapList){
                if ("event_user_remove".equals(String.valueOf(map.get("eventType")))|| "event_dept_remove".equals(String.valueOf(map.get("eventType")))){

                    String enterpriseId = String.valueOf(map.get("enterpriseId")) ;
                    String url = String.valueOf(map.get("url"));
                    logger.info("deleteCallBackByUrl =================== enterpriseId={}|url={}",enterpriseId,url);
                    try {
                        registerCallBackResBean = QytxlUtil.getInstance().deleteCallBackByUrl(enterpriseId, url, (String) map.get("eventType"));
                    } catch (NoSuchAlgorithmException e) {
                        logger.error("deleteCallBackByUrl delete event_user_remove 一次失败 enterId={}|e={}",enterId,e);
                        try {
                            registerCallBackResBean = QytxlUtil.getInstance().deleteCallBackByUrl(enterpriseId, url, (String) map.get("eventType"));
                        } catch (NoSuchAlgorithmException e1) {
                            logger.error("deleteCallBackByUrl delete event_user_remove 一次失败 enterId={}|e1={}",enterId,e1);
                            delFailureEnter.put(enterpriseId,"请求不通");
                        }
                    }
                    if (!"0".equals(registerCallBackResBean.getError_code())){
                        delFailureEnter.put(enterpriseId,registerCallBackResBean.getError_msg());
                    }
                }
            }

        }
        enterDao.deleteEnter();
        return delFailureEnter;
    }

    /***
     * 重新注册回调接口
     */
    @Override
    public Map anewRegister() {
        //获取正在使用考勤组的企业
        List<String>groupEnters = attendGroupDao.queryGroupEnter();
        Map<String,String>creUserFailureEnter = new HashMap<>();
        Map<String,String>creDeptFailureEnter = new HashMap<>();
        Map<String,Object>map = new HashMap<>();
        RegisterCallBackResBean registerCallBackResBean = null;
        //重新注册人员删除回调与 部门删除回调
        for(String enterId : groupEnters){
            try {
                registerCallBackResBean = QytxlUtil.getInstance().registerCallBack(enterId, "create", "event_user_modify");
            } catch (NoSuchAlgorithmException e) {
                logger.error("registerCallBack create event_user_modify 一次失败 enterId={}|e={}",enterId,e);
                try {
                    registerCallBackResBean =  QytxlUtil.getInstance().registerCallBack(enterId, "create", "event_user_modify");
                } catch (NoSuchAlgorithmException e1) {
                    logger.error("registerCallBack create event_user_modify 二次失败 enterId={}|e={}",enterId,e1);
                    creUserFailureEnter.put(enterId,"request error");
                }
            }
            if (!registerCallBackResBean.getError_code().equals("0")){
                creUserFailureEnter.put(enterId,registerCallBackResBean.getError_msg());
            }else {
                enterDao.insertEnterStatus(enterId, 1, "event_user_remove");
                try {
                    registerCallBackResBean = QytxlUtil.getInstance().registerCallBack(enterId, "create", "event_dept_remove");
                } catch (NoSuchAlgorithmException e) {
                    logger.error("registerCallBack create event_dept_remove 一次失败 enterId={}|e={}",enterId,e);
                    try {
                        registerCallBackResBean =  QytxlUtil.getInstance().registerCallBack(enterId, "create", "event_dept_remove");
                    } catch (NoSuchAlgorithmException e1) {
                        logger.error("registerCallBack create event_dept_remove 二次失败 enterId={}|e={}",enterId,e1);
                        creDeptFailureEnter.put(enterId,"request error");
                    }
                }
                if (!registerCallBackResBean.getError_code().equals("0")){
                    creDeptFailureEnter.put(enterId,registerCallBackResBean.getError_msg());
                }else {
                    enterDao.insertEnterStatus(enterId, 1, "event_dept_remove");
                }
            }

        }
        map.put("creUserFailureEnter",creUserFailureEnter);
        map.put("creDeptFailureEnter",creDeptFailureEnter);

        return map;
    }


    /**
     * 校验删除企业
     * @return
     */
    @Override
    public List checkEnter() {
        //获取考勤企业
        List<String> enterList = attendGroupDao.queryAttendEnter();
       // logger.info("queryAttendEnter query data enterList = {}",enterList);
        List<Object> resList = new ArrayList<>();
        for (String enterId : enterList){
            Map<String, Object> entInfo = null;
            try {
                entInfo = QytxlUtil.getInstance().getEntInfo(enterId);
            } catch (Exception e) {
                try {
                    entInfo = QytxlUtil.getInstance().getEntInfo(enterId);
                } catch (Exception e1) {
                    logger.error("QytxlUtil.getInstance().getEntInfo  2次失败！！！");
                }

            }
            if (AssertUtil.isEmpty(entInfo)){
                continue;
            }

            if ((int)entInfo.get("error_code") == 999 && ("企业"+enterId+"不存在").equals(String.valueOf(entInfo.get("error_msg")))){
                resList.add(entInfo);
                enterDao.delEnterGroup(Arrays.asList(enterId));
            }

        }
        return resList;
    }

    @Override
    public Map checkEnterIsExist() {

        return null;
    }


    /**
     * 注册企业删除回调
     * @return
     */
    @Override
    public List registerEnterCallBack() {
        List<String> enterList = enterDao.queryEnterCallBack(1);
        List<Map<String,Object>>resList = new ArrayList<>();
        for (String enter : enterList){
            Map<String,Object>resMap = null;

            RegisterCallBackResBean registerCallBackResBean = null;
            try {
                registerCallBackResBean = QytxlUtil.getInstance().registerCallBack(enter, "create", "event_user_modify");
                if ( "0".equals(registerCallBackResBean.getError_code())) {
                    int i = enterDao.insertEnterStatus(enter, 1, registerCallBackResBean.getEventType());
                    logger.info("================{}==============",i);
                }else{
                    resMap = new HashMap<>();
                    resMap.put("enterId",enter);
                    resMap.put("errorMsg",registerCallBackResBean.getError_msg());
                    resList.add(resMap);
                }
            } catch (NoSuchAlgorithmException e) {
                try {
                    registerCallBackResBean = QytxlUtil.getInstance().registerCallBack(enter, "create", "event_user_modify");
                    if (AssertUtil.isNotEmpty(registerCallBackResBean) && "0".equals(registerCallBackResBean.getError_code())) {
                        int i = enterDao.insertEnterStatus(enter, 1, registerCallBackResBean.getEventType());
                        logger.info("================{}==============",i);
                    }else{
                        resMap = new HashMap<>();
                        resMap.put("enterId",enter);
                        resMap.put("errorMsg",registerCallBackResBean.getError_msg());
                        resList.add(resMap);
                    }
                } catch (NoSuchAlgorithmException e1) {

                }
            }
        }
        return resList;
    }
}
